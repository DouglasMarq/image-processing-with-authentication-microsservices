package com.douglasmarq.bff.domain.dto;

import com.douglasmarq.bff.domain.ImageFiltersEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageOptionsRequest {
    private String base64Content;

    private ImageFiltersEnum imageFilters;

    private Float dimension;
}
