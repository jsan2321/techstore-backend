package com.ecoapi.techstore.user.infrastructure.config;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ecoapi.techstore.user.application.port.out.RoleRepositoryPort;
import com.ecoapi.techstore.user.domain.model.Role;
import com.ecoapi.techstore.user.domain.model.RoleName;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RoleInitializer {
    @Bean
    public CommandLineRunner initRoles(RoleRepositoryPort roleRepository) {
            return args -> {
                // Loop through every enum value (ROLE_USER, ROLE_ADMIN)
                Arrays.stream(RoleName.values()).forEach(roleName -> {
                    // If it doesn't exist in the DB, create it!
                    if (!roleRepository.existsByName(roleName)) {
                        roleRepository.save(new Role(roleName));
                        log.info("✅ Data Seeding: Created missing role -> {}", roleName);
                    }
                }); 
            };
    }
}
