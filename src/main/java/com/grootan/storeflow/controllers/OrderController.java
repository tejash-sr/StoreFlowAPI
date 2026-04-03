package com.grootan.storeflow.controllers;

import com.grootan.storeflow.dto.request.OrderRequestDto;
import com.grootan.storeflow.dto.request.OrderStatusUpdateRequestDto;
import com.grootan.storeflow.dto.response.OrderResponseDto;
import com.grootan.storeflow.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto placeOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        com.grootan.storeflow.security.CustomUserDetails userDetails = (com.grootan.storeflow.security.CustomUserDetails) auth.getPrincipal();
        UUID customerId = userDetails.getUser().getId(); 
        
        return orderService.placeOrder(customerId, requestDto);
    }

    @GetMapping
    public Page<OrderResponseDto> getOrders(Pageable pageable) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        com.grootan.storeflow.security.CustomUserDetails userDetails = (com.grootan.storeflow.security.CustomUserDetails) auth.getPrincipal();
        UUID customerId = userDetails.getUser().getId();
        return orderService.getOrdersForUser(customerId, pageable);
    }

    @GetMapping("/{id}")
    public OrderResponseDto getOrderById(@PathVariable UUID id) {
        return orderService.getOrderById(id);
    }

    @PatchMapping("/{id}/status")
    public OrderResponseDto updateOrderStatus(@PathVariable UUID id, @Valid @RequestBody OrderStatusUpdateRequestDto requestDto) {
        return orderService.updateOrderStatus(id, requestDto);
    }

    @GetMapping("/{id}/pdf")
    public org.springframework.http.ResponseEntity<byte[]> downloadOrderPdf(@PathVariable UUID id, @org.springframework.beans.factory.annotation.Autowired com.grootan.storeflow.service.PdfGenerationService pdfGenerationService) {
        OrderResponseDto order = orderService.getOrderById(id);
        byte[] pdf = pdfGenerationService.generateOrderSummaryPdf(order);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "order-" + order.getReferenceNumber() + ".pdf");
        return new org.springframework.http.ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/export")
    public org.springframework.http.ResponseEntity<byte[]> exportOrders(Pageable pageable, @org.springframework.beans.factory.annotation.Autowired com.grootan.storeflow.service.CsvExportService csvExportService) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        com.grootan.storeflow.security.CustomUserDetails userDetails = (com.grootan.storeflow.security.CustomUserDetails) auth.getPrincipal();
        UUID customerId = userDetails.getUser().getId();
        Page<OrderResponseDto> orders = orderService.getOrdersForUser(customerId, pageable);
        
        byte[] csv = csvExportService.exportOrdersToCsv(orders);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "orders.csv");
        return new org.springframework.http.ResponseEntity<>(csv, headers, HttpStatus.OK);
    }
}
