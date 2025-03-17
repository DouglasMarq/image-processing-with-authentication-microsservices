package com.douglasmarq.auth.infraestructure.repository;

import com.douglasmarq.auth.domain.exception.EmailApiException;
import com.douglasmarq.auth.infraestructure.logs.Anonymize;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.io.IOException;

@Repository
@Anonymize
public class EmailRepositoryImpl {

    private final SendGrid sendGrid;
    private final Request request;
    private final Email emailSender;

    public EmailRepositoryImpl(
            @Value("${sendgrid.api-key}") String apiKey,
            @Value("${sendgrid.sender}") String senderEmail) {
        sendGrid = new SendGrid(apiKey);
        emailSender = new Email(senderEmail);
        request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
    }

    public void sendEmail(String to) {
        try {
            var mail = buildMailPayload(to);
            request.setBody(mail.build());
            sendGrid.api(request);
        } catch (IOException e) {
            throw new EmailApiException(
                    "could not send welcome email to " + to, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Mail buildMailPayload(String to) {
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", "Account succesfully created.");
        return new Mail(emailSender, "Account creation on Image Processing Site", toEmail, content);
    }
}
