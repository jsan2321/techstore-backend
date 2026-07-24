package com.ecoapi.techstore.user.application.port.out;

/**
 * Output port for user security-related email notifications.
 */
public interface UserEmailNotificationPort {

    void sendEmailConfirmation(String toEmail, String firstName, String confirmationLink);

    void sendPasswordReset(String toEmail, String firstName, String resetLink);

    void sendPasswordChangedNotification(String toEmail, String firstName);
}
