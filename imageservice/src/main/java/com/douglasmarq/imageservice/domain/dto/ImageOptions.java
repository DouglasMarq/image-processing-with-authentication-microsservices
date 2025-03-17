package com.douglasmarq.imageservice.domain.dto;

import com.douglasmarq.imageservice.domain.ImageFiltersEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageOptions {
    private String base64Content;

    private ImageFiltersEnum imageFilters;

    private Float dimension;
}
