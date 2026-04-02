package com.grootan.storeflow.service;

import com.grootan.storeflow.dto.request.OrderItemRequestDto;
import com.grootan.storeflow.dto.request.OrderRequestDto;
import com.grootan.storeflow.dto.request.OrderStatusUpdateRequestDto;
import com.grootan.storeflow.dto.request.ShippingAddressDto;
import com.grootan.storeflow.dto.response.OrderResponseDto;
import com.grootan.storeflow.enums.OrderStatus;
import com.grootan.storeflow.exceptions.InsufficientStockException;
import com.grootan.storeflow.exceptions.InvalidStatusTransitionException;
import com.grootan.storeflow.mapper.OrderMapper;
import com.grootan.storeflow.models.Order;
import com.grootan.storeflow.models.Product;
import com.grootan.storeflow.models.User;
import com.grootan.storeflow.repository.OrderItemRepository;
import com.grootan.storeflow.repository.OrderRepository;
import com.grootan.storeflow.repository.ProductRepository;
import com.grootan.storeflow.repository.UserRepository;
import com.grootan.storeflow.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private OrderMapper orderMapper;

    @InjectMocks private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private UUID userId = UUID.randomUUID();
    private UUID productId = UUID.randomUUID();
    private OrderRequestDto orderReq;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);

        product = new Product();
        product.setId(productId);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1000));
        product.setStock(5);

        orderReq = new OrderRequestDto();
        ShippingAddressDto address = new ShippingAddressDto();
        address.setCity("City");
        address.setStreet("Street");
        address.setCountry("Country");
        address.setPostalCode("12345");
        orderReq.setShippingAddress(address);

        OrderItemRequestDto itemReq = new OrderItemRequestDto();
        itemReq.setProductId(productId);
        itemReq.setQuantity(2);
        orderReq.setItems(List.of(itemReq));
    }

    @Test
    void placeOrder_success_deductsStock() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArguments()[0]);
        when(orderMapper.toDto(any(Order.class))).thenReturn(new OrderResponseDto());

        orderService.placeOrder(userId, orderReq);

        assertEquals(3, product.getStock());
        verify(productRepository).save(product);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void placeOrder_insufficientStock_throwsException() {
        orderReq.getItems().get(0).setQuantity(10); // requested 10, stock is 5
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> orderService.placeOrder(userId, orderReq));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void updateStatus_invalidTransition_throwsException() {
        Order order = new Order();
        order.setStatus(OrderStatus.DELIVERED);
        
        when(orderRepository.findById(any())).thenReturn(Optional.of(order));
        
        OrderStatusUpdateRequestDto updateReq = new OrderStatusUpdateRequestDto();
        updateReq.setStatus("PENDING");
        
        assertThrows(InvalidStatusTransitionException.class, () -> orderService.updateOrderStatus(UUID.randomUUID(), updateReq));
    }

}
