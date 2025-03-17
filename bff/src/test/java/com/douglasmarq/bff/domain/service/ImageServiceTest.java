package com.douglasmarq.bff.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.douglasmarq.bff.domain.PlanType;
import com.douglasmarq.bff.domain.Users;
import com.douglasmarq.bff.domain.dto.ImageOptionsRequest;
import com.douglasmarq.bff.domain.dto.ImagesResponse;
import com.douglasmarq.bff.domain.dto.UserToken;
import com.douglasmarq.bff.domain.exception.QuotaException;
import com.douglasmarq.bff.domain.exception.UserException;
import com.douglasmarq.bff.domain.repository.UserRepository;
import com.douglasmarq.bff.infraestructure.repository.ImageServiceApi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @Mock private KafkaService kafkaService;

    @Mock private UserRepository userRepository;

    @Mock private ImageServiceApi imageServiceApi;

    @Mock private SecurityContext securityContext;

    @Mock private Authentication authentication;

    @InjectMocks private ImageService imageService;

    private UUID userId;
    private UserToken userToken;
    private List<ImageOptionsRequest> images;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        userToken =
                UserToken.builder().userId(userId).email("test@example.com").plan("BASIC").build();

        images = List.of(new ImageOptionsRequest("base64Content", null, 1.0f));

        lenient().when(authentication.getDetails()).thenReturn(userToken);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    private Users createValidBasicUser(int quotas) {
        return Users.builder()
                .id(userId)
                .email("test@example.com")
                .plan(PlanType.BASIC)
                .quotas(quotas)
                .build();
    }

    private Users createValidPremiumUser() {
        return Users.builder()
                .id(userId)
                .email("test@example.com")
                .plan(PlanType.PREMIUM)
                .quotas(0)
                .build();
    }

    @Test
    @DisplayName("handleImages should throw user not found exception when user is not found")
    void handleImagesShouldThrowUserNotFoundExceptionWhenUserIsNotFound() {
        try (MockedStatic<SecurityContextHolder> securityContextHolder =
                mockStatic(SecurityContextHolder.class)) {
            securityContextHolder
                    .when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            UserException exception =
                    assertThrows(UserException.class, () -> imageService.handleImages(images));
            assertEquals("user not found.", exception.getMessage());

            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(kafkaService);
        }
    }

    @Test
    @DisplayName("handleImages should throw quota exception when user has no quota")
    void handleImagesThrowsQuotaExceptionWhenUserHasNoQuota() {
        var validUser = createValidBasicUser(0);

        try (MockedStatic<SecurityContextHolder> securityContextHolder =
                mockStatic(SecurityContextHolder.class)) {
            securityContextHolder
                    .when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(userRepository.findById(userId)).thenReturn(Optional.of(validUser));

            QuotaException exception =
                    assertThrows(QuotaException.class, () -> imageService.handleImages(images));
            assertEquals("maximum quota reached.", exception.getMessage());

            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(kafkaService);
        }
    }

    @Test
    @DisplayName("handleImages should throw quota exception when user has insufficient quota")
    void handleImagesThrowsQuotaExceptionWhenUserHasInsufficientQuota() {
        var validUser = createValidBasicUser(1);

        List<ImageOptionsRequest> multipleImages =
                List.of(
                        new ImageOptionsRequest("content1", null, 1.0f),
                        new ImageOptionsRequest("content2", null, 1.0f));

        try (MockedStatic<SecurityContextHolder> securityContextHolder =
                mockStatic(SecurityContextHolder.class)) {
            securityContextHolder
                    .when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(userRepository.findById(userId)).thenReturn(Optional.of(validUser));

            QuotaException exception =
                    assertThrows(
                            QuotaException.class, () -> imageService.handleImages(multipleImages));
            assertEquals("maximum quota exceeded.", exception.getMessage());

            verify(userRepository).findById(userId);
            verifyNoMoreInteractions(userRepository);
            verifyNoInteractions(kafkaService);
        }
    }

    @Test
    @DisplayName("handleImages should process images and update quota when user has quota")
    void handleImagesProcessesImagesAndUpdatesQuotaWhenUserHasQuota() {
        var validUser = createValidBasicUser(5);

        try (MockedStatic<SecurityContextHolder> securityContextHolder =
                mockStatic(SecurityContextHolder.class)) {
            securityContextHolder
                    .when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(userRepository.findById(userId)).thenReturn(Optional.of(validUser));
            when(userRepository.save(any(Users.class))).thenReturn(validUser);

            Boolean result = imageService.handleImages(images);

            assertTrue(result);
            assertEquals(4, validUser.getQuotas());
            verify(userRepository).findById(userId);
            verify(kafkaService).sendBatchMessages(userId.toString(), images);
            verify(userRepository).save(validUser);
        }
    }

    @Test
    @DisplayName("handleImages should process images regardless of quota when user is premium")
    void handleImagesProcessesImagesRegardlessOfQuotaWhenUserIsPremium() {
        var validUser = createValidPremiumUser();

        try (MockedStatic<SecurityContextHolder> securityContextHolder =
                mockStatic(SecurityContextHolder.class)) {
            securityContextHolder
                    .when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(userRepository.findById(userId)).thenReturn(Optional.of(validUser));
            when(userRepository.save(any(Users.class))).thenReturn(validUser);

            Boolean result = imageService.handleImages(images);

            assertTrue(result);
            assertEquals(-1, validUser.getQuotas());
            verify(userRepository).findById(userId);
            verify(kafkaService).sendBatchMessages(userId.toString(), images);
            verify(userRepository).save(validUser);
        }
    }

    @Test
    @DisplayName("getImages should retrieve images for current user")
    void getImagesRetrievesImagesForCurrentUser() {
        List<ImagesResponse> expectedImages = List.of(ImagesResponse.builder().build());

        try (MockedStatic<SecurityContextHolder> securityContextHolder =
                mockStatic(SecurityContextHolder.class)) {
            securityContextHolder
                    .when(SecurityContextHolder::getContext)
                    .thenReturn(securityContext);
            when(imageServiceApi.retrieveImagesFromImagesService(userId))
                    .thenReturn(expectedImages);

            List<ImagesResponse> result = imageService.getImages();

            assertEquals(expectedImages, result);
            verify(imageServiceApi).retrieveImagesFromImagesService(userId);
        }
    }
}
