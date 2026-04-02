package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderStatusUpdateRequestDto {
    @NotBlank(message = "Status is required")
    private String status;
}
