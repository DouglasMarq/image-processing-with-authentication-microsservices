package com.douglasmarq.bff.application.controller;

import com.douglasmarq.bff.domain.dto.ImageOptionsRequest;
import com.douglasmarq.bff.domain.dto.ImagesResponse;
import com.douglasmarq.bff.domain.service.ImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ImageController {
    private final ImageService imageService;

    @PreAuthorize("isAuthenticated()")
    @MutationMapping
    public Boolean uploadImages(@Argument List<ImageOptionsRequest> imagesOpts) {
        log.info("uploadImages called with imagesOpts: {}", imagesOpts);

        return imageService.handleImages(imagesOpts);
    }

    @PreAuthorize("isAuthenticated()")
    @QueryMapping
    public List<ImagesResponse> getImages() {
        return imageService.getImages();
    }
}
