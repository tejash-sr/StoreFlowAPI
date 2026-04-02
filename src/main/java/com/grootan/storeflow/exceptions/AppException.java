package com.grootan.storeflow.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
public class AppException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final Map<String, String> fieldErrors;

    public AppException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.fieldErrors = null;
    }

    public AppException(String message, HttpStatus httpStatus, Map<String, String> fieldErrors) {
        super(message);
        this.httpStatus = httpStatus;
        this.fieldErrors = fieldErrors;
    }
}
