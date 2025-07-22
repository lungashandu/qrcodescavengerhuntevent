package com.sourcream.qrcodescavengerhunt.util;

import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserUtil {

    public UserEntity formatUser(OidcUser oidcUser) {

        return UserEntity.builder()
                .sub(oidcUser.getIdToken().getSubject())
                .fullname(oidcUser.getFullName())
                .email(oidcUser.getEmail())
                .role(Role.USER)
                .createAt(LocalDateTime.now().toString())
                .build();

    }

    public UserEntity formatUser(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");
        String sub = jwt.getSubject();

        return UserEntity.builder()
                .sub(sub)
                .fullname(name)
                .email(email)
                .role(Role.USER)
                .createAt(LocalDateTime.now().toString())
                .build();
    }
}
