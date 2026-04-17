package com.sourcream.qrcodescavengerhunt.config;

import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import com.sourcream.qrcodescavengerhunt.util.UserUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final UserService userService;
    private final UserUtil userUtil;

    public CustomJwtAuthenticationConverter(UserService userService, UserUtil userUtil) {
        this.userService = userService;
        this.userUtil = userUtil;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String email = jwt.getClaimAsString("email");

        userService.getUserByEmail(email).orElseGet(() -> {
            System.out.println("Processing user: " + email);
            UserEntity user = userUtil.formatUser(jwt);

            UserEntity savedUser = userService.saveUser(user);
            System.out.println("User "+ savedUser.getEmail() + " saved");

            return userService.saveUser(user);
        });

        return new JwtAuthenticationToken(jwt, Collections.emptyList());
    }
}
