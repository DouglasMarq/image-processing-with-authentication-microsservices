package com.douglasmarq.imageservice.domain.exception;

import org.springframework.http.HttpStatus;

public class EmailApiException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public EmailApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
