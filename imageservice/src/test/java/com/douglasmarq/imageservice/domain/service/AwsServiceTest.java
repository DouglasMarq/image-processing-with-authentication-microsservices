package com.douglasmarq.imageservice.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
class AwsServiceTest {
    @Mock private AmazonS3 amazonS3;

    @InjectMocks private AwsService awsService;

    @Captor private ArgumentCaptor<InputStream> inputStreamCaptor;

    @Captor private ArgumentCaptor<ObjectMetadata> metadataCaptor;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(awsService, "bucketName", bucketName);
    }

    @Test
    @DisplayName("When uploading image, should upload to S3 and return path")
    void uploadImageShouldUploadToS3AndReturnPath() {
        String key = "users/profile";
        byte[] imageData = "test image content".getBytes();

        String result = awsService.uploadImage(key, imageData);

        verify(amazonS3)
                .putObject(
                        eq(bucketName),
                        argThat(path -> path.startsWith(key + "/") && path.endsWith(".jpg")),
                        inputStreamCaptor.capture(),
                        metadataCaptor.capture());

        ObjectMetadata metadata = metadataCaptor.getValue();
        assertEquals(imageData.length, metadata.getContentLength());
        assertEquals("image/jpeg", metadata.getContentType());

        assertNotNull(result);
        assertTrue(result.startsWith(key + "/"));
        assertTrue(result.endsWith(".jpg"));
    }

    @Test
    @DisplayName("When uploading image, should use correct bucket name")
    void uploadImageShouldUseCorrectBucketName() {
        String key = "documents";
        byte[] imageData = new byte[10];

        awsService.uploadImage(key, imageData);

        verify(amazonS3)
                .putObject(
                        eq(bucketName), any(), any(InputStream.class), any(ObjectMetadata.class));
    }

    @Test
    @DisplayName("When uploading image, should set correct metadata")
    void uploadImageShouldSetCorrectMetadata() {
        String key = "products";
        byte[] imageData = new byte[100];

        awsService.uploadImage(key, imageData);

        verify(amazonS3).putObject(any(), any(), any(InputStream.class), metadataCaptor.capture());

        ObjectMetadata metadata = metadataCaptor.getValue();
        assertEquals(imageData.length, metadata.getContentLength());
        assertEquals("image/jpeg", metadata.getContentType());
    }

    @Test
    @DisplayName("When uploading image, should generate unique filename")
    void uploadImageShouldGenerateUniqueFilename() {
        String key = "gallery";
        byte[] imageData = new byte[50];

        String path1 = awsService.uploadImage(key, imageData);
        String path2 = awsService.uploadImage(key, imageData);

        assertNotEquals(path1, path2);
    }
}
