package com.sourcream.qrcodescavengerhunt.controllers;

import com.sourcream.qrcodescavengerhunt.domain.dto.EventDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import com.sourcream.qrcodescavengerhunt.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class EventController {

    private Mapper<EventEntity, EventDto> eventMapper;
    private EventService eventService;

    public EventController(Mapper<EventEntity, EventDto> eventMapper, EventService eventService) {
        this.eventMapper = eventMapper;
        this.eventService = eventService;
    }

    @PostMapping("/events")
    public ResponseEntity<EventDto> createEvent(@RequestBody EventDto eventDto){
        EventEntity eventEntity = eventMapper.mapFrom(eventDto);
        EventEntity savedEvent = eventService.saveEvent(eventEntity);

        return new ResponseEntity<>(eventMapper.mapTo(savedEvent), HttpStatus.CREATED);
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventDto> getEvent(@PathVariable("id") Long id) {
        Optional<EventEntity> foundEvent = eventService.getEventById(id);
        return foundEvent.map(eventEntity -> {
            EventDto eventDto = eventMapper.mapTo(eventEntity);
            return new ResponseEntity<>(eventDto, HttpStatus.OK);
        }).orElse(
                new ResponseEntity<>(HttpStatus.NOT_FOUND)
        );
    }

    @GetMapping("/events/by-email/{email}")
    public List<EventDto> getEventsByUser(@PathVariable("email") String email) {
        List<EventEntity> events = eventService.getEventsByUser(email);
        return events.stream().map(eventMapper::mapTo)
                .collect(Collectors.toList());
    }

    @PatchMapping("/events/{id}")
    public ResponseEntity<EventDto> eventUpdate(@PathVariable("id") Long id, @RequestBody EventDto eventDto){
        if(!eventService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        EventEntity eventEntity = eventMapper.mapFrom(eventDto);
        EventEntity updatedEvent = eventService.partialEventUpdate(id, eventEntity);
        return new ResponseEntity<>(eventMapper.mapTo(updatedEvent), HttpStatus.OK);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity deleteEvent(@PathVariable("id") Long id){
        eventService.deleteEvent(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
