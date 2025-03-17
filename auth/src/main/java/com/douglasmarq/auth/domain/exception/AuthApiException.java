package com.douglasmarq.auth.domain.exception;

import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
public class AuthApiException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public AuthApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
