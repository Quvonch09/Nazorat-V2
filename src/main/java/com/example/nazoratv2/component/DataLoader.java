package com.example.nazoratv2.component;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.example.nazoratv2.entity.User;
import com.example.nazoratv2.entity.enums.Role;
import com.example.nazoratv2.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddl;

    @Override
    public void run(String... args) throws Exception {
        if (ddl.equals("create") || ddl.equals("create-drop")){
            User admin = User.builder()
                    .phone("998900000000")
                    .password(encoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .fullName("Admin Admin")
                    .build();

            userRepository.save(admin);
        }
    }
}
