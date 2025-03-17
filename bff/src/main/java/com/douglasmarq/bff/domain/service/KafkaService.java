package com.douglasmarq.bff.domain.service;

import com.douglasmarq.bff.domain.dto.ImageOptionsRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KafkaService {

    @Value("${spring.kafka.template.default-topic}")
    private String kafkaTopic;

    private final KafkaTemplate<String, ImageOptionsRequest> kafkaTemplate;

    public void sendBatchMessages(String key, List<ImageOptionsRequest> payload) {
        for (ImageOptionsRequest message : payload) {
            kafkaTemplate.send(kafkaTopic, key, message);
        }
    }
}
