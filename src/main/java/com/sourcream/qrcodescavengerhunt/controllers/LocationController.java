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
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LocationController {

    private Mapper<LocationEntity, LocationDto> locationMapper;

    private Mapper<EventEntity, EventDto> eventMapper;
    private LocationService locationService;

    private static final Logger logger = LoggerFactory.getLogger(LocationController.class);

    public LocationController(Mapper<LocationEntity, LocationDto> locationMapper,Mapper<EventEntity, EventDto> eventMapper, LocationService locationService) {
        this.locationMapper = locationMapper;
        this.eventMapper = eventMapper;
        this.locationService = locationService;
    }

    @PostMapping("/locations")
    public ResponseEntity<?> createLocation(@Valid @RequestBody LocationDto locationDto) {
        try {
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

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "error", e.getReason(),
                    "timestamp", Instant.now()
            ));
        } catch (Exception e) {
            logger.error("Unexpected error while creating location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unexpected server error",
                    "timestamp", Instant.now()
            ));
        }
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<?> getLocation(@PathVariable("id") Long id) {
        try {
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

        } catch (Exception e) {
            logger.error("Unexpected error retrieving location with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unexpected server error",
                    "timestamp", Instant.now()
            ));
        }
    }


    @GetMapping("/locations/by-event")
    public ResponseEntity<?> getLocationsByEvent(@RequestBody EventDto event){
        try {
            if (event == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Event is null, cannot retrieve locations with null event",
                        "timestamp", Instant.now()
                ));
            }

            EventEntity eventEntity = eventMapper.mapFrom(event);
            List<LocationEntity> locations = locationService.getLocationByEvent(eventEntity);
            List<LocationDto> locationDtos = locations.stream().map(locationMapper::mapTo).collect(Collectors.toList());

            logger.info("Locations form event {} were successfully retrieved from the database", event.getId());

            return ResponseEntity.ok(locationDtos);

        } catch (Exception e) {
            logger.error("Failed to fetch locations for event {}", event != null ? event.getId() : "null", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "unexpected server error",
                    "timestamp", Instant.now()
            ));
        }
    }

    @PatchMapping("/locations/{id}")
    public ResponseEntity<?> locationUpdate(@PathVariable("id") Long id, @RequestBody LocationDto locationDto){
        try {
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

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "error", Objects.requireNonNull(e.getReason()),
                    "timestamp", Instant.now()
            ));
        } catch (Exception e) {
            logger.error("Error while updating location with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unexpected server error",
                    "timestamp", Instant.now()
            ));
        }

    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable("id") Long id){
        try {
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

        } catch (Exception e) {
            logger.error("Unexpected error while deleting location with ID: {}", id != null ? id : "null", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unexpected server error",
                    "timestamp", Instant.now()
            ));
        }


    }
}
