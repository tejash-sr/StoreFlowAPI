package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.dto.response.OrderResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvExportServiceImplTest {

    private final CsvExportServiceImpl service = new CsvExportServiceImpl();

    @Test
    void exportOrdersToCsv_createsValidCsv() {
        OrderResponseDto dto1 = new OrderResponseDto();
        dto1.setReferenceNumber("ORD-001");
        dto1.setStatus("CONFIRMED");
        dto1.setTotalAmount(new BigDecimal("150.00"));

        OrderResponseDto dto2 = new OrderResponseDto();
        dto2.setReferenceNumber("ORD-002, \"with commas\"");
        dto2.setStatus("SHIPPED");
        dto2.setTotalAmount(new BigDecimal("250.00"));

        Page<OrderResponseDto> page = new PageImpl<>(List.of(dto1, dto2));

        byte[] csvBytes = service.exportOrdersToCsv(page);
        String csv = new String(csvBytes);

        assertTrue(csv.contains("Reference Number,Status,Total Amount,Created At,Item Count"));
        assertTrue(csv.contains("ORD-001,CONFIRMED,150.00,"));
        assertTrue(csv.contains("\"ORD-002, \"\"with commas\"\"\",SHIPPED,250.00,"));
    }

    @Test
    void exportOrdersToCsv_withEmptyList_returnsHeaderOnly() {
        Page<OrderResponseDto> page = new PageImpl<>(List.of());
        byte[] csvBytes = service.exportOrdersToCsv(page);
        String csv = new String(csvBytes);
        
        assertTrue(csv.contains("Reference Number,Status,Total Amount,Created At,Item Count"));
    }
}
