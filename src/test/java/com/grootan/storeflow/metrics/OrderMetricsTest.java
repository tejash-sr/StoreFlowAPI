package com.grootan.storeflow.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderMetricsTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Counter ordersPlacedCounter;

    @Mock
    private Counter revenueCounter;

    private OrderMetrics orderMetrics;

    @BeforeEach
    void setUp() {
        when(meterRegistry.register(any())).thenAnswer(invocation -> {
            Counter counter = invocation.getArgument(0, Counter.class);
            return counter;
        });
        when(meterRegistry.counter("orders.placed.count")).thenReturn(ordersPlacedCounter);
        when(meterRegistry.counter("orders.revenue.total")).thenReturn(revenueCounter);
    }

    @Test
    void recordOrderPlaced_incrementsCounter() {
        orderMetrics = new OrderMetrics(meterRegistry);

        orderMetrics.recordOrderPlaced(new BigDecimal("100.00"));

        verify(ordersPlacedCounter).increment();
    }

    @Test
    void recordOrderPlaced_addsToRevenue() {
        orderMetrics = new OrderMetrics(meterRegistry);

        orderMetrics.recordOrderPlaced(new BigDecimal("250.50"));

        verify(revenueCounter).increment(250.50);
    }
}
