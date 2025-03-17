package com.douglasmarq.bff.application.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.douglasmarq.bff.domain.dto.ImagesResponse;
import com.douglasmarq.bff.domain.service.ImageService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@GraphQlTest(ImageController.class)
public class ImageControllerTestIT {

    @Autowired private GraphQlTester graphQlTester;

    @MockitoBean private ImageService imageService;

    @Test
    @WithMockUser
    @DisplayName("Should Return List of Images When Called")
    void getImagesShouldReturnListOfImagesWhenCalled() {
        List<ImagesResponse> mockImages =
                Arrays.asList(
                        ImagesResponse.builder().url("exampleimage.com/image1").build(),
                        ImagesResponse.builder().url("exampleimage.com/image2").build());
        when(imageService.getImages()).thenReturn(mockImages);

        String query =
                """
                query {
                  getImages {
                    url
                  }
                }
                """;

        graphQlTester
                .document(query)
                .execute()
                .path("getImages")
                .entityList(ImagesResponse.class)
                .hasSize(2)
                .contains(mockImages.get(0), mockImages.get(1));
    }

    @Test
    @WithMockUser
    @DisplayName("Should Return True When Upload Is Successful")
    void uploadImagesShouldReturnTrueWhenUploadIsSuccessful() {
        List<Map<String, Object>> imageOptions =
                Arrays.asList(
                        Map.of(
                                "base64Content", "base64encodedimage1",
                                "imageFilters", "GRAYSCALE",
                                "dimension", 1.0F),
                        Map.of(
                                "base64Content", "base64encodedimage2",
                                "imageFilters", "GRAYSCALE",
                                "dimension", 1.0F));

        when(imageService.handleImages(any())).thenReturn(true);

        String mutation =
                """
                    mutation uploadImages($imagesOpts: [ImageOptionsRequestInput!]!) {
                      uploadImages(imagesOpts: $imagesOpts)
                    }
                """;

        graphQlTester
                .document(mutation)
                .variable("imagesOpts", imageOptions)
                .execute()
                .path("uploadImages")
                .entity(Boolean.class)
                .isEqualTo(true);
    }

    @Test
    @WithMockUser
    @DisplayName("Should Return False When Upload Fails")
    void uploadImagesShouldReturnFalseWhenUploadFails() {
        List<Map<String, Object>> imageOptions =
                Arrays.asList(
                        Map.of(
                                "base64Content", "base64encodedimage1",
                                "imageFilters", "GRAYSCALE",
                                "dimension", 1.0F),
                        Map.of(
                                "base64Content", "base64encodedimage2",
                                "imageFilters", "GRAYSCALE",
                                "dimension", 1.0F));

        when(imageService.handleImages(any())).thenReturn(false);

        String mutation =
                """
                    mutation uploadImages($imagesOpts: [ImageOptionsRequestInput!]!) {
                      uploadImages(imagesOpts: $imagesOpts)
                    }
                """;

        graphQlTester
                .document(mutation)
                .variable("imagesOpts", imageOptions)
                .execute()
                .path("uploadImages")
                .entity(Boolean.class)
                .isEqualTo(false);
    }
}
