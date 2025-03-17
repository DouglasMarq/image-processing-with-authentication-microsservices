package com.douglasmarq.imageservice.domain.service;

import com.douglasmarq.imageservice.domain.dto.ImageOptions;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@AllArgsConstructor
@Slf4j
public class KafkaService {
    private ImageService imageService;

    @KafkaListener(topics = "local-images-topic", groupId = "image-service")
    public void consumeMessage(@Header(KafkaHeaders.RECEIVED_KEY) String key, ImageOptions message)
            throws IOException {
        log.info("Received key: {}", key);
        log.info("Received message: {}", message);

        imageService.processImage(key, message);
    }
}
