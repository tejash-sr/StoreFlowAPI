package com.grootan.storeflow.exceptions;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends AppException {

    public InsufficientStockException(String sku, int requested, int available) {
        super(String.format("Insufficient stock for product %s: requested %d, available %d",
                sku, requested, available), HttpStatus.CONFLICT);
    }
}
