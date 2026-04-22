package com.sourcream.qrcodescavengerhunt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.domain.dto.LocationDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.security.WithMockOidcUser;
import com.sourcream.qrcodescavengerhunt.security.config.TestSecurityConfig;
import com.sourcream.qrcodescavengerhunt.services.EventService;
import com.sourcream.qrcodescavengerhunt.services.LocationService;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class LocationControllerIntegrationTests {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final EventService eventService;

    private final UserService userService;

    private final LocationService locationService;

    @Autowired

    public LocationControllerIntegrationTests(MockMvc mockMvc, ObjectMapper objectMapper, EventService eventService, UserService userService, LocationService locationService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.eventService = eventService;
        this.userService = userService;
        this.locationService = locationService;
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatCreateLocationReturnsHttpStatus201Created() throws Exception{
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        event = eventService.saveEvent(event);

        LocationDto location = TestDataUtil.createTestLocationDtoA();
        String locationJson = objectMapper.writeValueAsString(location);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatCreateLocationSuccessfullyReturnsSavedLocation() throws Exception{
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        event = eventService.saveEvent(event);

        LocationDto location = TestDataUtil.createTestLocationDtoA();
        String locationJson = objectMapper.writeValueAsString(location);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(location.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.eventId").value(location.getEventId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.eventName").value(location.getEventName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hint").value(location.getHint())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.challenge").value(location.getChallenge())
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatGetLocationByIdReturnsHttpStatus404WhenLocationNotFound() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatGetLocationByIdReturnsHttpStatus200Created() throws Exception{
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        event = eventService.saveEvent(event);

        LocationEntity location = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(location);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatGetLocationByIdReturnsLocationWhenLocationIsFound() throws Exception{
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        event = eventService.saveEvent(event);

        LocationEntity location = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(location);

        LocationDto locationDto = TestDataUtil.createTestLocationDtoA();

        mockMvc.perform(
                MockMvcRequestBuilders.get("/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(location.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.eventId").value(locationDto.getEventId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.eventName").value(locationDto.getEventName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hint").value(locationDto.getHint())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.challenge").value(locationDto.getChallenge())
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatGetLocationByEventReturnsEmptyListWhenNoLocationsAreAvailable() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        String eventJson = objectMapper.writeValueAsString(event);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/locations/by-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.content().json("[]")
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatGetLocationByEventReturnsLocationsWhenAvailable() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        eventService.saveEvent(event);

        LocationEntity location = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(location);

        String eventJson = objectMapper.writeValueAsString(event);

        LocationDto locationDto = TestDataUtil.createTestLocationDtoA();

        mockMvc.perform(
                MockMvcRequestBuilders.get("/locations/by-event")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(eventJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(locationDto.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].eventId").value(locationDto.getEventId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].eventName").value(locationDto.getEventName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hint").value(location.getHint())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].challenge").value(location.getChallenge())
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatGetLocationByEventIdReturnsEmptyListWhenNoLocationsAreAvailable() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/locations/event/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.content().json("[]")
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatGetLocationByEventIdReturnsLocationsWhenAvailable() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        eventService.saveEvent(event);

        LocationEntity location = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(location);

        LocationDto locationDto = TestDataUtil.createTestLocationDtoA();

        mockMvc.perform(
                MockMvcRequestBuilders.get("/locations/event/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].id").value(locationDto.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].eventId").value(locationDto.getEventId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].eventName").value(locationDto.getEventName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].hint").value(location.getHint())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$[0].challenge").value(location.getChallenge())
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatUpdateLocationReturnsHttpStatus200AndDoesntChangeToNullIfNoValueProvided() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        eventService.saveEvent(event);

        LocationEntity location = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(location);

        LocationDto updatedLocation = TestDataUtil.createTestLocationDtoA();
        updatedLocation.setHint("hello");
        updatedLocation.setChallenge("Snap a selfie with one of the lions");

        System.out.println(updatedLocation);

        String locationJson = objectMapper.writeValueAsString(updatedLocation);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(locationJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").value(updatedLocation.getId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.eventId").value(updatedLocation.getEventId())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.eventName").value(updatedLocation.getEventName())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.hint").value(updatedLocation.getHint())
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.challenge").value(updatedLocation.getChallenge())
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatDeleteReturnHttpsStatus404ForNonExistingLocation() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    @WithMockOidcUser(email = "john.doe@example.com", name = "John Doe", roles = {"USER"})
    public void testThatDeleteReturnHttpsStatus204ForExistingLocation() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user = userService.saveUser(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        eventService.saveEvent(event);

        LocationEntity location = TestDataUtil.createTestLocationA(event);
        locationService.saveLocation(location);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/locations/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }
}
