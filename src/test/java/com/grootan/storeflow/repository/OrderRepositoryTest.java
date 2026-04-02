package com.grootan.storeflow.repository;

import com.grootan.storeflow.enums.OrderStatus;
import com.grootan.storeflow.enums.Role;
import com.grootan.storeflow.models.Order;
import com.grootan.storeflow.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void saveOrder_savesCorrectly() {
        User user = userRepository.save(User.builder()
                .email("cust@example.com")
                .password("pwd")
                .fullName("Cust")
                .role(Role.USER)
                .build());

        Order order = Order.builder()
                .referenceNumber("ORD-001")
                .customer(user)
                .status(OrderStatus.PENDING)
                .shippingStreet("123 Street")
                .shippingCity("City")
                .shippingCountry("Country")
                .shippingPostalCode("12345")
                .totalAmount(new BigDecimal("100.00"))
                .build();

        Order saved = orderRepository.save(order);
        assertNotNull(saved.getId());
        assertEquals("ORD-001", saved.getReferenceNumber());
    }

    @Test
    public void findByReferenceNumber_returnsOrder() {
        User user = userRepository.save(User.builder()
                .email("cust2@example.com")
                .password("pwd")
                .fullName("Cust 2")
                .role(Role.USER)
                .build());

        Order order = Order.builder()
                .referenceNumber("ORD-002")
                .customer(user)
                .shippingStreet("123 Street")
                .shippingCity("City")
                .shippingCountry("Country")
                .shippingPostalCode("12345")
                .totalAmount(new BigDecimal("200.00"))
                .build();
        orderRepository.save(order);

        Optional<Order> found = orderRepository.findByReferenceNumber("ORD-002");
        assertTrue(found.isPresent());
        assertEquals("ORD-002", found.get().getReferenceNumber());
    }
}
