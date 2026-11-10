package com.ecoapi.techstore.user.infrastructure.config;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.user.application.port.out.PasswordEncoderPort;
import com.ecoapi.techstore.user.application.port.out.RoleRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.domain.model.Role;
import com.ecoapi.techstore.user.domain.model.RoleName;
import com.ecoapi.techstore.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.boot.CommandLineRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DemoCustomerInitializerTest {

    private UserRepositoryPort userRepository;
    private RoleRepositoryPort roleRepository;
    private PasswordEncoderPort passwordEncoder;
    private DemoCustomerInitializer initializer;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepositoryPort.class);
        roleRepository = Mockito.mock(RoleRepositoryPort.class);
        passwordEncoder = Mockito.mock(PasswordEncoderPort.class);

        initializer = new DemoCustomerInitializer();
        ReflectionTestUtils.setField(initializer, "demoCustomerEnabled", true);
        ReflectionTestUtils.setField(initializer, "customerEmail", "customer@techstore.local");
        ReflectionTestUtils.setField(initializer, "customerPassword", "Customer123!");
        ReflectionTestUtils.setField(initializer, "customerFirstName", "Demo");
        ReflectionTestUtils.setField(initializer, "customerLastName", "Customer");

        when(passwordEncoder.encode("Customer123!")).thenReturn("hashed-pwd");
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(new Role(RoleName.ROLE_USER)));
    }

    @Test
    void seedsCustomerUserWhenNotPresent() throws Exception {
        when(userRepository.existsByEmail(new Email("customer@techstore.local"))).thenReturn(false);

        CommandLineRunner runner = initializer.initDemoCustomer(userRepository, roleRepository, passwordEncoder);
        runner.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getEmail().value()).isEqualTo("customer@techstore.local");
        assertThat(saved.isEmailVerified()).isTrue();
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ROLE_USER)).isTrue();
        assertThat(saved.getRoles().stream().anyMatch(r -> r.getName() == RoleName.ROLE_ADMIN)).isFalse();
        assertThat(saved.getAddressBook()).isNotEmpty();
        assertThat(saved.getDefaultShippingAddress()).isPresent();
    }
}
