package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import com.sourcream.qrcodescavengerhunt.repositories.EventRepository;
import com.sourcream.qrcodescavengerhunt.repositories.LocationRepository;
import com.sourcream.qrcodescavengerhunt.services.LocationService;
import com.sourcream.qrcodescavengerhunt.util.QRCodeGenerator;
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

    QRCodeGenerator qrCodeGenerator;
    private static final Logger logger = LoggerFactory.getLogger(LocationServiceImpl.class);

    public LocationServiceImpl(LocationRepository locationRepository, EventRepository eventRepository, QRCodeGenerator qrCodeGenerator) {
        this.locationRepository = locationRepository;
        this.eventRepository = eventRepository;
        this.qrCodeGenerator = qrCodeGenerator;
    }

    @Transactional
    @Override
    public LocationEntity saveLocation(LocationEntity locationEntity) {
        QRCodeGenerator.QRCodeUploadResult uploadResult = null;
        
        try {
            if (locationEntity == null || locationEntity.getEventEntity() == null || locationEntity.getEventEntity().getId() == null) {
                logger.warn("Attempted to save new location with missing event reference");
                throw new IllegalArgumentException("Location and Event ID must not be null");
            }

            Long eventId = locationEntity.getEventEntity().getId();

            EventEntity event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Event not found"));

            locationEntity.setEventEntity(event);

            LocationEntity savedLocation = locationRepository.save(locationEntity);

            String locationPath = "progress/"
                    + savedLocation.getEventEntity().getId() + "/"
                    + savedLocation.getId();

            uploadResult = qrCodeGenerator.generateQRCodeAndUploadWithMetadata(locationPath);

            savedLocation.setQrCodeUrl(uploadResult.downloadUrl());

            savedLocation = locationRepository.save(savedLocation);

            logger.info("Location {} for event {} saved with QR code at {}",
                    savedLocation.getId(), savedLocation.getEventEntity().getId(), uploadResult);

            return savedLocation;

        } catch (IllegalArgumentException e) {
            logger.warn("Validation failed while saving location: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error while saving location", e);
            if (uploadResult != null) {
                qrCodeGenerator.deleteUploadedFile(uploadResult.fileName());
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save location", e);
        }

    }

    @Override
    public List<LocationEntity> getLocationByEvent(EventEntity event) {
        try {
            if (event == null) {
                logger.warn("Attempted to retrieve locations with null event");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event must not be null");
            }

            List<LocationEntity> locations = locationRepository.findByEventEntity(event);

            if (locations.isEmpty()) {
                logger.info("No locations found for event {}", event.getId());
            }

            return locations;

        } catch (Exception e) {
            logger.error("Unexpected error while retrieving locations for event {}", event != null ? event.getId() : "null", e);

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve locations for event");
        }
    }

    @Override
    public Optional<LocationEntity> getLocationById(Long id) {
        try {
            if (id == null || id <= 0) {
                logger.warn("Attempted to retrieve location with invalid id: {}", id);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location ID must be a positive number");
            }

            return locationRepository.findById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve location");
        }
    }

    @Override
    public Boolean isExists(Long id) {
        try {
            if (id == null || id <= 0) {
                logger.warn("Attempted to check existence of location with invalid id: {}", id);
                return false;
            }

            return locationRepository.existsById(id);

        } catch (Exception e) {
            logger.error("Unexpected error while checking existence of location {}", id, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to check location existence"
            );
        }
    }


    @Override
    public LocationEntity updateLocation(Long id, LocationEntity location) {
        try {
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

        } catch (ResponseStatusException e) {
            throw e;

        } catch (Exception e) {
            logger.error("Unexpected error while updating location {}", id, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to update location"
            );
        }
    }


    @Override
    public void deleteLocation(Long id) {
        try {
            if (id == null || id <= 0) {
                logger.warn("Attempted to delete location with invalid id: {}", id);
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Location ID must be a positive number"
                );
            }

            if (!locationRepository.existsById(id)) {
                logger.warn("Attempted to delete non-existent location {}", id);
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Location with ID " + id + " not found"
                );
            }

            locationRepository.deleteById(id);
            logger.info("Location {} successfully deleted", id);

        } catch (ResponseStatusException e) {
            throw e;

        } catch (Exception e) {
            logger.error("Unexpected error while deleting location {}", id, e);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to delete location"
            );
        }
    }

}
