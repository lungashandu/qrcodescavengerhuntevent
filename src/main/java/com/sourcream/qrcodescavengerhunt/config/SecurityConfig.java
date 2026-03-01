package com.sourcream.qrcodescavengerhunt.config;

import com.sourcream.qrcodescavengerhunt.services.impl.CustomOidcUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
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
    @Order(1)
    public SecurityFilterChain progressApiSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/progress/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests( auth -> auth
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain appSecurity(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
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
