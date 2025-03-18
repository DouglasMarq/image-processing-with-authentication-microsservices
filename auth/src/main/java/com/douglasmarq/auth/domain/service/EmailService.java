package com.douglasmarq.auth.domain.service;

import com.douglasmarq.auth.domain.exception.EmailApiException;
import com.douglasmarq.auth.infraestructure.logs.AnonymizeLogger;
import com.douglasmarq.auth.infraestructure.repository.EmailRepositoryImpl;

import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class EmailService {
    private final AnonymizeLogger logger = new AnonymizeLogger(EmailService.class);

    private final EmailRepositoryImpl emailApiRepository;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public void sendWelcomeEmail(String to) {
        logger.info("Checking if email {} is valid", to);

        validateEmail(to);

        logger.info("Valid email, sending email.");

        emailApiRepository.sendEmail(to);

        logger.info("Email sent successfully");
    }

    private void validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new EmailApiException(
                    "Email address cannot be null or empty", HttpStatus.BAD_REQUEST);
        }

        if (!isValidEmailFormat(email)) {
            throw new EmailApiException("Invalid email address format", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isValidEmailFormat(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
}
