package com.douglasmarq.bff.domain.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserToken {
    private UUID userId;

    private String email;

    private String plan;
}
