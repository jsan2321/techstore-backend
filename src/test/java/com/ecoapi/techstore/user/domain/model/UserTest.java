package com.ecoapi.techstore.user.domain.model;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void registersUserWithDefaultActiveStateAndUnverifiedEmail() {
        User user = User.register("John", "Doe", new Email("john.doe@example.com"), "hashed_password");

        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getEmail().value()).isEqualTo("john.doe@example.com");
        assertThat(user.isActive()).isTrue();
        assertThat(user.isEmailVerified()).isFalse();
    }

    @Test
    void managesUserDeactivationAndReactivation() {
        User user = User.register("John", "Doe", new Email("john.doe@example.com"), "hashed_password");

        user.deactivate();
        assertThat(user.isActive()).isFalse();
        assertThat(user.getStatus()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.getAccessTokenInvalidBefore()).isNotNull();

        user.reactivate();
        assertThat(user.isActive()).isTrue();
    }

    @Test
    void confirmsEmailAndPasswordChanges() {
        User user = User.register("John", "Doe", new Email("john.doe@example.com"), "hashed_password");

        user.confirmEmail();
        assertThat(user.isEmailVerified()).isTrue();

        user.changePassword("new_hashed_password");
        assertThat(user.getPasswordHash()).isEqualTo("new_hashed_password");
        assertThat(user.getAccessTokenInvalidBefore()).isNotNull();
    }

    @Test
    void validatesFirstNameAndLastName() {
        assertThatThrownBy(() -> User.register("", "Doe", new Email("john@example.com"), "hash"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> User.register("John", "   ", new Email("john@example.com"), "hash"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
