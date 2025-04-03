package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.*;
import com.sourcream.qrcodescavengerhunt.repositories.EventRepository;
import com.sourcream.qrcodescavengerhunt.repositories.LocationRepository;
import com.sourcream.qrcodescavengerhunt.repositories.ProgressRepository;
import com.sourcream.qrcodescavengerhunt.repositories.UserRepository;
import com.sourcream.qrcodescavengerhunt.services.ProgressService;
import com.sourcream.qrcodescavengerhunt.util.CalculateScore;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ProgressServiceImpl implements ProgressService {

    ProgressRepository progressRepository;
    CalculateScore calculateScore;
    EventRepository eventRepository;
    LocationRepository locationRepository;

    UserRepository userRepository;
    private EntityManager entityManager;

    private static final Logger logger = LoggerFactory.getLogger(ProgressServiceImpl.class);

    public ProgressServiceImpl(ProgressRepository progressRepository, CalculateScore calculateScore, EventRepository eventRepository, LocationRepository locationRepository, UserRepository userRepository, EntityManager entityManager) {
        this.progressRepository = progressRepository;
        this.calculateScore = calculateScore;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public ProgressSummary saveProgress(Long eventID, Long locationID) {
        if (eventID == null || locationID == null){
            return null;
        }

        ProgressSummary summary = null;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        OidcUser oidcUser = null;

        if (authentication != null && authentication.getPrincipal() instanceof OidcUser){
            oidcUser = (OidcUser) authentication.getPrincipal();
        }

        Optional<UserEntity> user = oidcUser != null
                ? userRepository.findByEmail(oidcUser.getEmail())
                : Optional.empty();

       if(user.isPresent()){
            ProgressEntity progress = progressEntityBuilder(eventID, locationID, user.get());
            ProgressEntity savedProgress = progressRepository.save(progress);
            summary = progressRepository.getProgressSummary(user.get(), savedProgress.getEventEntity());
            logger.info("Progress Summary: score={}, eventName={}, count={}", summary.getScore(), summary.getEventName(), summary.getCount());

        } else {
            summary = progressSummaryBuilderForAnonymousUser(eventID);

        }

       return summary;
    }

    @Override
    public ProgressSummary getProgressSummary(UserEntity user, EventEntity event) {
        return null;
    }

    @Override
    public Integer getNumberOfScannedQRCodes(UserEntity user, EventEntity event) {
        List<ProgressEntity> scannedLocations = progressRepository.findByUserEntityAndEventEntity(user, event);
        int noOfLocationsScanned = 0;
        if(!scannedLocations.isEmpty()){
            noOfLocationsScanned = scannedLocations.size();
        }
        return noOfLocationsScanned;
    }

    @Override
    public Integer calculateScoreForScan(UserEntity user, EventEntity event) {
        return calculateScore.totalScore(event.getStartTime(), getNumberOfScannedQRCodes(user, event));
    }


    public ProgressEntity progressEntityBuilder(Long eventID, Long locationID, UserEntity user) {
        EventEntity event = eventRepository.findById(eventID).orElse(null);
        logger.info("Authenticated User: Event = " + event);
        LocationEntity location = locationRepository.findById(locationID).orElse(null);
        logger.info("Authenticated User: Location = " + location);

        event = entityManager.merge(event);
        location = entityManager.merge(location);

        location.setEventEntity(null);
        int score = calculateScoreForScan(user, event);
        event.setUserEntity(null);
        String scanDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);

        return ProgressEntity.builder()
                .userEntity(user)
                .eventEntity(event)
                .locationEntity(location)
                .scanTime(scanDate)
                .score(score)
                .build();
    }

    public ProgressSummary progressSummaryBuilderForAnonymousUser(Long eventID){
        EventEntity event = eventRepository.findById(eventID).orElse(null);
        logger.info("Anonymous User: Event = " + event);
        event = entityManager.merge(event);

        long numberOfLocations = 0L;
        if(event != null){
            List<LocationEntity> locations = locationRepository.findByEventEntity(event);
            numberOfLocations = locations.size();

            return ProgressSummary.builder()
                    .eventName(event.getEventName())
                    .count(numberOfLocations)
                    .score(null)
                    .build();
        }else {
            return null;
        }


    }
}
