package com.sourcream.qrcodescavengerhunt.util;

import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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
}
