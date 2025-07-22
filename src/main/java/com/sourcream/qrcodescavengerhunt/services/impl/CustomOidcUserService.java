package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import com.sourcream.qrcodescavengerhunt.util.UserUtil;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class CustomOidcUserService extends OidcUserService {

    UserService userService;
    UserUtil userUtil;

    public CustomOidcUserService(UserService userService, UserUtil userUtil) {
        this.userService = userService;
        this.userUtil = userUtil;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        System.out.println("Processing OIDC user: " + oidcUser.getEmail());
        try {
            processOidcUser(oidcUser);
            System.out.println("OIDC user, " + oidcUser.getEmail() + ", saved.");
        } catch (Exception e) {
            System.out.println("Failed to process OIDC user: " + e.getMessage());
            throw e;
        }

        return oidcUser;
    }

    public void processOidcUser(OidcUser oidcUser) {
        String email = oidcUser.getEmail();
        userService.getUserByEmail(email)
                .orElseGet(() -> {
                    UserEntity newUser = userUtil.formatUser(oidcUser);
                    return userService.saveUser(newUser);
                });
    }
}
