package com.ecoapi.techstore.user.infrastructure.config;

import com.ecoapi.techstore.common.domain.valueobjects.Email;
import com.ecoapi.techstore.user.application.port.out.PasswordEncoderPort;
import com.ecoapi.techstore.user.application.port.out.RoleRepositoryPort;
import com.ecoapi.techstore.user.application.port.out.UserRepositoryPort;
import com.ecoapi.techstore.user.domain.model.Role;
import com.ecoapi.techstore.user.domain.model.RoleName;
import com.ecoapi.techstore.user.domain.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class AdminUserInitializer {

	@Value("${app.admin.enabled:false}")
	private boolean adminEnabled;

	@Value("${app.admin.email:}")
	private String adminEmail;

	@Value("${app.admin.password:}")
	private String adminPassword;

	@Value("${app.admin.first-name:Admin}")
	private String adminFirstName;

	@Value("${app.admin.last-name:User}")
	private String adminLastName;

	@Bean
	public CommandLineRunner initAdminUser(
			UserRepositoryPort userRepository,
			RoleRepositoryPort roleRepository,
			PasswordEncoderPort passwordEncoder) {
		return args -> {
			if (!adminEnabled) {
				log.info("Admin seeding disabled (app.admin.enabled=false).");
				return;
			}

			if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
				log.warn("Admin seeding skipped: app.admin.email or app.admin.password is empty.");
				return;
			}

			Email email = new Email(adminEmail);
			User adminUser;
			if (userRepository.existsByEmail(email)) {
				log.info("Admin user already exists for email {}. Ensuring admin roles...", adminEmail);
				adminUser = userRepository.findByEmail(email).get();
			} else {
				String passwordHash = passwordEncoder.encode(adminPassword);
				adminUser = User.register(adminFirstName, adminLastName, email, passwordHash);
			}

			Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
					.orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_ADMIN)));
			adminUser.addRole(adminRole);

			Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
					.orElseGet(() -> roleRepository.save(new Role(RoleName.ROLE_USER)));
			adminUser.addRole(userRole);

			adminUser.confirmEmail();
			userRepository.save(adminUser);
			log.info("Admin user created for email {}", adminEmail);
		};
	}
}
