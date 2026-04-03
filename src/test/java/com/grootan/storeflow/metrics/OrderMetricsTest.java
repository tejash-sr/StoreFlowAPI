package com.grootan.storeflow.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderMetricsTest {

    private SimpleMeterRegistry registry;
    private OrderMetrics orderMetrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        orderMetrics = new OrderMetrics(registry);
    }

    @Test
    void recordOrderPlaced_incrementsCounter() {
        orderMetrics.recordOrderPlaced(new BigDecimal("100.00"));

        double count = registry.counter("orders.placed.count").count();
        assertEquals(1.0, count);
    }

    @Test
    void recordOrderPlaced_addsToRevenue() {
        orderMetrics.recordOrderPlaced(new BigDecimal("250.50"));

        double revenue = registry.counter("orders.revenue.total").count();
        assertEquals(250.50, revenue);
    }

    @Test
    void recordOrderPlaced_multipleOrders_accumulate() {
        orderMetrics.recordOrderPlaced(new BigDecimal("100.00"));
        orderMetrics.recordOrderPlaced(new BigDecimal("200.00"));

        double count = registry.counter("orders.placed.count").count();
        double revenue = registry.counter("orders.revenue.total").count();

        assertEquals(2.0, count);
        assertEquals(300.0, revenue);
    }
}
