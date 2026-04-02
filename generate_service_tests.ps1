$baseDir = "e:\GROOTAN\grootan-spring-boot-exercise-tejash\src\test\java\com\grootan\storeflow\service"

New-Item -ItemType Directory -Force -Path $baseDir

Set-Content -Path "$baseDir\ProductServiceTest.java" -Value @"
package com.grootan.storeflow.service;

import com.grootan.storeflow.dto.request.ProductRequestDto;
import com.grootan.storeflow.dto.response.ProductResponseDto;
import com.grootan.storeflow.enums.ProductStatus;
import com.grootan.storeflow.exceptions.ResourceNotFoundException;
import com.grootan.storeflow.mapper.ProductMapper;
import com.grootan.storeflow.models.Category;
import com.grootan.storeflow.models.Product;
import com.grootan.storeflow.repository.CategoryRepository;
import com.grootan.storeflow.repository.ProductRepository;
import com.grootan.storeflow.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;
    private ProductRequestDto requestDto;
    private ProductResponseDto responseDto;
    private UUID categoryId = UUID.randomUUID();
    private UUID productId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setId(categoryId);
        category.setName("Electronics");

        product = new Product();
        product.setId(productId);
        product.setName("Test Product");
        product.setStock(10);
        product.setCategory(category);
        product.setStatus(ProductStatus.ACTIVE);

        requestDto = new ProductRequestDto();
        requestDto.setName("Test Product");
        requestDto.setCategoryId(categoryId);
        requestDto.setPrice(BigDecimal.TEN);
        requestDto.setStock(10);
        
        responseDto = new ProductResponseDto();
        responseDto.setId(productId);
        responseDto.setName("Test Product");
    }

    @Test
    void createProduct_success() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);

        ProductResponseDto result = productService.createProduct(requestDto);

        assertNotNull(result);
        assertEquals(productId, result.getId());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_categoryNotFound_throwsException() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(requestDto));
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_success_softDeletes() {
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deleteProduct(productId);

        assertEquals(ProductStatus.DISCONTINUED, product.getStatus());
        assertNotNull(product.getDeletedAt());
        verify(productRepository).save(product);
    }
}
"@

Set-Content -Path "$baseDir\OrderServiceTest.java" -Value @"
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
"@
