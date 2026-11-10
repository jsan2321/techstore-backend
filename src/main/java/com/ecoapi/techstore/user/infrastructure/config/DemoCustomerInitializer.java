package com.ecoapi.techstore.user.infrastructure.config;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.user.application.port.out.PasswordEncoderPort;
import com.ecoapi.techstore.user.application.port.out.RoleRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.domain.model.Role;
import com.ecoapi.techstore.user.domain.model.RoleName;
import com.ecoapi.techstore.user.domain.model.SavedAddress;
import com.ecoapi.techstore.user.domain.model.User;
import com.ecoapi.techstore.user.domain.valueobjects.Address;
import com.ecoapi.techstore.user.domain.valueobjects.AddressType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Seeds a verified customer account for demonstration, QA, and recruiter evaluation.
 * Allows instant evaluation of customer storefront features without requiring email verification.
 */
@Configuration
@Slf4j
public class DemoCustomerInitializer {

    @Value("${app.demo-customer.enabled:true}")
    private boolean demoCustomerEnabled;

    @Value("${app.demo-customer.email:customer@techstore.local}")
    private String customerEmail;

    @Value("${app.demo-customer.password:Customer123!}")
    private String customerPassword;

    @Value("${app.demo-customer.first-name:Demo}")
    private String customerFirstName;

    @Value("${app.demo-customer.last-name:Customer}")
    private String customerLastName;

    @Bean
    public CommandLineRunner initDemoCustomer(
            UserRepositoryPort userRepository,
            RoleRepositoryPort roleRepository,
            PasswordEncoderPort passwordEncoder) {
        return args -> {
            if (!demoCustomerEnabled) {
                log.info("Demo customer seeding disabled (app.demo-customer.enabled=false).");
                return;
            }

            if (customerEmail == null || customerEmail.isBlank() || customerPassword == null || customerPassword.isBlank()) {
                log.warn("Demo customer seeding skipped: email or password is empty.");
                return;
            }

            Email email = new Email(customerEmail);
            User customerUser;
            if (userRepository.existsByEmail(email)) {
                log.info("Demo customer user already exists for email {}. Ensuring customer roles and status...", customerEmail);
                customerUser = userRepository.findByEmail(email).get();
                if (!passwordEncoder.matches(customerPassword, customerUser.getPasswordHash())) {
                    customerUser.changePassword(passwordEncoder.encode(customerPassword));
                }
                if (!customerUser.isActive()) {
                    customerUser.reactivate();
                }
            } else {
                String passwordHash = passwordEncoder.encode(customerPassword);
                customerUser = User.register(customerFirstName, customerLastName, email, passwordHash);
            }

            Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_USER)));
            customerUser.addRole(userRole);

            customerUser.confirmEmail();

            if (customerUser.getAddressBook().isEmpty()) {
                Address sampleAddress = Address.of(
                        "123 Tech Avenue",
                        "Suite 400",
                        "San Francisco",
                        "CA",
                        "94105",
                        "US"
                );
                SavedAddress savedAddress = SavedAddress.create(
                        "Home",
                        customerFirstName + " " + customerLastName,
                        sampleAddress,
                        AddressType.SHIPPING,
                        true
                );
                customerUser.addAddress(savedAddress);
            }

            userRepository.save(customerUser);
            log.info("Demo customer user ready for email {}", customerEmail);
        };
    }
}
