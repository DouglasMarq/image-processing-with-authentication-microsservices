package com.douglasmarq.imageservice.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedImage {
    private byte[] image;

    private int width;

    private int height;
}
