package com.douglasmarq.imageservice.domain.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Builder
@Data
public class ImagesResponse {
    @NonNull private String url;
}
