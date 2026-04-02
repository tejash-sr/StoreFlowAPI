package com.grootan.storeflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateRequestDto {
    @NotNull(message = "Amount is required")
    private Integer amount; // positive to increment, negative to decrement
}
