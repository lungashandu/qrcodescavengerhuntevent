package com.sourcream.qrcodescavengerhunt.config;

import com.sourcream.qrcodescavengerhunt.services.impl.CustomOidcUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    CustomOidcUserService customOidcUserService;
    CustomJwtAuthenticationConverter customJwtAuthenticationConverter;

    public SecurityConfig(CustomOidcUserService customOidcUserService, CustomJwtAuthenticationConverter customJwtAuthenticationConverter) {
        this.customOidcUserService = customOidcUserService;
        this.customJwtAuthenticationConverter = customJwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/locations/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .oidcUserService(customOidcUserService)
                        )
                        .successHandler(savedRequestSuccessHandler())
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(customJwtAuthenticationConverter)
                                .decoder(oidcIdTokenDecoder())))
                .csrf(AbstractHttpConfigurer::disable)
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll());

        return http.build();
    }

    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler savedRequestSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler handler = new SavedRequestAwareAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl("/");
        return handler;
    }

    @Bean
    public JwtDecoder oidcIdTokenDecoder() {
        return JwtDecoders.fromIssuerLocation("https://accounts.google.com");
    }
}
