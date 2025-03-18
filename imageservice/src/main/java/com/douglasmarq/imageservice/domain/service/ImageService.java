package com.douglasmarq.imageservice.domain.service;

import com.douglasmarq.imageservice.domain.ImageFiltersEnum;
import com.douglasmarq.imageservice.domain.Images;
import com.douglasmarq.imageservice.domain.dto.ImageOptions;
import com.douglasmarq.imageservice.domain.dto.ImagesResponse;
import com.douglasmarq.imageservice.domain.dto.ProcessedImage;
import com.douglasmarq.imageservice.domain.repository.ImagesRepository;
import com.douglasmarq.imageservice.infraestructure.utils.Base64Utils;
import com.douglasmarq.imageservice.infraestructure.utils.ChecksumUtils;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

import javax.imageio.ImageIO;

@Service
@Slf4j
public class ImageService {
    @Value("${image-service.api.nginx.url}")
    private String nginxUrl;

    private final Map<ImageFiltersEnum, BiFunction<byte[], Float, ProcessedImage>> filterMap;

    private final AwsService awsService;
    private final ImagesRepository imagesRepository;

    public ImageService(AwsService awsService, ImagesRepository imagesRepository) {
        filterMap = new EnumMap<>(ImageFiltersEnum.class);
        filterMap.put(ImageFiltersEnum.GRAYSCALE, this::applyGrayscale);

        this.awsService = awsService;
        this.imagesRepository = imagesRepository;
    }

    public void processImage(String key, ImageOptions image) throws IOException {
        byte[] decodedBytesImage =
                Base64.getDecoder().decode(Base64Utils.removePrefix(image.getBase64Content()));

        BiFunction<byte[], Float, ProcessedImage> filterFunction =
                filterMap.get(image.getImageFilters());

        if (filterFunction == null) {
            return;
        }

        log.info("Processing image with filter {}", image.getImageFilters());

        ProcessedImage processedImage =
                filterFunction.apply(decodedBytesImage, image.getDimension());

        String md5Checksum = ChecksumUtils.calculateMD5Checksum(processedImage.getImage());

        log.info("image MD5 checksum: {}", md5Checksum);

        var imageChecksum = imagesRepository.findAllByMd5Checksum(md5Checksum);

        if (!imageChecksum.isEmpty()) {
            log.info("Image already exists in database, setting new image dimension");
            if (imageChecksum.stream()
                    .anyMatch(
                            i ->
                                    i.getImageDimension()
                                            .equals(
                                                    String.format(
                                                            "%dx%d",
                                                            processedImage.getWidth(),
                                                            processedImage.getHeight())))) {
                log.info("Image already exists with same dimension, skipping");
                return;
            }

            var imageResult = imageChecksum.getFirst();
            saveImageMetadata(
                    key,
                    String.format("%s/%s", imageResult.getUserId(), imageResult.getImageKey()),
                    processedImage,
                    md5Checksum);
            log.info(
                    "new image dimension of {}, metadata updated",
                    String.format("%dx%d", processedImage.getWidth(), processedImage.getHeight()));
            return;
        }

        var path = awsService.uploadImage(key, processedImage.getImage());

        log.info("Image uploaded to S3 path: {}", path);

        saveImageMetadata(key, path, processedImage, md5Checksum);

        log.info("Image metadata saved to database");
    }

    public List<ImagesResponse> getImages(UUID userId) {
        var images = imagesRepository.findAllByUserId(userId);

        var imagesList = new ArrayList<ImagesResponse>();

        var imageBuilder = ImagesResponse.builder();

        for (var image : images) {
            imageBuilder.url(
                    String.format(
                            "%s/resize/%s/%s/%s",
                            nginxUrl,
                            image.getImageDimension(),
                            image.getUserId(),
                            image.getImageKey()));

            imagesList.add(imageBuilder.build());
        }

        log.info("Found {} images for user {}", imagesList.size(), userId);

        return imagesList;
    }

    private void saveImageMetadata(
            String userId, String path, ProcessedImage processedImage, String md5Checksum) {
        var splittedPath = path.split("/");

        var imageBuilder =
                Images.builder()
                        .userId(UUID.fromString(userId))
                        .imageKey(splittedPath[splittedPath.length - 1])
                        .imageDimension(
                                String.format(
                                        "%dx%d",
                                        processedImage.getWidth(), processedImage.getHeight()))
                        .md5Checksum(md5Checksum);

        imagesRepository.save(imageBuilder.build());
    }

    private ProcessedImage applyGrayscale(byte[] image, float dimension) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            BufferedImage originalImage = ImageIO.read(inputStream);

            var newWidth = (int) (originalImage.getWidth() * dimension);
            var newHeight = (int) (originalImage.getHeight() * dimension);

            BufferedImage grayscaleImage =
                    new BufferedImage(
                            originalImage.getWidth(),
                            originalImage.getHeight(),
                            BufferedImage.TYPE_BYTE_GRAY);

            Graphics2D g2d = grayscaleImage.createGraphics();
            g2d.drawImage(originalImage, 0, 0, null);
            g2d.dispose();

            ImageIO.write(grayscaleImage, "jpg", outputStream);

            return buildProcessedImage(outputStream.toByteArray(), newWidth, newHeight);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert image to grayscale", e);
        }
    }

    private ProcessedImage buildProcessedImage(byte[] image, int width, int height) {
        return new ProcessedImage(image, width, height);
    }
}
