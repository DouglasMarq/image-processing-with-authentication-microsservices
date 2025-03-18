package com.douglasmarq.imageservice.domain.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsService {
    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final AmazonS3 amazonS3;

    public String uploadImage(String key, byte[] image) {
        var imagePath = String.format("%s/%s.jpg", key, UUID.randomUUID().toString());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.length);
        metadata.setContentType("image/jpeg");

        amazonS3.putObject(bucketName, imagePath, inputStream, metadata);

        return imagePath;
    }
}
