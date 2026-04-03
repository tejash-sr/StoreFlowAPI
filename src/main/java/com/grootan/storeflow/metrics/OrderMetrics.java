package com.grootan.storeflow.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderMetrics {

    private final Counter ordersPlacedCounter;
    private final Counter revenueCounter;

    public OrderMetrics(MeterRegistry registry) {
        this.ordersPlacedCounter = Counter.builder("orders.placed.count")
                .description("Total orders placed")
                .register(registry);
        this.revenueCounter = Counter.builder("orders.revenue.total")
                .description("Total revenue from orders")
                .register(registry);
    }

    public void recordOrderPlaced(BigDecimal amount) {
        ordersPlacedCounter.increment();
        revenueCounter.increment(amount.doubleValue());
    }
}
