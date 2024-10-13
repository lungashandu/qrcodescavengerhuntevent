package com.sourcream.qrcodescavengerhunt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.security.WithMockOidcUser;
import com.sourcream.qrcodescavengerhunt.security.config.TestSecurityConfig;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.services.EventService;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class EventControllerIntegrationTests {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private EventService eventService;
    private UserService userService;

    @Autowired
    public EventControllerIntegrationTests(MockMvc mockMvc,
                                           ObjectMapper objectMapper,
                                           EventService eventService,
                                           UserService userService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.eventService = eventService;
        this.userService = userService;
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatCreateEventsReturnsHttpStatus201Created() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        String eventJson = objectMapper.writeValueAsString(event);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );

    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatCreateEventsSuccessfullyReturnsSavedEvents() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        String eventJson = objectMapper.writeValueAsString(event);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(event.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.eventName").value(event.getEventName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.description").value(event.getDescription())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.startTime").value(event.getStartTime())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.endTime").value(event.getEndTime())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.id").value(user.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.sub").value(user.getSub())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.fullname").value(user.getFullname())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.email").value(user.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.role").value(user.getRole().toString())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.createAt").value(user.getCreateAt())
        );
    }

    @Test
    public void testThatGetEventsByIdReturnsHttpStatus404WhenEventNotFound() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatGetEventsByIdReturnsHttpStatus200WhenEventIsFound() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);


        EventEntity event = TestDataUtil.createTestEventA(user);
        eventService.saveEvent(event);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatGetEventsByIdReturnsEventWhenEventIsFound() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);


        EventEntity event = TestDataUtil.createTestEventA(user);
        eventService.saveEvent(event);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(event.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.eventName").value(event.getEventName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.description").value(event.getDescription())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.startTime").value(event.getStartTime())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.endTime").value(event.getEndTime())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.id").value(user.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.sub").value(user.getSub())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.fullname").value(user.getFullname())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.email").value(user.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.role").value(user.getRole().toString())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.createAt").value(user.getCreateAt())
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatGetEventsByEmailReturnsHttpStatus200WhenEventIsFound() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);


        EventEntity event = TestDataUtil.createTestEventA(user);
        eventService.saveEvent(event);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/events/by-email/"+user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatGetEventsByEmailReturnsEventWhenEventIsFound() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);


        EventEntity event = TestDataUtil.createTestEventA(user);
        eventService.saveEvent(event);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/events/by-email/"+user.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(event.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].eventName").value(event.getEventName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].description").value(event.getDescription())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].startTime").value(event.getStartTime())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].endTime").value(event.getEndTime())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].userEntity.id").value(user.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].userEntity.sub").value(user.getSub())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].userEntity.fullname").value(user.getFullname())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].userEntity.email").value(user.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].userEntity.role").value(user.getRole().toString())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].userEntity.createAt").value(user.getCreateAt())
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatUpdateEventReturnsHttpStatus200() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        EventEntity savedEvent = eventService.saveEvent(event);

        EventEntity eventUpdate = TestDataUtil.createTestEventB(user);
        eventUpdate.setId(savedEvent.getId());
        String eventJson = objectMapper.writeValueAsString(eventUpdate);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/events/" + eventUpdate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatUpdateEventReturnsUpdatedEvent() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        EventEntity savedEvent = eventService.saveEvent(event);

        EventEntity eventUpdate = TestDataUtil.createTestEventB(user);
        eventUpdate.setId(savedEvent.getId());
        String eventJson = objectMapper.writeValueAsString(eventUpdate);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/events/" + eventUpdate.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(event.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.eventName").value(eventUpdate.getEventName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.description").value(eventUpdate.getDescription())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.startTime").value(eventUpdate.getStartTime())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.endTime").value(eventUpdate.getEndTime())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.id").value(user.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.sub").value(user.getSub())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.fullname").value(user.getFullname())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.email").value(user.getEmail())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.role").value(user.getRole().toString())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.userEntity.createAt").value(user.getCreateAt())
        );
    }

    @Test
    public void testThatDeleteReturnsHttpStatus204ForNonExistingEvent() throws Exception{
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatDeleteReturnsHttpStatus204ForExistingEvent() throws Exception{
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        eventService.saveEvent(event);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }
}
