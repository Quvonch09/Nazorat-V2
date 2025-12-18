package com.example.nazoratv2.security;

import com.example.nazoratv2.entity.Student;
import com.example.nazoratv2.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    public CurrentActor getCurrentActor() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return CurrentActor.anonymous();
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails cud) {

            if (cud.isUser()) {
                User u = cud.getUser();
                return CurrentActor.user(
                        u.getId(),
                        u.getRole().name(),
                        u.getFullName()
                );
            }

            if (cud.isStudent()) {
                Student s = cud.getStudent();
                return CurrentActor.student(
                        s.getId(),
                        "STUDENT",
                        s.getFullName()
                );
            }
        }

        return CurrentActor.anonymous();
    }
}

