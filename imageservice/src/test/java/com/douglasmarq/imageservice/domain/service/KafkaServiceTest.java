package com.douglasmarq.imageservice.domain.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.douglasmarq.imageservice.domain.ImageFiltersEnum;
import com.douglasmarq.imageservice.domain.dto.ImageOptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
public class KafkaServiceTest {

    @Mock private ImageService imageService;

    @InjectMocks private KafkaService kafkaService;

    private String testKey;
    private ImageOptions testImageOptions;

    @BeforeEach
    void setUp() {
        testKey = "test-image-key";
        testImageOptions = new ImageOptions();
        testImageOptions.setBase64Content("base64encodedcontent");
        testImageOptions.setImageFilters(ImageFiltersEnum.GRAYSCALE);
        testImageOptions.setDimension(0.5f);
    }

    @Test
    @DisplayName("Consume Message should call process image")
    void consumeMessageShouldCallProcessImage() throws IOException {
        kafkaService.consumeMessage(testKey, testImageOptions);

        verify(imageService, times(1)).processImage(eq(testKey), eq(testImageOptions));
    }

    @Test
    @DisplayName("When image service throws exception, should propagate exception")
    void testConsumeMessageWhenImageServiceThrowsExceptionShouldPropagateException()
            throws IOException {
        doThrow(new IOException("Processing error"))
                .when(imageService)
                .processImage(anyString(), any(ImageOptions.class));

        assertThrows(
                IOException.class,
                () -> {
                    kafkaService.consumeMessage(testKey, testImageOptions);
                });

        verify(imageService, times(1)).processImage(eq(testKey), eq(testImageOptions));
    }

    @Test
    @DisplayName("When image filter is null, should process image without filter")
    void testConsumeMessageWithNullImageFilter() throws IOException {
        ImageOptions nullFilterOptions = new ImageOptions("base64content", null, 0.8f);

        kafkaService.consumeMessage(testKey, nullFilterOptions);

        verify(imageService, times(1)).processImage(eq(testKey), eq(nullFilterOptions));
    }
}
