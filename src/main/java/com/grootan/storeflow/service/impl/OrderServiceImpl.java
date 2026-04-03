package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.dto.request.OrderItemRequestDto;
import com.grootan.storeflow.dto.request.OrderRequestDto;
import com.grootan.storeflow.dto.request.OrderStatusUpdateRequestDto;
import com.grootan.storeflow.dto.response.OrderResponseDto;
import com.grootan.storeflow.enums.OrderStatus;
import com.grootan.storeflow.exceptions.InsufficientStockException;
import com.grootan.storeflow.exceptions.InvalidStatusTransitionException;
import com.grootan.storeflow.exceptions.ResourceNotFoundException;
import com.grootan.storeflow.mapper.OrderMapper;
import com.grootan.storeflow.models.Order;
import com.grootan.storeflow.models.OrderItem;
import com.grootan.storeflow.models.Product;
import com.grootan.storeflow.models.User;
import com.grootan.storeflow.repository.OrderItemRepository;
import com.grootan.storeflow.repository.OrderRepository;
import com.grootan.storeflow.repository.ProductRepository;
import com.grootan.storeflow.repository.UserRepository;
import com.grootan.storeflow.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    @Override
    @Transactional
    public OrderResponseDto placeOrder(UUID customerId, OrderRequestDto requestDto) {
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + customerId));

        List<Product> products = new ArrayList<>();
        for (OrderItemRequestDto itemDto : requestDto.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemDto.getProductId()));
            if (product.getStock() < itemDto.getQuantity()) {
                throw new InsufficientStockException(product.getSku(), itemDto.getQuantity(), product.getStock());
            }
            products.add(product);
        }

        Order order = new Order();
        order.setReferenceNumber("ORD-" + System.currentTimeMillis());
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setShippingStreet(requestDto.getShippingAddress().getStreet());
        order.setShippingCity(requestDto.getShippingAddress().getCity());
        order.setShippingCountry(requestDto.getShippingAddress().getCountry());
        order.setShippingPostalCode(requestDto.getShippingAddress().getPostalCode());
        
        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (int i = 0; i < requestDto.getItems().size(); i++) {
            OrderItemRequestDto itemDto = requestDto.getItems().get(i);
            Product product = products.get(i);

            product.setStock(product.getStock() - itemDto.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(product.getPrice());
            
            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            orderItem.setSubtotal(subtotal);
            
            items.add(orderItem);
            totalAmount = totalAmount.add(subtotal);
        }

        order.setItems(items);
        order.setTotalAmount(totalAmount);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponseDto> getOrdersForUser(UUID customerId, Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrderStatus(UUID id, OrderStatusUpdateRequestDto requestDto) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(requestDto.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new com.grootan.storeflow.exceptions.AppException("Invalid status provided", org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        
        OrderStatus previousStatus = order.getStatus();
        if (previousStatus == OrderStatus.CANCELLED || previousStatus == OrderStatus.DELIVERED) {
            throw new InvalidStatusTransitionException(previousStatus.name(), newStatus.name());
        }
        
        order.setStatus(newStatus);
        Order savedOrder = orderRepository.save(order);
        
        return orderMapper.toDto(savedOrder);
    }
}
