package com.sourcream.qrcodescavengerhunt.controllers;

import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.domain.entities.*;
import com.sourcream.qrcodescavengerhunt.repositories.ProgressRepository;
import com.sourcream.qrcodescavengerhunt.security.WithMockOidcUser;
import com.sourcream.qrcodescavengerhunt.security.config.TestSecurityConfig;
import com.sourcream.qrcodescavengerhunt.services.EventService;
import com.sourcream.qrcodescavengerhunt.services.LocationService;
import com.sourcream.qrcodescavengerhunt.services.ProgressService;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import com.sourcream.qrcodescavengerhunt.services.impl.ProgressServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class ProgressControllerIntegrationTests {


    private MockMvc mockMvc;

    private UserService userService;

    private EventService eventService;

    private LocationService locationService;
    private ProgressService progressService;

    private ProgressRepository progressRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProgressServiceImpl.class);

    @Autowired
    public ProgressControllerIntegrationTests(MockMvc mockMvc, UserService userService, EventService eventService, LocationService locationService, ProgressService progressService, ProgressRepository progressRepository) {
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.eventService = eventService;
        this.locationService = locationService;
        this.progressService = progressService;
        this.progressRepository = progressRepository;
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatProgressWithAuthenticatedUserReturnsHttpStatus201() throws Exception {

        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        event = eventService.saveEvent(event);

        LocationEntity location = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(location);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/progress/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatProgressWithAuthenticatedUserReturnsCorrectData() throws Exception {

        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        event = eventService.saveEvent(event);

        LocationEntity location = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(location);

        ProgressSummary summary = TestDataUtil.createTestProgressSummaryUrl();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/progress/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.eventName").value(summary.getEventName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.count").value(summary.getCount())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.score").value(summary.getScore())
        );
    }

    @Test
    @Sql("/test-data.sql")
    public void testThatProgressWithAnonymousUserReturnsHttpStatus200() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.post("/progress/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    @Sql("/test-data.sql")
    public void testThatProgressWithAnonymousUserReturnsSummary() throws Exception {

        ProgressSummary summary = TestDataUtil.createTestProgressSummaryForAnonymousUser();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/progress/1/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.eventName").value(summary.getEventName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.count").value(summary.getCount())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.score").value(summary.getScore())
        );
    }
}
