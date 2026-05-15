package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.repositories.EventRepository;
import com.sourcream.qrcodescavengerhunt.repositories.LocationRepository;
import com.sourcream.qrcodescavengerhunt.repositories.ScanRepository;
import com.sourcream.qrcodescavengerhunt.services.LocationService;
import com.sourcream.qrcodescavengerhunt.util.AccessControlService;
import com.sourcream.qrcodescavengerhunt.util.QRCodeGenerator;
import com.sourcream.qrcodescavengerhunt.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {

    LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final ScanRepository scanRepository;
    private final UserContext userContext;
    private final AccessControlService accessControlService;

    QRCodeGenerator qrCodeGenerator;
    private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);

    public LocationServiceImpl(LocationRepository locationRepository, EventRepository eventRepository, ScanRepository scanRepository, UserContext userContext, QRCodeGenerator qrCodeGenerator, AccessControlService accessControlService) {
        this.locationRepository = locationRepository;
        this.eventRepository = eventRepository;
        this.scanRepository = scanRepository;
        this.userContext = userContext;
        this.qrCodeGenerator = qrCodeGenerator;
        this.accessControlService = accessControlService;
    }

    @Transactional
    @Override
    public LocationEntity saveLocation(LocationEntity locationEntity) {
        QRCodeGenerator.QRCodeUploadResult uploadResult = null;
        if (locationEntity == null || locationEntity.getEventEntity() == null || locationEntity.getEventEntity().getId() == null) {
            logger.warn("Attempted to save new location with missing event reference");
            throw new IllegalArgumentException("Location and Event ID must not be null");
        }

        Long eventId = locationEntity.getEventEntity().getId();

        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        accessControlService.requireEventOwnerOrAdmin(event);

        locationEntity.setEventEntity(event);

        LocationEntity savedLocation = locationRepository.save(locationEntity);

        String locationPath = "scan/"
                + savedLocation.getEventEntity().getId() + "/"
                + savedLocation.getId();

        uploadResult = qrCodeGenerator.generateQRCodeAndUploadWithMetadata(locationPath);

        savedLocation.setQrCodeUrl(uploadResult.downloadUrl());

        savedLocation = locationRepository.save(savedLocation);

        return savedLocation;
    }

    @Override
    public List<LocationEntity> getLocationByEvent(EventEntity event) {
        if (event == null) {
            logger.warn("Attempted to retrieve locations with null event");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event must not be null");
        }

        accessControlService.requireEventOwnerOrAdmin(event);

        List<LocationEntity> locations = locationRepository.findByEventEntity(event);

        logger.info("Locations {}", locations);

        if (locations.isEmpty()) {
            logger.info("No locations found for event {}", event.getId());
        }

        return locations;

    }

    @Override
    public List<LocationEntity> getLocationByEventId(Long id) {
        if (id == null || id == 0) {
            logger.warn("Attempted to retrieve locations with null event id");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event id must not be null");
        }

        EventEntity event = eventRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Event not found")
        );

        accessControlService.requireEventOwnerOrAdmin(event);

        List<LocationEntity> locations = locationRepository.findByEventEntityId(id);

        if (locations.isEmpty()) {
            logger.info("No locations found for event {}", id);
        }

        return locations;

    }

    @Override
    public Optional<LocationEntity> getLocationById(Long id) {
        if (id == null || id <= 0) {
            logger.warn("Attempted to retrieve location with invalid id: {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location ID must be a positive number");
        }

        LocationEntity location = locationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Location not found"
                ));

        UserEntity currentUser = userContext.getCurrentUser();

        boolean hasFoundLocation = scanRepository.existsByUserIdAndLocationId(currentUser.getId(), id);

        accessControlService.requireLocationAccess(location, hasFoundLocation);

        return Optional.of(location);

    }

    @Override
    public Boolean isExists(Long id) {
        if (id == null || id <= 0) {
            logger.warn("Attempted to check existence of location with invalid id: {}", id);
            return false;
        }

        return locationRepository.existsById(id);
    }


    @Override
    public LocationEntity updateLocation(Long id, LocationEntity location) {
        if (id == null || id <= 0) {
            logger.warn("Attempted to update location with invalid id: {}", id);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Location ID must be a positive number"
            );
        }

        if (location == null) {
            logger.warn("Attempted to update location {} with null payload", id);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Location update payload must not be null"
            );
        }

        return locationRepository.findById(id)
                .map(existingLocation -> {
                    accessControlService.requireEventOwnerOrAdmin(existingLocation.getEventEntity());

                    boolean updated = false;

                    if (location.getName() != null && !location.getName().isBlank()) {
                        existingLocation.setName(location.getName());
                        updated = true;
                    }

                    if (location.getHint() != null && !location.getHint().isBlank()) {
                        existingLocation.setHint(location.getHint());
                        updated = true;
                    }

                    if (location.getChallenge() != null && !location.getChallenge().isBlank()) {
                        existingLocation.setChallenge(location.getChallenge());
                        updated = true;
                    }

                    if (!updated) {
                        logger.warn("No valid fields provided to update location {}", id);
                        throw new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "No valid fields provided for update"
                        );
                    }

                    LocationEntity saved = locationRepository.save(existingLocation);
                    logger.info("Location {} successfully updated", id);
                    return saved;

                })
                .orElseThrow(() -> {
                    logger.warn("Attempted to update non-existent location {}", id);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Location with ID " + id + " not found"
                    );
                });
    }


    @Override
    public void deleteLocation(Long id) {
        if (id == null || id <= 0) {
            logger.warn("Attempted to delete location with invalid id: {}", id);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Location ID must be a positive number"
            );
        }

        Optional<LocationEntity> existingLocation = locationRepository.findById(id);
        if (existingLocation.isEmpty()) {
            logger.warn("Attempted to delete non-existent location {}", id);
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Location with ID " + id + " not found"
            );
        }

        accessControlService.requireEventOwnerOrAdmin(existingLocation.get().getEventEntity());

        locationRepository.deleteById(id);
        logger.info("Location {} successfully deleted", id);
    }

}
