package com.douglasmarq.auth.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.douglasmarq.auth.domain.exception.EmailApiException;
import com.douglasmarq.auth.infraestructure.repository.EmailRepositoryImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock private EmailRepositoryImpl emailApiRepository;

    @InjectMocks private EmailService emailService;

    @Test
    @DisplayName("Should call repository when email is valid")
    public void sendWelcomeEmailIfEmailIsValidShouldCallRepository() {
        String validEmail = "test@example.com";

        emailService.sendWelcomeEmail(validEmail);

        verify(emailApiRepository, times(1)).sendEmail(validEmail);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw EmailApiException when email is null or empty")
    public void sendWelcomeEmailShouldThrowIfEmailIsNullOrEmpty(String invalidEmail) {
        EmailApiException exception =
                assertThrows(
                        EmailApiException.class, () -> emailService.sendWelcomeEmail(invalidEmail));

        assertEquals("Email address cannot be null or empty", exception.getMessage());
        verify(emailApiRepository, never()).sendEmail(any());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "plainaddress",
                "@missingusername.com",
                "missing.domain@",
            })
    @DisplayName("Should throw EmailApiException when email format is invalid")
    public void sendWelcomeEmailShouldThrowIfEmailFormatIsInvalid(String invalidEmail) {
        EmailApiException exception =
                assertThrows(
                        EmailApiException.class, () -> emailService.sendWelcomeEmail(invalidEmail));

        assertEquals("Invalid email address format", exception.getMessage());
        verify(emailApiRepository, never()).sendEmail(any());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "normal@example.com",
                "very.common@example.com",
                "disposable.style.email.with+symbol@example.com",
                "other.email-with-hyphen@example.com",
                "fully-qualified-domain@example.com",
                "user.name+tag+sorting@example.com",
                "x@example.com",
                "example-indeed@strange-example.com",
                "example@s.example"
            })
    @DisplayName("Should call repository when email format is valid")
    public void sendWelcomeEmailShouldCallRepositoryWhenEmailFormatIsValid(String validEmail) {
        emailService.sendWelcomeEmail(validEmail);

        verify(emailApiRepository, times(1)).sendEmail(validEmail);
    }

    @Test
    @DisplayName("Should throw EmailApiException when an unexpected error occurs")
    public void sendWelcomeEmailShouldThrowEmailApiExceptionWhenAnUnexpectedErrorOccurs() {
        String validEmail = "test@example.com";
        EmailApiException expectedException =
                new EmailApiException(
                        "could not send welcome email to " + validEmail,
                        HttpStatus.INTERNAL_SERVER_ERROR);

        doThrow(expectedException).when(emailApiRepository).sendEmail(validEmail);

        EmailApiException actualException =
                assertThrows(
                        EmailApiException.class, () -> emailService.sendWelcomeEmail(validEmail));

        assertEquals(expectedException.getMessage(), actualException.getMessage());
    }
}
