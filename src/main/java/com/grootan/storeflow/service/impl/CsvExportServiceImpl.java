package com.grootan.storeflow.service.impl;

import com.grootan.storeflow.dto.response.OrderResponseDto;
import com.grootan.storeflow.exceptions.AppException;
import com.grootan.storeflow.service.CsvExportService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@Service
public class CsvExportServiceImpl implements CsvExportService {

    @Override
    public byte[] exportOrdersToCsv(Page<OrderResponseDto> orders) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter pw = new PrintWriter(baos)) {
            
            // Header
            pw.println("Reference Number,Status,Total Amount,Created At,Item Count");
            
            // Rows
            for (OrderResponseDto order : orders) {
                int itemCount = order.getItems() != null ? order.getItems().size() : 0;
                String row = String.format("%s,%s,%s,%s,%d", 
                        escapeCsv(order.getReferenceNumber()),
                        escapeCsv(order.getStatus()),
                        order.getTotalAmount() != null ? order.getTotalAmount().toString() : "0",
                        order.getCreatedAt() != null ? order.getCreatedAt().toString() : "",
                        itemCount);
                pw.println(row);
            }
            pw.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new AppException("Failed to generate CSV", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
