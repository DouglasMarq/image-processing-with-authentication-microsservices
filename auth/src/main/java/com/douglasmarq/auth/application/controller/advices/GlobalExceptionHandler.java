package com.douglasmarq.auth.application.controller.advices;

import com.douglasmarq.auth.domain.dto.BaseErrorResponse;
import com.douglasmarq.auth.domain.exception.RegisterApiException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RegisterApiException.class)
    public ResponseEntity<BaseErrorResponse> handleRegisterApiException(RegisterApiException ex) {
        int statusCode = ex.getHttpStatus().value();
        String message = ex.getMessage();

        BaseErrorResponse body = new BaseErrorResponse(statusCode, message);
        return new ResponseEntity<>(body, ex.getHttpStatus());
    }
}
