package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.repositories.UserRepository;
import com.sourcream.qrcodescavengerhunt.services.impl.CustomOidcUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CustomOidcUserServiceIntegrationTests {

    @Autowired
    private CustomOidcUserService customOidcUserService;

    @Autowired
    private UserRepository userRepository;

    private OidcUserRequest createMockUserRequest(String email, String sub, String name) {
        Map<String, Object> claims = Map.of(
                "sub", sub,
                "email", email,
                "name", name
        );

        OidcIdToken idToken = new OidcIdToken(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                claims
        );

        OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
        ClientRegistration clientRegistration = mock(ClientRegistration.class);
        ClientRegistration.ProviderDetails providerDetails = mock(ClientRegistration.ProviderDetails.class);
        ClientRegistration.ProviderDetails.UserInfoEndpoint userInfoEndpoint = mock(ClientRegistration.ProviderDetails.UserInfoEndpoint.class);

        when(providerDetails.getUserInfoEndpoint()).thenReturn(userInfoEndpoint);
        when(clientRegistration.getProviderDetails()).thenReturn(providerDetails);
        when(clientRegistration.getRegistrationId()).thenReturn("google");

        return new OidcUserRequest(
                clientRegistration,
                accessToken,
                idToken
        );
    }

    @Test
    public void testLoadUserCreatesAndSavesNewUser() {

        String email = "test@example.com";
        String sub = "123456789";
        String name = "Test User";

        OidcUserRequest userRequest = createMockUserRequest(email, sub, name);

        customOidcUserService.loadUser(userRequest);

        Optional<UserEntity> savedUser = userRepository.findByEmail(email);

        assertThat(savedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getEmail()).isEqualTo(email);
                    assertThat(user.getSub()).isEqualTo(sub);
                    assertThat(user.getFullname()).isEqualTo(name);
                });
    }

    @Test
    public void testThatLoadUserDoesNotUpdateUserForExistingUser() {
        UserEntity existingUser = TestDataUtil.createTestUserA();
        userRepository.save(existingUser);

        String newSub = "new-sub-123";
        String newName = "Updated User";

        OidcUserRequest userRequest = createMockUserRequest(existingUser.getEmail(), newSub, newName);

        customOidcUserService.loadUser(userRequest);

        Optional<UserEntity> updatedUser = userRepository.findByEmail(existingUser.getEmail());

        assertThat(updatedUser)
                .isPresent()
                .hasValueSatisfying(user -> {
                    assertThat(user.getSub()).isEqualTo(existingUser.getSub());
                    assertThat(user.getFullname()).isEqualTo(existingUser.getFullname());
                    assertThat(user.getRole()).isEqualTo(existingUser.getRole());
                });
    }
}
