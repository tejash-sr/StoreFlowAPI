package com.grootan.storeflow.service;

import com.grootan.storeflow.dto.response.OrderResponseDto;
import org.springframework.data.domain.Page;

public interface CsvExportService {
    byte[] exportOrdersToCsv(Page<OrderResponseDto> orders);
}
