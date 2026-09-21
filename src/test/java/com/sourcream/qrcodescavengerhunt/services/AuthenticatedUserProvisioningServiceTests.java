package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.util.UserUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class AuthenticatedUserProvisioningServiceTests {

    private final UserService userService = mock(UserService.class);
    private final UserUtil userUtil = mock(UserUtil.class);
    private final AuthenticatedUserProvisioningService provisioningService = new AuthenticatedUserProvisioningService(userService, userUtil, "");

    @Test
    public void provisionUserIfNeededSavesNewUserOnlyOncePerAuthenticatedUser() {
        Jwt jwt = createJwt();
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");

        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userUtil.formatUser(jwt)).thenReturn(user);
        when(userService.saveUser(user)).thenReturn(user);

        provisioningService.provisionUserIfNeeded(jwt);
        provisioningService.provisionUserIfNeeded(jwt);

        verify(userService).getUserByEmail("test@example.com");
        verify(userUtil).formatUser(jwt);
        verify(userService).saveUser(user);
    }

    @Test
    public void provisionUserIfNeededCachesExistingUserAfterFirstLookup() {
        Jwt jwt = createJwt();
        UserEntity user = new UserEntity();
        user.setEmail("test@example.com");

        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        provisioningService.provisionUserIfNeeded(jwt);
        provisioningService.provisionUserIfNeeded(jwt);

        verify(userService).getUserByEmail("test@example.com");
        verify(userUtil, never()).formatUser(jwt);
        verify(userService, never()).saveUser(user);
    }

    @Test
    public void provisionUserIfNeededAssignsAdminRoleForConfiguredSubject() {
        Jwt jwt = createJwt();
        UserEntity user = new UserEntity();

        AuthenticatedUserProvisioningService adminProvisioningService = new AuthenticatedUserProvisioningService(userService, userUtil, "google-subject");
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userUtil.formatUser(jwt)).thenReturn(user);

        adminProvisioningService.provisionUserIfNeeded(jwt);

        verify(userService).saveUser(user);
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    public void provisionUserIfNeededPromotesExistingConfiguredAdmin() {
        Jwt jwt = createJwt();
        UserEntity user = new UserEntity();
        user.setRole(Role.USER);

        AuthenticatedUserProvisioningService adminProvisioningService = new AuthenticatedUserProvisioningService(userService, userUtil, "google-subject");
        when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(user));

        adminProvisioningService.provisionUserIfNeeded(jwt);

        verify(userService).saveUser(user);
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        verify(userUtil, never()).formatUser(jwt);
    }

    private Jwt createJwt() {
        return Jwt.withTokenValue("token-value")
                .header("alg", "none")
                .subject("google-subject")
                .claim("email", "test@example.com")
                .claim("name", "Test User")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}
