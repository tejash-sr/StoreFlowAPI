package com.grootan.storeflow.controllers;

import com.grootan.storeflow.dto.request.OrderRequestDto;
import com.grootan.storeflow.dto.request.OrderStatusUpdateRequestDto;
import com.grootan.storeflow.dto.response.OrderResponseDto;
import com.grootan.storeflow.repository.UserRepository;
import com.grootan.storeflow.security.CustomUserDetails;
import com.grootan.storeflow.service.CsvExportService;
import com.grootan.storeflow.service.OrderService;
import com.grootan.storeflow.service.PdfGenerationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final PdfGenerationService pdfGenerationService;
    private final CsvExportService csvExportService;

    private UUID getAuthenticatedUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser().getId();
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new com.grootan.storeflow.exceptions.AppException("User not found", HttpStatus.UNAUTHORIZED))
                .getId();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto placeOrder(@Valid @RequestBody OrderRequestDto requestDto) {
        return orderService.placeOrder(getAuthenticatedUserId(), requestDto);
    }

    @GetMapping
    public Page<OrderResponseDto> getOrders(Pageable pageable) {
        return orderService.getOrdersForUser(getAuthenticatedUserId(), pageable);
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
    public org.springframework.http.ResponseEntity<byte[]> downloadOrderPdf(@PathVariable UUID id) {
        OrderResponseDto order = orderService.getOrderById(id);
        byte[] pdf = pdfGenerationService.generateOrderSummaryPdf(order);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "order-" + order.getReferenceNumber() + ".pdf");
        return new org.springframework.http.ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/export")
    public org.springframework.http.ResponseEntity<byte[]> exportOrders(Pageable pageable) {
        Page<OrderResponseDto> orders = orderService.getOrdersForUser(getAuthenticatedUserId(), pageable);
        byte[] csv = csvExportService.exportOrdersToCsv(orders);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "orders.csv");
        return new org.springframework.http.ResponseEntity<>(csv, headers, HttpStatus.OK);
    }
}
