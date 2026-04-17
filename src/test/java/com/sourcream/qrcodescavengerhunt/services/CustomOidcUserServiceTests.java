package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.services.impl.CustomOidcUserService;
import com.sourcream.qrcodescavengerhunt.util.UserUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomOidcUserServiceTests {

    @Mock
    private UserService userService;

    @Mock
    private UserUtil userUtil;

    @InjectMocks
    private CustomOidcUserService customOidcUserService;

    @Test
    public void testLoadUser_newUser_createsAndSavesUser() throws Exception {
        String email = "test@example.com";
        String sub = "123456789";
        String name = "Test User";

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", sub);
        claims.put("email", email);
        claims.put("name", name);

        OidcIdToken idToken = new OidcIdToken("token-value", Instant.now(), Instant.now().plusSeconds(3600), claims);
        OidcUserInfo userInfo = new OidcUserInfo(claims);
        OidcUser oidcUser = new DefaultOidcUser(Collections.emptyList(), idToken, userInfo);

        when(userService.getUserByEmail(email)).thenReturn(Optional.empty());

        UserEntity newUser = new UserEntity();
        newUser.setEmail(email);
        newUser.setSub(sub);
        newUser.setFullname(name);
        when(userUtil.formatUser(oidcUser)).thenReturn(newUser);

        UserEntity savedUser = new UserEntity();
        savedUser.setId(1L);
        savedUser.setEmail(email);
        savedUser.setFullname(name);
        when(userService.saveUser(newUser)).thenReturn(savedUser);

        customOidcUserService.processOidcUser(oidcUser);

        verify(userService).getUserByEmail(email);
        verify(userUtil).formatUser(oidcUser);
        verify(userService).saveUser(newUser);
    }
}
