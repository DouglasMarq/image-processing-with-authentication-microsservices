package com.douglasmarq.bff.domain.exception;

import org.springframework.http.HttpStatus;

public class UserException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public UserException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
