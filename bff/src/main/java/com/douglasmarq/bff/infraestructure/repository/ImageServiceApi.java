package com.douglasmarq.bff.infraestructure.repository;

import com.douglasmarq.bff.domain.dto.ImagesResponse;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Repository
public class ImageServiceApi {

    @Qualifier("imageServiceRestTemplate")
    private final RestTemplate imageServiceRestTemplate;

    @Value("${bff.api.image-service.url}")
    private String imageServiceUrl;

    public ImageServiceApi(
            @Qualifier("imageServiceRestTemplate") RestTemplate imageServiceRestTemplate) {
        this.imageServiceRestTemplate = imageServiceRestTemplate;
    }

    public List<ImagesResponse> retrieveImagesFromImagesService(UUID userId) {
        var requestEntity =
                RequestEntity.get(
                                String.format(
                                        "%s/%s?userId=%s",
                                        imageServiceUrl, "v1/images", userId.toString()))
                        .header(HttpHeaders.CONTENT_TYPE, "application/json")
                        .build();
        var result =
                imageServiceRestTemplate.exchange(
                        requestEntity, new ParameterizedTypeReference<List<ImagesResponse>>() {});

        return result.getBody();
    }
}
