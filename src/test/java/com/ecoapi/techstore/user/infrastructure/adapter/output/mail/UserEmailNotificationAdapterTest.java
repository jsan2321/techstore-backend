package com.ecoapi.techstore.user.infrastructure.adapter.output.mail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class UserEmailNotificationAdapterTest {
    @Test
    void failsTheCallingWorkflowWhenConfiguredSmtpDeliveryFails() {
        @SuppressWarnings("unchecked") ObjectProvider<JavaMailSender> provider = mock(ObjectProvider.class);
        JavaMailSender sender = mock(JavaMailSender.class);
        when(provider.getIfAvailable()).thenReturn(sender);
        doThrow(new RuntimeException("smtp unavailable")).when(sender).send(any(org.springframework.mail.SimpleMailMessage.class));

        var adapter = new UserEmailNotificationAdapter(provider, "no-reply@techstore.test", true, "smtp", "");

        assertThatThrownBy(() -> adapter.sendPasswordReset("customer@techstore.test", "Customer", "https://example.test/reset"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Email delivery failed");
    }
}
