package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.*;
import com.sourcream.qrcodescavengerhunt.repositories.EventRepository;
import com.sourcream.qrcodescavengerhunt.repositories.LocationRepository;
import com.sourcream.qrcodescavengerhunt.repositories.ProgressRepository;
import com.sourcream.qrcodescavengerhunt.services.ProgressService;
import com.sourcream.qrcodescavengerhunt.util.CalculateScore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ProgressServiceImpl implements ProgressService {

    ProgressRepository progressRepository;
    CalculateScore calculateScore;
    EventRepository eventRepository;
    LocationRepository locationRepository;

    public ProgressServiceImpl(ProgressRepository progressRepository, CalculateScore calculateScore, EventRepository eventRepository, LocationRepository locationRepository) {
        this.progressRepository = progressRepository;
        this.calculateScore = calculateScore;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @Override
    public ProgressSummary saveProgress(Long eventID, Long locationID) {
        ProgressEntity progress = null;
        if (eventID != null && locationID != null){
            progress = progressEntityBuilder(eventID, locationID);
        }
        assert progress != null;
        ProgressEntity savedProgress = progressRepository.save(progress);

        return progressRepository.getProgressSummary(savedProgress.getUserEntity(), savedProgress.getEventEntity());
    }

    @Override
    public ProgressSummary getProgressSummary(UserEntity user, EventEntity event) {
        return progressRepository.getProgressSummary(user, event);
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
        return getLatestScore(user, event) + calculateScore.totalScore(event.getStartTime(), getNumberOfScannedQRCodes(user, event));
    }

    @Override
    public Integer getLatestScore(UserEntity user, EventEntity event) {
        Optional<ProgressEntity> latestScore = progressRepository.findLatestScoreByUserEntityAndEventEntity(user, event);
        return latestScore.map(ProgressEntity::getScore).orElse(0);
    }

    public ProgressEntity progressEntityBuilder(Long eventID, Long locationID) {
        EventEntity event = eventRepository.findById(eventID).orElse(null);
        LocationEntity location = locationRepository.findById(locationID).orElse(null);
        location.setEventEntity(null);
        int score = getLatestScore(event.getUserEntity(), event);
        event.setUserEntity(null);
        String scanDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);

        return ProgressEntity.builder()
                .userEntity(event.getUserEntity())
                .eventEntity(event)
                .locationEntity(location)
                .scanTime(scanDate)
                .score(score)
                .build();
    }
}
