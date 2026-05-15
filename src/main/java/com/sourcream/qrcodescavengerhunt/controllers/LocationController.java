package com.sourcream.qrcodescavengerhunt.controllers;

import com.sourcream.qrcodescavengerhunt.domain.dto.EventDto;
import com.sourcream.qrcodescavengerhunt.domain.dto.LocationDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import com.sourcream.qrcodescavengerhunt.services.LocationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LocationController {

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);
    private final Mapper<LocationEntity, LocationDto> locationMapper;
    private final Mapper<EventEntity, EventDto> eventMapper;
    private final LocationService locationService;

    public LocationController(Mapper<LocationEntity, LocationDto> locationMapper,Mapper<EventEntity, EventDto> eventMapper, LocationService locationService) {
        this.locationMapper = locationMapper;
        this.eventMapper = eventMapper;
        this.locationService = locationService;
    }

    @PostMapping("/locations")
    public ResponseEntity<?> createLocation(@Valid @RequestBody LocationDto locationDto) throws Exception {
        if (locationDto == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Location data is required",
                    "timestamp", Instant.now()
            ));
        }

        LocationEntity location = locationMapper.mapFrom(locationDto);
        LocationEntity savedLocation = locationService.saveLocation(location);

        logger.info("Location {} created for event {}",
                savedLocation.getId(),
                savedLocation.getEventEntity().getId());

        return new ResponseEntity<>(
                locationMapper.mapTo(savedLocation),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<?> getLocation(@PathVariable("id") Long id) {
        Optional<LocationEntity> foundLocation = locationService.getLocationById(id);

        return foundLocation
                .<ResponseEntity<?>>map(locationEntity -> {
                    LocationDto locationDto = locationMapper.mapTo(locationEntity);
                    logger.info("Location {} retrieved successfully", id);
                    return new ResponseEntity<>(locationDto, HttpStatus.OK);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                        "error", "Location not found",
                        "timestamp", Instant.now()
                )));

    }


    @GetMapping("/locations/by-event")
    public ResponseEntity<?> getLocationsByEvent(@RequestBody EventDto event){
        if (event == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Event is null, cannot retrieve locations with null event",
                    "timestamp", Instant.now()
            ));
        }

        System.out.println(event);

        EventEntity eventEntity = eventMapper.mapFrom(event);
        List<LocationEntity> locations = locationService.getLocationByEvent(eventEntity);
        List<LocationDto> locationDtos = locations.stream().map(locationMapper::mapTo).collect(Collectors.toList());

        logger.info("Locations form event {} were successfully retrieved from the database", event.getId());

        return ResponseEntity.ok(locationDtos);

    }

    @GetMapping("/locations/event/{id}")
    public ResponseEntity<?> getLocationsByEventId(@PathVariable("id") Long id) {
        if (id == null || id == 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Event id is null, cannot retrieve locations with null event id",
                    "timestamp", Instant.now()
            ));
        }

        List<LocationEntity> locations = locationService.getLocationByEventId(id);
        List<LocationDto> locationDtos = locations.stream().map(locationMapper::mapTo).toList();

        return ResponseEntity.ok(locationDtos);
    }

    @PatchMapping("/locations/{id}")
    public ResponseEntity<?> locationUpdate(@PathVariable("id") Long id, @RequestBody LocationDto locationDto){
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Location ID cannot be null, and must be a positive number",
                    "timestamp", Instant.now()
            ));
        }

        if (locationDto == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Location data is required",
                    "timestamp", Instant.now()
            ));
        }

        if(!locationService.isExists(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Location not found",
                    "timestamp", Instant.now()
            ));
        }

        LocationEntity locationEntity = locationMapper.mapFrom(locationDto);
        LocationEntity updatedLocation = locationService.updateLocation(id, locationEntity);

        logger.info("Event {} was successfully updated", id);

        return ResponseEntity.ok(locationMapper.mapTo(updatedLocation));

    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable("id") Long id){
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Location ID cannot be null, and must be a positive number",
                    "timestamp", Instant.now()
            ));
        }

        if(!locationService.isExists(id)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Location not found",
                    "timestamp", Instant.now()
            ));
        }

        locationService.deleteLocation(id);
        logger.info("Location {} was successfully deleted", id);
        return ResponseEntity.noContent().build();
    }
}
