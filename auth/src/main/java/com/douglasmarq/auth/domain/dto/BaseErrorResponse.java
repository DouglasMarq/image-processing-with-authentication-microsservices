package com.douglasmarq.auth.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BaseErrorResponse {
    private int status;
    private String message;
}
