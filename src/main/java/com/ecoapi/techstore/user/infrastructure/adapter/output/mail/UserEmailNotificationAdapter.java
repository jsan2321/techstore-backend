package com.ecoapi.techstore.user.infrastructure.adapter.output.mail;

import com.ecoapi.techstore.user.application.port.out.UserEmailNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * Mail adapter for user security notifications.
 */
@Component
public class UserEmailNotificationAdapter implements UserEmailNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(UserEmailNotificationAdapter.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String fromAddress;
    private final boolean failOnError;
    private final String provider;
    private final String resendApiKey;
    private final RestClient resendClient = RestClient.builder().baseUrl("https://api.resend.com").build();

    public UserEmailNotificationAdapter(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${app.mail.from:no-reply@techstore.local}") String fromAddress,
            @Value("${app.mail.fail-on-error:false}") boolean failOnError,
            @Value("${app.mail.provider:smtp}") String provider,
            @Value("${app.mail.resend-api-key:}") String resendApiKey) {
        this.mailSenderProvider = mailSenderProvider;
        this.fromAddress = fromAddress;
        this.failOnError = failOnError;
        this.provider = provider;
        this.resendApiKey = resendApiKey;
    }

    @Override
    public void sendEmailConfirmation(String toEmail, String firstName, String confirmationLink) {
        String subject = "Confirm your TechStore account";
        String body = "Hi " + firstName + ",\n\n"
                + "Please confirm your account using this link:\n"
                + confirmationLink + "\n\n"
                + "If you did not create this account, please ignore this email.";
        send(toEmail, subject, body);
    }

    @Override
    public void sendPasswordReset(String toEmail, String firstName, String resetLink) {
        String subject = "Reset your TechStore password";
        String body = "Hi " + firstName + ",\n\n"
                + "We received a password reset request. Use this link to set a new password:\n"
                + resetLink + "\n\n"
                + "If you did not request this, you can ignore this email.";
        send(toEmail, subject, body);
    }

    @Override
    public void sendPasswordChangedNotification(String toEmail, String firstName) {
        String subject = "Your TechStore password was changed";
        String body = "Hi " + firstName + ",\n\n"
                + "Your password has been changed successfully.\n"
                + "If this was not you, reset your password immediately and contact support.";
        send(toEmail, subject, body);
    }

    private void send(String toEmail, String subject, String body) {
        try {
            if ("resend".equalsIgnoreCase(provider)) {
                if (resendApiKey.isBlank()) {
                    throw new IllegalStateException("RESEND_API_KEY is required when MAIL_PROVIDER=resend");
                }
                resendClient.post()
                        .uri("/emails")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + resendApiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(Map.of("from", fromAddress, "to", toEmail, "subject", subject, "text", body))
                        .retrieve()
                        .toBodilessEntity();
            } else {
                JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
                if (mailSender == null) {
                    throw new IllegalStateException("SMTP mail is not configured");
                }
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromAddress);
                message.setTo(toEmail);
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
            }
        } catch (Exception ex) {
            logger.error("Email delivery failed using provider {}", provider, ex);
            if (failOnError) {
                throw new IllegalStateException("Email delivery failed", ex);
            }
        }
    }
}
