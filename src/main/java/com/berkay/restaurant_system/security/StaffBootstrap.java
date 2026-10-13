package com.berkay.restaurant_system.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.berkay.restaurant_system.entities.Staff;
import com.berkay.restaurant_system.repositories.StaffRepository;

// Creates the very first staff account on startup, if none exists yet,
// so there is always a way to log in and create further accounts via /api/auth/register.
@Component
public class StaffBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(StaffBootstrap.class);

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultUsername;
    private final String defaultPassword;

    public StaffBootstrap(StaffRepository staffRepository, PasswordEncoder passwordEncoder,
            @Value("${staff.bootstrap.username}") String defaultUsername,
            @Value("${staff.bootstrap.password}") String defaultPassword) {
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.defaultUsername = defaultUsername;
        this.defaultPassword = defaultPassword;
    }

    @Override
    public void run(String... args) {
        if (staffRepository.count() > 0) {
            return;
        }

        Staff staff = new Staff();
        staff.setUsername(defaultUsername);
        staff.setPassword(passwordEncoder.encode(defaultPassword));
        staffRepository.save(staff);

        log.info("No staff accounts found. Created the initial account with username '{}'.", defaultUsername);
    }
}
