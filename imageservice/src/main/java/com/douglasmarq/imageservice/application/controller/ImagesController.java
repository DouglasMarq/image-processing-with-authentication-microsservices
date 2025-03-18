package com.douglasmarq.imageservice.application.controller;

import com.douglasmarq.imageservice.domain.dto.ImagesResponse;
import com.douglasmarq.imageservice.domain.service.ImageService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/images")
@AllArgsConstructor
@Tag(name = "Image", description = "Image management APIs")
@Slf4j
public class ImagesController {

    private final ImageService imageService;

    @Operation(
            summary = "Get images by userId",
            description = "Returns a list of images based on the userId",
            responses = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved the images",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema =
                                                @Schema(
                                                        type = "array",
                                                        implementation = ImagesResponse.class))),
                @ApiResponse(responseCode = "404", description = "Images not found")
            })
    @GetMapping
    public ResponseEntity<List<ImagesResponse>> getImages(@RequestParam UUID userId) {
        log.info("Getting images for userId: {}", userId);
        return ResponseEntity.ok(imageService.getImages(userId));
    }
}
