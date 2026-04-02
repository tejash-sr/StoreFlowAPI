package com.grootan.storeflow.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grootan.storeflow.dto.request.OrderItemRequestDto;
import com.grootan.storeflow.dto.request.OrderRequestDto;
import com.grootan.storeflow.dto.request.ShippingAddressDto;
import com.grootan.storeflow.enums.Role;
import com.grootan.storeflow.models.Category;
import com.grootan.storeflow.models.Product;
import com.grootan.storeflow.models.User;
import com.grootan.storeflow.repository.CategoryRepository;
import com.grootan.storeflow.repository.OrderRepository;
import com.grootan.storeflow.repository.ProductRepository;
import com.grootan.storeflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        jdbcTemplate.update(
            "INSERT INTO users (id, email, password, full_name, role, enabled) VALUES (?, ?, ?, ?, ?, ?)",
            UUID.fromString("00000000-0000-0000-0000-000000000001"), "test@example.com", "password", "Test User", "USER", true
        );

        Category category = new Category();
        category.setName("Electronics");
        category = categoryRepository.save(category);

        product = new Product();
        product.setName("Existing Product");
        product.setDescription("Desc");
        product.setSku("EXISTING-SKU-ORD");
        product.setPrice(BigDecimal.valueOf(500));
        product.setStock(20);
        product.setCategory(category);
        product = productRepository.save(product);
    }

    @Test
    void placeOrder_success_returns201Status() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        ShippingAddressDto address = new ShippingAddressDto();
        address.setStreet("123 Main St");
        address.setCity("Metropolis");
        address.setCountry("Wonderland");
        address.setPostalCode("12345");
        request.setShippingAddress(address);

        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setProductId(product.getId());
        item.setQuantity(2); // total will be 1000

        request.setItems(List.of(item));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.totalAmount", is(1000.0)));
    }

    @Test
    void placeOrder_insufficientStock_returns409Status() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        ShippingAddressDto address = new ShippingAddressDto();
        address.setStreet("123 Main St");
        address.setCity("Metropolis");
        address.setCountry("Wonderland");
        address.setPostalCode("12345");
        request.setShippingAddress(address);

        OrderItemRequestDto item = new OrderItemRequestDto();
        item.setProductId(product.getId());
        item.setQuantity(50); // stock is 20

        request.setItems(List.of(item));

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict()); // Because InsufficientStockException is mapped to 409
    }
}
