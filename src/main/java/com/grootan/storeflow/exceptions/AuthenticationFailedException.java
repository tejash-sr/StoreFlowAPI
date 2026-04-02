package com.grootan.storeflow.exceptions;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends AppException {

    public AuthenticationFailedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
