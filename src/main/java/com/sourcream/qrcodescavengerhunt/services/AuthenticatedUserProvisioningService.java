package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.util.UserUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class AuthenticatedUserProvisioningService {
    private final UserService userService;
    private final UserUtil userUtil;
    private final Set<String> adminSubjects;
    private final ConcurrentMap<String, Boolean> provisionedUser = new ConcurrentHashMap<>();

    public AuthenticatedUserProvisioningService(UserService userService,
                                                UserUtil userUtil,
                                                @Value("${app.security.admin-subjects:}") String configuredAdminSubjects) {
        this.userService = userService;
        this.userUtil = userUtil;
        this.adminSubjects = parseSubjects(configuredAdminSubjects);
    }

    public void provisionUserIfNeeded(Jwt jwt) {
        String subject = jwt.getSubject();

        provisionedUser.computeIfAbsent(subject, key -> {
            String email = jwt.getClaimAsString("email");
            System.out.println(email + " subject: " + subject);
            userService.getUserByEmail(email).ifPresentOrElse(user ->
                promoteToAdminIfConfigured(user, subject),
                () -> createUser(jwt, subject)
            );
            return true;
        });
    }

    private void createUser(Jwt jwt, String subject) {
        UserEntity user = userUtil.formatUser(jwt);
        if (adminSubjects.contains(subject)) {
            user.setRole(Role.ADMIN);
        }
        userService.saveUser(user);
    }

    private void promoteToAdminIfConfigured(UserEntity user, String subject) {
        if (adminSubjects.contains(subject) && user.getRole() != Role.ADMIN) {
            user.setRole(Role.ADMIN);
            userService.saveUser(user);
            System.out.println(user.getFullname() + " has been promoted to System Administrator");
        }
    }

    private Set<String> parseSubjects(String configuredAdminSubjects) {
        return Arrays.stream(configuredAdminSubjects.split(","))
                .map(String::trim)
                .filter(subject -> !subject.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }
}
