package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.dto.response.OrderItemResponseDto;
import com.grootan.storeflow.dto.response.OrderResponseDto;
import com.grootan.storeflow.dto.response.ProductResponseDto;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PdfGenerationServiceImplTest {

    private final PdfGenerationServiceImpl service = new PdfGenerationServiceImpl();

    @Test
    void generateOrderSummaryPdf_createsValidPdf() {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setReferenceNumber("ORD-123");
        dto.setStatus("CONFIRMED");
        dto.setTotalAmount(new BigDecimal("100.50"));
        dto.setCreatedAt(OffsetDateTime.now());
        dto.setShippingStreet("123 Main St");
        dto.setShippingCity("New York");
        dto.setShippingCountry("USA");
        dto.setShippingPostalCode("10001");

        OrderItemResponseDto item = new OrderItemResponseDto();
        item.setQuantity(2);
        item.setSubtotal(new BigDecimal("100.50"));
        ProductResponseDto product = new ProductResponseDto();
        product.setName("Widget");
        item.setProduct(product);
        dto.setItems(List.of(item));

        byte[] pdf = service.generateOrderSummaryPdf(dto);
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
        
        assertEquals("%PDF", new String(pdf, 0, 4));
    }
        
    @Test
    void generateOrderSummaryPdf_withNullItems_doesNotThrow() {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setReferenceNumber("ORD-999");
        dto.setStatus("PENDING");
        dto.setTotalAmount(new BigDecimal("0.00"));
        dto.setItems(null);

        byte[] pdf = service.generateOrderSummaryPdf(dto);
        assertNotNull(pdf);
        assertTrue(pdf.length > 0);
    }
}
