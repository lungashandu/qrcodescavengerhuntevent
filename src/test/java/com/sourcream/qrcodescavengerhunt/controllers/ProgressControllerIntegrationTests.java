package com.sourcream.qrcodescavengerhunt.controllers;

import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.repositories.ProgressRepository;
import com.sourcream.qrcodescavengerhunt.security.WithMockOidcUser;
import com.sourcream.qrcodescavengerhunt.security.config.TestSecurityConfig;
import com.sourcream.qrcodescavengerhunt.services.EventService;
import com.sourcream.qrcodescavengerhunt.services.LocationService;
import com.sourcream.qrcodescavengerhunt.services.ProgressService;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatProgressReturnsCorrectForTop10Leaderboard() throws Exception {
        UserEntity userA = TestDataUtil.createTestUserA();
        userService.saveUser(userA);
        UserEntity userB = TestDataUtil.createTestUserB();
        userService.saveUser(userB);
        UserEntity userC = TestDataUtil.createTestUserC();
        userService.saveUser(userC);

        EventEntity event = TestDataUtil.createTestEventA(null);
        eventService.saveEvent(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(locationA);
        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationService.saveLocation(locationB);
        LocationEntity locationC = TestDataUtil.createTestLocationC(event);
        locationService.saveLocation(locationC);

        //User A progress
        setupAuthentication(userA.getEmail());
        progressService.saveProgress(event.getId(), locationA.getId());
        progressService.saveProgress(event.getId(), locationB.getId());
        progressService.saveProgress(event.getId(), locationC.getId());

        //User B progress
        setupAuthentication(userB.getEmail());
        progressService.saveProgress(event.getId(), locationA.getId());
        progressService.saveProgress(event.getId(), locationB.getId());

        //User C progress
        setupAuthentication(userC.getEmail());
        progressService.saveProgress(event.getId(), locationA.getId());

        mockMvc.perform(MockMvcRequestBuilders.get("/progress/1/top")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fullname").value(userA.getFullname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].score").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].scannedLocations").value(3))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].fullname").value(userB.getFullname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].scannedLocations").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].fullname").value(userC.getFullname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].scannedLocations").value(1));
    }

    private void setupAuthentication(String email) {
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getEmail()).thenReturn(email);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                oidcUser,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testGetUserLeaderboardPositionReturnValidData() throws Exception {
        UserEntity userA = TestDataUtil.createTestUserA();
        userService.saveUser(userA);
        UserEntity userB = TestDataUtil.createTestUserB();
        userService.saveUser(userB);
        UserEntity userC = TestDataUtil.createTestUserC();
        userService.saveUser(userC);

        EventEntity event = TestDataUtil.createTestEventA(null);
        eventService.saveEvent(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(locationA);
        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationService.saveLocation(locationB);
        LocationEntity locationC = TestDataUtil.createTestLocationC(event);
        locationService.saveLocation(locationC);

        //User A progress
        setupAuthentication(userA.getEmail());
        progressService.saveProgress(event.getId(), locationA.getId());
        progressService.saveProgress(event.getId(), locationB.getId());
        progressService.saveProgress(event.getId(), locationC.getId());

        //User B progress
        setupAuthentication(userB.getEmail());
        progressService.saveProgress(event.getId(), locationA.getId());
        progressService.saveProgress(event.getId(), locationB.getId());

        //User C progress
        setupAuthentication(userC.getEmail());
        progressService.saveProgress(event.getId(), locationA.getId());

        mockMvc.perform(MockMvcRequestBuilders.get("/progress/1/leaderboard/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullname").value(userC.getFullname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.score").isNumber())
                .andExpect(MockMvcResultMatchers.jsonPath("$.scannedLocations").value(1));
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testGetTopLeaderboardForNonExistentEventReturnsEmptyList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/progress/1/top"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isEmpty());
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testGetUserLeaderboardPositionForUserWithNoProgress() throws Exception {
        UserEntity userA = TestDataUtil.createTestUserA();
        userService.saveUser(userA);
        UserEntity userB = TestDataUtil.createTestUserB();
        userService.saveUser(userB);
        UserEntity userC = TestDataUtil.createTestUserC();
        userService.saveUser(userC);

        EventEntity event = TestDataUtil.createTestEventA(null);
        eventService.saveEvent(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(locationA);
        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationService.saveLocation(locationB);
        LocationEntity locationC = TestDataUtil.createTestLocationC(event);
        locationService.saveLocation(locationC);

        //User A progress
        setupAuthentication(userA.getEmail());
        progressService.saveProgress(event.getId(), locationA.getId());
        progressService.saveProgress(event.getId(), locationB.getId());
        progressService.saveProgress(event.getId(), locationC.getId());

        //User B progress
        setupAuthentication(userB.getEmail());
        progressService.saveProgress(event.getId(), locationA.getId());
        progressService.saveProgress(event.getId(), locationB.getId());

        //User C progress
        setupAuthentication(userC.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.get("/progress/1/leaderboard/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullname").value(userC.getFullname()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.score").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.scannedLocations").value(0));
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testLeaderboardOrderByScoreDescending() throws Exception {
        UserEntity userA = TestDataUtil.createTestUserA();
        userService.saveUser(userA);
        UserEntity userB = TestDataUtil.createTestUserB();
        userService.saveUser(userB);
        UserEntity userC = TestDataUtil.createTestUserC();
        userService.saveUser(userC);

        EventEntity event = TestDataUtil.createTestEventA(null);
        eventService.saveEvent(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(locationA);
        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationService.saveLocation(locationB);
        LocationEntity locationC = TestDataUtil.createTestLocationC(event);
        locationService.saveLocation(locationC);

        //User A progress
        setupAuthentication(userA.getEmail());
        progressService.saveProgress(event.getId(), locationA.getId());
        progressService.saveProgress(event.getId(), locationB.getId());
        progressService.saveProgress(event.getId(), locationC.getId());

        //User B progress
        setupAuthentication(userB.getEmail());
        progressService.saveProgress(event.getId(), locationA.getId());
        progressService.saveProgress(event.getId(), locationB.getId());

        //User C progress
        setupAuthentication(userC.getEmail());
        progressService.saveProgress(event.getId(), locationA.getId());

        mockMvc.perform(MockMvcRequestBuilders.get("/progress/1/top"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].fullname").value("John Doe"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].fullname").value("Jane Smith"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[2].fullname").value("Alice Williams"));
    }

}
