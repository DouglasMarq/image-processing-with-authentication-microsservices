package com.douglasmarq.auth.domain.exception;

import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
public class RegisterApiException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public RegisterApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
