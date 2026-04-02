package com.grootan.storeflow.exceptions;

import org.springframework.http.HttpStatus;

public class InvalidStatusTransitionException extends AppException {

    public InvalidStatusTransitionException(String from, String to) {
        super(String.format("Invalid status transition from %s to %s", from, to),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
