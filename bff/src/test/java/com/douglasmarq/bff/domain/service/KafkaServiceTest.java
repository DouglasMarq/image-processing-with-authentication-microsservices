package com.douglasmarq.bff.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.douglasmarq.bff.domain.dto.ImageOptionsRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class KafkaServiceTest {

    private static final String TEST_KAFKA_TOPIC = "test-topic";
    private static final String TEST_KEY = "test-key";

    @Mock private KafkaTemplate<String, ImageOptionsRequest> kafkaTemplate;

    @InjectMocks private KafkaService kafkaService;

    @Captor private ArgumentCaptor<ImageOptionsRequest> messageCaptor;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaService, "kafkaTopic", TEST_KAFKA_TOPIC);
    }

    @Test
    @DisplayName("should send batch messages with multiple messages")
    void shouldSendBatchMessagesWithMultipleMessages() {
        ImageOptionsRequest request1 = new ImageOptionsRequest("base64-content-1", null, 1.0f);
        ImageOptionsRequest request2 = new ImageOptionsRequest("base64-content-2", null, 2.0f);
        List<ImageOptionsRequest> requests = Arrays.asList(request1, request2);

        when(kafkaTemplate.send(anyString(), anyString(), any(ImageOptionsRequest.class)))
                .thenReturn(null);

        kafkaService.sendBatchMessages(TEST_KEY, requests);

        verify(kafkaTemplate, times(2))
                .send(eq(TEST_KAFKA_TOPIC), eq(TEST_KEY), messageCaptor.capture());
        List<ImageOptionsRequest> capturedMessages = messageCaptor.getAllValues();

        assertEquals(2, capturedMessages.size());
        assertEquals("base64-content-1", capturedMessages.get(0).getBase64Content());
        assertEquals("base64-content-2", capturedMessages.get(1).getBase64Content());
    }

    @Test
    @DisplayName("should send batch messages with single message")
    void shouldSendBatchMessagesWithSingleMessage() {
        ImageOptionsRequest request = new ImageOptionsRequest("base64-content", null, 1.0f);
        List<ImageOptionsRequest> requests = Collections.singletonList(request);

        when(kafkaTemplate.send(anyString(), anyString(), any(ImageOptionsRequest.class)))
                .thenReturn(null);

        kafkaService.sendBatchMessages(TEST_KEY, requests);

        verify(kafkaTemplate, times(1)).send(eq(TEST_KAFKA_TOPIC), eq(TEST_KEY), eq(request));
    }

    @Test
    @DisplayName("should not send batch messages when kafkaTemplate is null")
    void shouldHandleEmptyBatchMessages() {
        List<ImageOptionsRequest> emptyList = Collections.emptyList();

        kafkaService.sendBatchMessages(TEST_KEY, emptyList);

        verify(kafkaTemplate, never())
                .send(anyString(), anyString(), any(ImageOptionsRequest.class));
    }

    @Test
    @DisplayName("should use correct topic when kafkaTemplate is null")
    void shouldUseCorrectTopicAndKey() {
        String customKey = "custom-key";
        ImageOptionsRequest request = new ImageOptionsRequest("content", null, 1.0f);
        List<ImageOptionsRequest> requests = Collections.singletonList(request);

        kafkaService.sendBatchMessages(customKey, requests);

        verify(kafkaTemplate, times(1))
                .send(eq(TEST_KAFKA_TOPIC), eq(customKey), any(ImageOptionsRequest.class));
    }
}
