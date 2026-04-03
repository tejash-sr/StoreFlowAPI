package com.grootan.storeflow.service;

import com.grootan.storeflow.dto.response.OrderResponseDto;

public interface PdfGenerationService {
    byte[] generateOrderSummaryPdf(OrderResponseDto order);
}
