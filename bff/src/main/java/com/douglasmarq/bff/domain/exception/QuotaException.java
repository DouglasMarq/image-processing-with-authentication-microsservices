package com.douglasmarq.bff.domain.exception;

import org.springframework.http.HttpStatus;

public class QuotaException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public QuotaException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
