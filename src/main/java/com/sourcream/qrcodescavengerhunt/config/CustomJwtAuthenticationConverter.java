package com.sourcream.qrcodescavengerhunt.config;

import com.sourcream.qrcodescavengerhunt.services.AuthenticatedUserProvisioningService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final AuthenticatedUserProvisioningService authenticatedUserProvisioningService;

    public CustomJwtAuthenticationConverter(AuthenticatedUserProvisioningService authenticatedUserProvisioningService) {
        this.authenticatedUserProvisioningService = authenticatedUserProvisioningService;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        authenticatedUserProvisioningService.provisionUserIfNeeded(jwt);
        return new JwtAuthenticationToken(jwt, Collections.emptyList());
    }
}
