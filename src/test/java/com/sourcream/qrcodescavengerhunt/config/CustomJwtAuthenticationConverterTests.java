package com.sourcream.qrcodescavengerhunt.config;

import com.sourcream.qrcodescavengerhunt.services.AuthenticatedUserProvisioningService;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomJwtAuthenticationConverterTests {
    private final AuthenticatedUserProvisioningService authenticatedUserProvisioningService = mock(AuthenticatedUserProvisioningService.class);
    private final CustomJwtAuthenticationConverter converter = new CustomJwtAuthenticationConverter(authenticatedUserProvisioningService);

    @Test
    public void convertProvisionUserAndCreatesAuthenticatedToken() {
        Jwt jwt = Jwt.withTokenValue("token-value")
                .header("alg", "none")
                .subject("google-subject")
                .claim("email", "test@example.com")
                .claim("name", "Test User")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        JwtAuthenticationToken authentication = (JwtAuthenticationToken) converter.convert(jwt);

        verify(authenticatedUserProvisioningService).provisionUserIfNeeded(jwt);

        assertThat(authentication.getToken()).isEqualTo(jwt);
        assertThat(authentication.isAuthenticated()).isTrue();
        assertThat(authentication.getAuthorities()).isEmpty();
        assertThat(authentication.getTokenAttributes())
                .containsAllEntriesOf(Map.of(
                   "sub", "google-subject",
                   "email", "test@example.com",
                   "name", "Test User"
                ));
    }
}
