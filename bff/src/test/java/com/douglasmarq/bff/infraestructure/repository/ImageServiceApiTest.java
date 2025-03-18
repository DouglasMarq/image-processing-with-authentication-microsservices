package com.douglasmarq.bff.infraestructure.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.douglasmarq.bff.domain.dto.ImagesResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ImageServiceApiTest {

    @Mock private RestTemplate imageServiceRestTemplate;

    @InjectMocks private ImageServiceApi imageServiceApi;

    private final String IMAGE_SERVICE_URL = "image-service.com";
    private final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(imageServiceApi, "imageServiceUrl", IMAGE_SERVICE_URL);
    }

    @Test
    @DisplayName("retrieveImagesFromImagesService should call image service and return images")
    void retrieveImagesFromImagesServiceShouldCallImageServiceAndReturnImages() {
        List<ImagesResponse> expectedImages =
                List.of(
                        ImagesResponse.builder().url("example.com/image1.jpg").build(),
                        ImagesResponse.builder().url("example.com/image2.jpg").build());

        String expectedUrl =
                String.format("%s/%s?userId=%s", IMAGE_SERVICE_URL, "v1/images", USER_ID);

        RequestEntity<Void> expectedRequest =
                RequestEntity.get(expectedUrl)
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .build();

        when(imageServiceRestTemplate.exchange(
                        any(RequestEntity.class),
                        eq(new ParameterizedTypeReference<List<ImagesResponse>>() {})))
                .thenReturn(ResponseEntity.ok(expectedImages));

        List<ImagesResponse> result = imageServiceApi.retrieveImagesFromImagesService(USER_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedImages, result);

        verify(imageServiceRestTemplate)
                .exchange(
                        any(RequestEntity.class),
                        eq(new ParameterizedTypeReference<List<ImagesResponse>>() {}));
    }

    @Test
    @DisplayName("retrieveImagesFromImagesService should build correct request")
    void retrieveImagesFromImagesServiceShouldBuildCorrectRequest() {
        when(imageServiceRestTemplate.exchange(
                        any(RequestEntity.class),
                        eq(new ParameterizedTypeReference<List<ImagesResponse>>() {})))
                .thenReturn(ResponseEntity.ok(List.of()));

        imageServiceApi.retrieveImagesFromImagesService(USER_ID);

        ArgumentCaptor<RequestEntity<?>> requestEntityCaptor =
                ArgumentCaptor.forClass(RequestEntity.class);
        verify(imageServiceRestTemplate)
                .exchange(
                        requestEntityCaptor.capture(),
                        eq(new ParameterizedTypeReference<List<ImagesResponse>>() {}));

        RequestEntity<?> capturedRequest = requestEntityCaptor.getValue();
        assertEquals(HttpMethod.GET, capturedRequest.getMethod());
        assertEquals(
                "application/json",
                capturedRequest.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));

        String uriString = capturedRequest.toString();
        assertTrue(uriString.contains("v1/images"));
        assertTrue(uriString.contains("userId=" + USER_ID));
    }

    @Test
    @DisplayName(
            "retrieveImagesFromImagesService should return empty list when service returns null")
    void retrieveImagesFromImagesServiceShouldReturnEmptyListWhenServiceReturnsEmptyList() {
        List<ImagesResponse> expectedImages = List.of();

        when(imageServiceRestTemplate.exchange(
                        any(RequestEntity.class),
                        eq(new ParameterizedTypeReference<List<ImagesResponse>>() {})))
                .thenReturn(ResponseEntity.ok(expectedImages));

        List<ImagesResponse> result = imageServiceApi.retrieveImagesFromImagesService(USER_ID);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("retrieveImagesFromImagesService should return null when service returns null")
    void retrieveImagesFromImagesServiceShouldHandleNullResponseBody() {
        ResponseEntity<List<ImagesResponse>> responseEntity =
                new ResponseEntity<>(null, HttpStatus.OK);

        when(imageServiceRestTemplate.exchange(
                        any(RequestEntity.class),
                        eq(new ParameterizedTypeReference<List<ImagesResponse>>() {})))
                .thenReturn(responseEntity);

        List<ImagesResponse> result = imageServiceApi.retrieveImagesFromImagesService(USER_ID);

        assertNull(result);
    }
}
