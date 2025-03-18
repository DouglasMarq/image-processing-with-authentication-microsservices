package com.douglasmarq.auth.domain.service;

import com.douglasmarq.auth.domain.PlanType;
import com.douglasmarq.auth.domain.Users;
import com.douglasmarq.auth.domain.dto.RegisterUserRequest;
import com.douglasmarq.auth.domain.exception.RegisterApiException;
import com.douglasmarq.auth.domain.repository.UserRepository;
import com.douglasmarq.auth.infraestructure.logs.AnonymizeLogger;

import lombok.AllArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegisterService {
    private final AnonymizeLogger logger = new AnonymizeLogger(RegisterService.class);

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordService passwordService;

    public void register(RegisterUserRequest request) {
        try {
            logger.info("Trying to register user {}", request.getEmail());

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                logger.error("password does not match");
                throw new RegisterApiException("password does not match", HttpStatus.BAD_REQUEST);
            }

            var userBuilder =
                    Users.builder()
                            .name(request.getName())
                            .email(request.getEmail())
                            .password(passwordService.encode(request.getPassword()))
                            .plan(PlanType.BASIC);

            userRepository.save(userBuilder.build());

            logger.info("User {} registered successfully", request.getEmail());

            emailService.sendWelcomeEmail(request.getEmail());
        } catch (DataIntegrityViolationException e) {
            logger.error("Error registering user, probably e-mail already registered");
            throw new RegisterApiException("e-mail already registered", HttpStatus.CONFLICT);
        } catch (RegisterApiException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error registering user");
            throw new RegisterApiException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
