package com.sourcream.qrcodescavengerhunt.controllers;

import com.sourcream.qrcodescavengerhunt.domain.dto.EventDto;
import com.sourcream.qrcodescavengerhunt.domain.dto.LocationDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import com.sourcream.qrcodescavengerhunt.services.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class LocationController {

    private Mapper<LocationEntity, LocationDto> locationMapper;

    private Mapper<EventEntity, EventDto> eventMapper;
    private LocationService locationService;

    public LocationController(Mapper<LocationEntity, LocationDto> locationMapper,Mapper<EventEntity, EventDto> eventMapper, LocationService locationService) {
        this.locationMapper = locationMapper;
        this.eventMapper = eventMapper;
        this.locationService = locationService;
    }

    @PostMapping("/locations")
    public ResponseEntity<LocationDto> createLocation(@RequestBody LocationDto locationDto) throws Exception {
        LocationEntity location = locationMapper.mapFrom(locationDto);
        LocationEntity savedLocation = locationService.saveLocation(location);

        return new ResponseEntity<>(locationMapper.mapTo(savedLocation), HttpStatus.CREATED);
    }

    @GetMapping("/locations/{id}")
    public ResponseEntity<LocationDto> getLocation(@PathVariable("id") Long id){
        Optional<LocationEntity> foundLocation = locationService.getLocationById(id);
        return foundLocation.map(locationEntity -> {
            LocationDto locationDto = locationMapper.mapTo(locationEntity);
            return new ResponseEntity<>(locationDto, HttpStatus.OK);
        }).orElse(
                new ResponseEntity<>(HttpStatus.NOT_FOUND)
        );
    }

    @GetMapping("/locations/by-event/")
    public List<LocationDto> getLocationsByEvent(@RequestBody EventDto event){
        EventEntity eventEntity = eventMapper.mapFrom(event);
        List<LocationEntity> locations = locationService.getLocationByEvent(eventEntity).orElse(Collections.emptyList());
        return locations.stream().map(locationMapper::mapTo)
                .collect(Collectors.toList());
    }

    @PatchMapping("/locations/{id}")
    public ResponseEntity<LocationDto> locationUpdate(@PathVariable("id") Long id, @RequestBody LocationDto locationDto){
        if(!locationService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        LocationEntity locationEntity = locationMapper.mapFrom(locationDto);
        LocationEntity updatedLocation = locationService.updateLocation(id, locationEntity);
        return new ResponseEntity<>(locationMapper.mapTo(updatedLocation), HttpStatus.OK);
    }

    @DeleteMapping("/locations/{id}")
    public ResponseEntity deleteLocation(@PathVariable("id") Long id){
        locationService.deleteLocation(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
