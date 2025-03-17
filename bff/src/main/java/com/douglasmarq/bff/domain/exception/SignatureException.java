package com.douglasmarq.bff.domain.exception;

import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
public class SignatureException extends RuntimeException {
    private HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public SignatureException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
