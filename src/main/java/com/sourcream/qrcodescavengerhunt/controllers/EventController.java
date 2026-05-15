package com.sourcream.qrcodescavengerhunt.controllers;

import com.sourcream.qrcodescavengerhunt.domain.dto.EventDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import com.sourcream.qrcodescavengerhunt.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shaded_package.javax.validation.Valid;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class EventController {

    private Mapper<EventEntity, EventDto> eventMapper;
    private EventService eventService;
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);


    public EventController(Mapper<EventEntity, EventDto> eventMapper, EventService eventService) {
        this.eventMapper = eventMapper;
        this.eventService = eventService;
    }

    @PostMapping("/events")
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventDto eventDto){
        if (eventDto == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Event data is required",
                    "timestamp", Instant.now()
            ));
        }

        EventEntity eventEntity = eventMapper.mapFrom(eventDto);
        EventEntity savedEvent = eventService.saveEvent(eventEntity);
        logger.info("New event {} was saved in the database", savedEvent.getId());
        return new ResponseEntity<>(eventMapper.mapTo(savedEvent), HttpStatus.CREATED);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<?> getEvent(@PathVariable("id") Long id) {
        if (id == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Event Id cannot be null",
                    "timestamp", Instant.now()
            ));
        }

        Optional<EventEntity> foundEvent = eventService.getEventById(id);

        return foundEvent.<ResponseEntity<?>>map(eventEntity -> {
            EventDto eventDto = eventMapper.mapTo(eventEntity);
            logger.info("Event {} was retrieved from the database", id);
            return new ResponseEntity<>(eventDto, HttpStatus.OK);
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "Event not found", "timestamp", Instant.now()))
        );

    }

    @GetMapping("/events/by-email/{email}")
    public ResponseEntity<?> getEventsByUser(@PathVariable("email") String email) {
        if (email == null || email.isBlank()){
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Email cannot be blank",
                    "timestamp", Instant.now()
            ));
        }

        List<EventEntity> events = eventService.getEventsByUser(email);
        List<EventDto> eventDtos = events.stream().map(eventMapper::mapTo)
                .collect(Collectors.toList());

        logger.info("Events create by {} were successfully retrieved from the database", email);

        return ResponseEntity.ok(eventDtos);
    }

    @PatchMapping("/events/{id}")
    public ResponseEntity<?> eventUpdate(@PathVariable("id") Long id, @Valid @RequestBody EventDto eventDto){
        if (id == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Event Id cannot be null",
                    "timestamp", Instant.now()
            ));
        }

        if (eventDto == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Event data is required",
                    "timestamp", Instant.now()
            ));
        }

        if(!eventService.isExists(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Event not found",
                    "timestamp", Instant.now()
            ));
        }

        EventEntity eventEntity = eventMapper.mapFrom(eventDto);
        EventEntity updatedEvent = eventService.partialEventUpdate(id, eventEntity);
        logger.info("Event {} was successfully updated", id);
        return ResponseEntity.ok(eventMapper.mapTo(updatedEvent));
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable("id") Long id){
        if (id == null){
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Event Id cannot be null",
                    "timestamp", Instant.now()
            ));
        }

        if(!eventService.isExists(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Event not found",
                    "timestamp", Instant.now()
            ));
        }

        eventService.deleteEvent(id);
        logger.info("Event {} was successfully deleted", id);
        return ResponseEntity.noContent().build();
    }
}
