package com.douglasmarq.bff.domain.service;

import com.douglasmarq.bff.domain.PlanType;
import com.douglasmarq.bff.domain.dto.ImageOptionsRequest;
import com.douglasmarq.bff.domain.dto.ImagesResponse;
import com.douglasmarq.bff.domain.dto.UserToken;
import com.douglasmarq.bff.domain.exception.QuotaException;
import com.douglasmarq.bff.domain.exception.UserException;
import com.douglasmarq.bff.domain.repository.UserRepository;
import com.douglasmarq.bff.infraestructure.repository.ImageServiceApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final KafkaService kafkaService;
    private final UserRepository userRepository;
    private final ImageServiceApi imageServiceApi;

    public Boolean handleImages(List<ImageOptionsRequest> images) {
        UserToken userData =
                (UserToken) SecurityContextHolder.getContext().getAuthentication().getDetails();

        var user = userRepository.findById(userData.getUserId());

        if (user.isEmpty()) {
            log.error("user not found.");
            throw new UserException("user not found.", HttpStatus.NOT_FOUND);
        }

        var userResult = user.get();
        if (userResult.getQuotas() < 1 && userResult.getPlan() == PlanType.BASIC) {
            log.error("maximum quota reached.");
            throw new QuotaException("maximum quota reached.", HttpStatus.FORBIDDEN);
        }

        if (images.size() > userResult.getQuotas() && userResult.getPlan() == PlanType.BASIC) {
            log.error("maximum quota exceeded.");
            throw new QuotaException("maximum quota exceeded.", HttpStatus.FORBIDDEN);
        }

        log.info("Sending images to kafka");
        kafkaService.sendBatchMessages(userResult.getId().toString(), images);

        userResult.setQuotas(userResult.getQuotas() - images.size());
        userRepository.save(userResult);
        log.info("Images sent to kafka successfully");
        return true;
    }

    public List<ImagesResponse> getImages() {
        UserToken userData =
                (UserToken) SecurityContextHolder.getContext().getAuthentication().getDetails();

        return imageServiceApi.retrieveImagesFromImagesService(userData.getUserId());
    }
}
