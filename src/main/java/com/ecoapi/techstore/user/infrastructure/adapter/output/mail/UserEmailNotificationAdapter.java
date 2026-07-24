package com.ecoapi.techstore.user.infrastructure.adapter.output.mail;

import com.ecoapi.techstore.user.application.port.out.UserEmailNotificationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Mail adapter for user security notifications.
 */
@Component
public class UserEmailNotificationAdapter implements UserEmailNotificationPort {

    private static final Logger logger = LoggerFactory.getLogger(UserEmailNotificationAdapter.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final boolean failOnError;

    public UserEmailNotificationAdapter(
            JavaMailSender mailSender,
            @Value("${app.mail.from:no-reply@techstore.local}") String fromAddress,
            @Value("${app.mail.fail-on-error:false}") boolean failOnError) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.failOnError = failOnError;
    }

    @Override
    public void sendEmailConfirmation(String toEmail, String firstName, String confirmationLink) {
        String subject = "Confirm your Good Shopping account";
        String body = "Hi " + firstName + ",\n\n"
                + "Please confirm your account using this link:\n"
                + confirmationLink + "\n\n"
                + "If you did not create this account, please ignore this email.";
        send(toEmail, subject, body);
    }

    @Override
    public void sendPasswordReset(String toEmail, String firstName, String resetLink) {
        String subject = "Reset your Good Shopping password";
        String body = "Hi " + firstName + ",\n\n"
                + "We received a password reset request. Use this link to set a new password:\n"
                + resetLink + "\n\n"
                + "If you did not request this, you can ignore this email.";
        send(toEmail, subject, body);
    }

    @Override
    public void sendPasswordChangedNotification(String toEmail, String firstName) {
        String subject = "Your password was changed";
        String body = "Hi " + firstName + ",\n\n"
                + "Your password has been changed successfully.\n"
                + "If this was not you, reset your password immediately and contact support.";
        send(toEmail, subject, body);
    }

    private void send(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ex) {
            logger.error("Failed to send email to {}: {}", toEmail, ex.getMessage(), ex);
            if (failOnError) {
                throw new IllegalStateException("Email delivery failed", ex);
            }
        }
    }
}
