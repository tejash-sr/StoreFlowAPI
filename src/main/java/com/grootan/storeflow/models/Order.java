package com.grootan.storeflow.models;

import com.grootan.storeflow.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Column(name = "reference_number", unique = true, nullable = false, length = 50)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "shipping_street", columnDefinition = "TEXT", nullable = false)
    private String shippingStreet;

    @Column(name = "shipping_city", length = 100, nullable = false)
    private String shippingCity;



    @Column(name = "shipping_country", length = 100, nullable = false)
    private String shippingCountry;

    @Column(name = "shipping_postal_code", length = 20, nullable = false)
    private String shippingPostalCode;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();
}
