package com.douglasmarq.imageservice.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.douglasmarq.imageservice.domain.ImageFiltersEnum;
import com.douglasmarq.imageservice.domain.Images;
import com.douglasmarq.imageservice.domain.dto.ImageOptions;
import com.douglasmarq.imageservice.domain.dto.ImagesResponse;
import com.douglasmarq.imageservice.domain.dto.ProcessedImage;
import com.douglasmarq.imageservice.domain.repository.ImagesRepository;
import com.douglasmarq.imageservice.infraestructure.utils.Base64Utils;
import com.douglasmarq.imageservice.infraestructure.utils.ChecksumUtils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock private AwsService awsService;

    @Mock private ImagesRepository imagesRepository;

    @InjectMocks private ImageService imageService;

    private final String userId = UUID.randomUUID().toString();
    private final String base64Content = "data:image/jpeg;base64,/9j/4AAQSkZJRg==";
    private final String key = "test-image-key";
    private final String md5Checksum = "abc123checksum";
    private final String nginxUrl = "http://nginx-server";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageService, "nginxUrl", nginxUrl);
    }

    @Test
    @DisplayName("Should not process image when checksum matches")
    void shouldNotProcessImageWhenChecksumExists() throws IOException {
        ImageOptions imageOptions =
                ImageOptions.builder()
                        .base64Content(base64Content)
                        .imageFilters(ImageFiltersEnum.GRAYSCALE)
                        .dimension(0.5f)
                        .build();

        Images existingImage =
                Images.builder()
                        .userId(UUID.fromString(userId))
                        .imageKey("existing-key")
                        .imageDimension("100x100")
                        .md5Checksum(md5Checksum)
                        .build();

        Map<ImageFiltersEnum, BiFunction<byte[], Float, ProcessedImage>> mockedFilterMap =
                new EnumMap<>(ImageFiltersEnum.class);
        BiFunction<byte[], Float, ProcessedImage> mockFilterFunction =
                (bytes, dimension) -> new ProcessedImage(new byte[] {1, 2, 3}, 100, 100);
        mockedFilterMap.put(ImageFiltersEnum.GRAYSCALE, mockFilterFunction);

        ReflectionTestUtils.setField(imageService, "filterMap", mockedFilterMap);

        try (MockedStatic<ChecksumUtils> checksumUtilsMock = mockStatic(ChecksumUtils.class);
                MockedStatic<Base64Utils> base64UtilsMock = mockStatic(Base64Utils.class)) {

            base64UtilsMock
                    .when(() -> Base64Utils.removePrefix(anyString()))
                    .thenReturn("dGVzdA==");
            checksumUtilsMock
                    .when(() -> ChecksumUtils.calculateMD5Checksum(any(byte[].class)))
                    .thenReturn(md5Checksum);

            when(imagesRepository.findAllByMd5Checksum(md5Checksum))
                    .thenReturn(List.of(existingImage));

            imageService.processImage(userId, imageOptions);

            verify(imagesRepository).findAllByMd5Checksum(md5Checksum);
            verify(awsService, never()).uploadImage(anyString(), any(byte[].class));
        }
    }

    @Test
    @DisplayName("Should get images succesfully")
    void shouldGetImagesSuccessfully() {
        UUID userUuid = UUID.fromString(userId);
        Images image1 =
                Images.builder()
                        .userId(userUuid)
                        .imageKey("image1.jpg")
                        .imageDimension("100x100")
                        .md5Checksum(md5Checksum)
                        .build();

        Images image2 =
                Images.builder()
                        .userId(userUuid)
                        .imageKey("image2.jpg")
                        .imageDimension("200x200")
                        .md5Checksum("another-checksum")
                        .build();

        List<Images> imagesList = Arrays.asList(image1, image2);

        when(imagesRepository.findAllByUserId(userUuid)).thenReturn(imagesList);

        List<ImagesResponse> response = imageService.getImages(userUuid);

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals(
                nginxUrl + "/resize/100x100/" + userUuid + "/image1.jpg", response.get(0).getUrl());
        assertEquals(
                nginxUrl + "/resize/200x200/" + userUuid + "/image2.jpg", response.get(1).getUrl());
        verify(imagesRepository).findAllByUserId(userUuid);
    }

    @Test
    @DisplayName("When no images are found, should return empty list")
    void shouldReturnEmptyListWhenNoImagesFound() {
        UUID userUuid = UUID.fromString(userId);
        when(imagesRepository.findAllByUserId(userUuid)).thenReturn(Collections.emptyList());

        List<ImagesResponse> response = imageService.getImages(userUuid);

        assertNotNull(response);
        assertTrue(response.isEmpty());
        verify(imagesRepository).findAllByUserId(userUuid);
    }
}
