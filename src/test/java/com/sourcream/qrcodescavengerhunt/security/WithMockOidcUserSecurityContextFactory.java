package com.sourcream.qrcodescavengerhunt.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WithMockOidcUserSecurityContextFactory implements WithSecurityContextFactory<WithMockOidcUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockOidcUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String role : annotation.roles()){
            authorities.add(new SimpleGrantedAuthority("Role_" + role));
        }

        OidcIdToken idToken = new OidcIdToken(
                "mock-id-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Collections.singletonMap("email", annotation.email())
        );

        OidcUserInfo userInfo = new OidcUserInfo(Collections.singletonMap("email", annotation.email()));

        OidcUser oidcUser = new DefaultOidcUser(
                authorities,
                idToken,
                userInfo,
                "email"
        );

        context.setAuthentication(new UsernamePasswordAuthenticationToken(
                oidcUser,
                "password",
                oidcUser.getAuthorities()
        ));

        return context;
    }
}
