package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.dto.EventProgressOverview;
import com.sourcream.qrcodescavengerhunt.domain.dto.LeaderboardDataDto;
import com.sourcream.qrcodescavengerhunt.domain.dto.LeaderboardEntryDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.*;
import com.sourcream.qrcodescavengerhunt.domain.projection.ScannedLocationCard;
import com.sourcream.qrcodescavengerhunt.repositories.*;
import com.sourcream.qrcodescavengerhunt.services.ProgressService;
import com.sourcream.qrcodescavengerhunt.util.CalculateScore;
import com.sourcream.qrcodescavengerhunt.util.EventValidationUtil;
import com.sourcream.qrcodescavengerhunt.util.UserContext;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProgressServiceImpl implements ProgressService {

    ProgressRepository progressRepository;
    CalculateScore calculateScore;
    EventRepository eventRepository;
    LocationRepository locationRepository;
    UserRepository userRepository;
    ScanRepository scanRepository;
    private EntityManager entityManager;

    UserContext userContext;

    private static final Logger logger = LoggerFactory.getLogger(ProgressServiceImpl.class);

    public ProgressServiceImpl(ProgressRepository progressRepository,
                               CalculateScore calculateScore,
                               EventRepository eventRepository,
                               LocationRepository locationRepository,
                               UserRepository userRepository,
                               ScanRepository scanRepository,
                               EntityManager entityManager,
                               UserContext userContext) {
        this.progressRepository = progressRepository;
        this.calculateScore = calculateScore;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.scanRepository = scanRepository;
        this.entityManager = entityManager;
        this.userContext = userContext;
    }

    @Override
    @Transactional
    public ProgressSummary saveProgress(Long eventID, Long locationID) {
        if (eventID == null || locationID == null){
            logger.warn("Attempted to save progress with null eventID = {} or locationID = {}", eventID, locationID);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event ID and Location ID must not be null");
        }

        String email = null;
        try {
            email = userContext.getCurrentUserEmail();
        } catch (Exception e) {
            logger.warn("No authenticated user found, continuing as anonymous");

        }

        Optional<UserEntity> user = email != null
                ? userRepository.findByEmail(email)
                : Optional.empty();

        progressValidation(eventID);

        if(user.isPresent()){
            if (!scanLocation(user.get().getId(), locationID)){
                logger.warn("Duplicate scan attempt by user = {} for location = {}", user.get().getId(), locationID);
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot scan the same location multiple time");
            }

            ProgressEntity progress = progressEntityBuilder(eventID, locationID, user.get());
            ProgressEntity savedProgress = progressRepository.save(progress);
            logger.info("Saved progress: {}", savedProgress);

            ProgressSummary summary = progressRepository.getProgressSummary(user.get(), savedProgress.getEventEntity());
            summary.setLocationName(savedProgress.getLocationEntity().getName());
            logger.info("Progress Summary: score={}, eventName={}, locationName={}, count={}", summary.getScore(), summary.getEventName(), summary.getLocationName(), summary.getCount());

            return summary;

        } else {
            logger.info("Anonymous user processing on event = {}, location = {}", eventID, locationID);
            return progressSummaryBuilderForAnonymousUser(eventID, locationID);

        }
    }

    @Override
    public Boolean scanLocation(Long userId, Long locationId) {
        if (userId == null || locationId == null) {
            logger.warn("Attempted to scan with null userId = {} or location = {}", userId, locationId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID and Location ID must not be null");
        }

        if (scanRepository.existsByUserIdAndLocationId(userId, locationId)) {
            return false;
        }

        ScanEntity scan = ScanEntity.builder()
                .userId(userId)
                .locationId(locationId)
                .scannedAt(Instant.now())
                .build();

        scanRepository.save(scan);

        return true;
    }

    @Override
    public ProgressSummary getProgressSummary(UserEntity user, EventEntity event) {
        return null;
    }

    @Override
    public EventProgressOverview getEventProgressOverview(UserEntity user, EventEntity event) {
        if (user == null || event == null) {
            logger.warn("User or Event was null while fetching progress overview");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "event and user cannot be null if looking for user progress");
        }

        long scannedCount = progressRepository.countByUserEntityAndEventEntity(user, event);
        long totalLocations = locationRepository.countByEventEntity(event);
        List<ScannedLocationCard> scannedCards = progressRepository.findScanCardsByUserAndEvent(user, event);
        long remaining = totalLocations - scannedCount;

        String nextLocationHint = null;
        if (remaining > 0) {
            Optional<ProgressEntity> latestProgress = progressRepository.findFirstByUserEntityAndEventEntityOrderByIdDesc(user, event);
            logger.info("Latest progress: {}", latestProgress.isPresent() ? latestProgress.get() : "no progress found");
            if (latestProgress.isPresent()) {
                nextLocationHint = latestProgress.get().getLocationEntity().getHint();
            } else {
                nextLocationHint = locationRepository.findTopByEventEntityOrderByIdDesc(event)
                        .map(LocationEntity::getHint)
                        .orElse(null);
            }
        }

        return new EventProgressOverview(
                event.getId(),
                event.getEventName(),
                event.getDescription(),
                scannedCount,
                totalLocations,
                remaining,
                scannedCards,
                nextLocationHint
        );
    }

    @Override
    public Integer getNumberOfScannedQRCodes(UserEntity user, EventEntity event) {
        if (user == null || event == null) {
            logger.warn("Invalid request to count scanned QR codes: user = {} event = {}", user, event);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User and Event must not be null");
        }

        List<ProgressEntity> scannedLocations = progressRepository.findByUserEntityAndEventEntity(user, event);

        int count = (scannedLocations != null) ? scannedLocations.size() : 0;

        logger.info("User {} has scanned {} QR codes for event {}", user.getId(), count, event.getId());
        return count;
    }

    @Override
    public Integer calculateScoreForScan(UserEntity user, EventEntity event) {
        return calculateScore.totalScore(event.getStartTime(), getNumberOfScannedQRCodes(user, event));
    }

    @Cacheable(value = "leaderboard", key = "#eventId")
    @Override
    public List<LeaderboardEntryDto> getLeaderboardForEventsTop10(Long eventId) {
        if (eventId == null) {
            logger.warn("Leaderboard requested with null eventId");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event ID must not be null");
        }

        Optional<EventEntity> event = eventRepository.findById(eventId);
        if (event.isEmpty()){
            logger.info("No event found for leaderboard request, eventId = {}", eventId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + eventId + " was not found");
        }

        List<LeaderboardDataDto> internalData = progressRepository.findTop10LeaderboardDataByEvent(event.get())
                .stream()
                .map(projection -> {
                    if (projection == null) {
                        logger.warn("Null leaderboard projection found for eventId = {}", eventId);
                        return null;
                    }
                    return LeaderboardDataDto.builder()
                            .fullname(projection.getFullname())
                            .email(projection.getEmail())
                            .score(projection.getTotalScore())
                            .scannedLocations(projection.getLocationsScanned())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        List<LeaderboardEntryDto> result = disambiguateNames(internalData);
        logger.info("Returning leaderboard for eventId = {}, entries = {}", eventId, result.size());

        return result;
    }

    @Override
    public List<LeaderboardDataDto> getFullLeaderboardData(Long eventId) {
        if (eventId == null) {
            logger.warn("Leader (full) requested with null eventId");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event ID must not be null");
        }

        Optional<EventEntity> event = eventRepository.findById(eventId);
        if (event.isEmpty()){
            logger.info("No event found for full leaderboard request, eventId={}", eventId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + eventId + " was not found");
        }

        List<LeaderboardDataDto> data = progressRepository.findAllLeaderboardDataByEvent(event.get())
                .stream()
                .map(projection -> LeaderboardDataDto.builder()
                        .fullname(projection.getFullname())
                        .email(projection.getEmail())
                        .score(projection.getTotalScore())
                        .scannedLocations(projection.getLocationsScanned())
                        .build())
                .toList();

        logger.info("Returning full leaderboard for eventId = {}, entries = {}", eventId, data.size());
        return data;
    }

    @Override
    public LeaderboardEntryDto getUserLeaderboardPosition(UserEntity user, Long eventId) {
        if (user == null || eventId == null) {
            logger.warn("Leaderboard position requested with null user or eventId (user={}, eventId={})",
                    user, eventId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User and Event ID must not be null");
        }

        LeaderboardDataDto userData = getIndividualUserStats(user, eventId);

        if (userData.getScore() == 0L && userData.getScannedLocations() == 0L){
            return LeaderboardEntryDto.builder()
                    .fullname(user.getFullname())
                    .score(0L)
                    .scannedLocations(0L)
                    .build();
        }

        List<LeaderboardDataDto> leaderboardData = getFullLeaderboardData(eventId);

        if (leaderboardData.isEmpty()) {
            logger.info("Leaderboard for event {} is empty, returning user {} with zeroed stats",
                    eventId, user.getEmail());
            return LeaderboardEntryDto.builder()
                    .fullname(user.getFullname())
                    .score(userData.getScore())
                    .scannedLocations(userData.getScannedLocations())
                    .build();
        }

        return convertToEntry(userData, leaderboardData);
    }

    @Override
    public LeaderboardDataDto getIndividualUserStats(UserEntity user, Long eventId) {
        if (user == null || eventId == null) {
            logger.warn("Attempted to retrieve individual stats with null user or eventId (user={}, eventId={})",
                    user, eventId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User and Event ID must not be null");
        }

        return progressRepository.findUserLeaderboardData(user.getEmail(), eventId)
                .map(projection -> {
                    if (projection == null) {
                        logger.warn("Null leaderboard projection for user = {} in event = {}", user.getEmail(), eventId);
                        return LeaderboardDataDto.builder()
                                .fullname(user.getFullname())
                                .email(user.getEmail())
                                .score(0L)
                                .scannedLocations(0L)
                                .build();
                    }
                    return LeaderboardDataDto.builder()
                            .fullname(Optional.ofNullable(projection.getFullname()).orElse(user.getFullname()))
                            .email(Optional.ofNullable(projection.getEmail()).orElse(user.getEmail()))
                            .score(Optional.ofNullable(projection.getTotalScore()).orElse(0L))
                            .scannedLocations(Optional.ofNullable(projection.getLocationsScanned()).orElse(0L))
                            .build();
                })
                .orElseGet(() -> LeaderboardDataDto.builder()
                        .fullname(user.getFullname())
                        .email(user.getEmail())
                        .score(0L)
                        .scannedLocations(0L)
                        .build());

    }

    private LeaderboardEntryDto convertToEntry(LeaderboardDataDto data, List<LeaderboardDataDto> fullList) {
        if (fullList == null || fullList.isEmpty()) {
            return LeaderboardEntryDto.builder()
                    .fullname(data.getFullname())
                    .score(data.getScore())
                    .scannedLocations(data.getScannedLocations())
                    .build();
        }

        Map<String, Long> nameCounts = fullList.stream()
                .collect(Collectors.groupingBy(
                        LeaderboardDataDto::getFullname,
                        Collectors.counting()
                ));

        String displayName = data.getFullname();
        if (nameCounts.getOrDefault(data.getFullname(), 0L) > 1) {
            String emailPrefix = data.getEmail().split("@")[0];
            displayName = data.getFullname() + " (" + emailPrefix + ")";
        }

        return LeaderboardEntryDto.builder()
                .fullname(displayName)
                .score(data.getScore())
                .scannedLocations(data.getScannedLocations())
                .build();
    }

    private List<LeaderboardEntryDto> disambiguateNames(List<LeaderboardDataDto> internalData) {
        Map<String, Long> nameCounts = internalData.stream()
                .collect(Collectors.groupingBy(
                        LeaderboardDataDto::getFullname,
                        Collectors.counting()
                ));

        return internalData.stream()
                .map(data -> {
                    String displayName = data.getFullname();
                    if (nameCounts.get(data.getFullname()) > 1) {
                        String emailPrefix = data.getEmail().split("@")[0];
                        displayName = data.getFullname() + " (" + emailPrefix + ")";
                    }
                    return LeaderboardEntryDto.builder()
                            .fullname(displayName)
                            .score(data.getScore())
                            .scannedLocations(data.getScannedLocations())
                            .build();
                })
                .toList();
    }


    public ProgressEntity progressEntityBuilder(Long eventID, Long locationID, UserEntity user) {
        if (eventID == null || locationID == null || user == null) {
            logger.warn("ProgressEntity build failed due to null input (eventID = {}, locationID = {}, user = {})",
                    eventID, locationID, user != null ? user.getEmail() : "null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event ID, Location ID, and User must not be null");
        }

        EventEntity event = eventRepository.findById(eventID).orElse(null);
        if (event == null) {
            logger.warn("No event found for ID = {}", eventID);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + eventID + " not found");
        }

        LocationEntity location = locationRepository.findById(locationID).orElse(null);
        if (location == null) {
            logger.warn("No location found for ID={}", locationID);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Location " + locationID + " not found");
        }

        int score;
        try {
            score = calculateScoreForScan(user, event);
        } catch (Exception e) {
            logger.error("Score calculation failed for user={} eventID={}", user.getEmail(), eventID, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Score calculation failed");
        }

        String scanDate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);

        return ProgressEntity.builder()
                .userEntity(user)
                .eventEntity(event)
                .locationEntity(location)
                .scanTime(scanDate)
                .score(score)
                .build();
    }

    public ProgressSummary progressSummaryBuilderForAnonymousUser(Long eventID, Long locationID){
        if (eventID == null || locationID == null) {
            logger.warn("Anonymous progress summary requested with null eventID or null locationID");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event and Location ID must not be null");
        }

        EventEntity event = eventRepository.findById(eventID).orElse(null);
        if (event == null) {
            logger.warn("No event found for anonymous progress summary, eventID={}", eventID);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + eventID + " not found");
        }

        try {
            event = entityManager.merge(event);
        } catch (IllegalArgumentException e) {
            logger.error("Entity merge failed for anonymous user eventID={}", eventID, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to prepare event entity");
        }


        LocationEntity location = locationRepository.findById(locationID)
                .orElseThrow(() -> {
                    logger.warn("Np location found for ID={}", locationID);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Location " + locationID + " not found");
                });
        String locationName = location.getName();

        List<LocationEntity> locations = locationRepository.findByEventEntity(event);
        long numberOfLocations = (locations != null) ? locations.size() : 0L;

        return ProgressSummary.builder()
                .eventName(event.getEventName())
                .locationName(locationName)
                .count(numberOfLocations)
                .score(null)
                .build();
    }

    private void progressValidation(Long eventId) {
        EventEntity event = eventRepository.findById(eventId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Event " + eventId + " not found")
        );

        String startDate = event.getStartTime();
        String endDate = event.getEndTime();

        EventValidationUtil.validateEventIsActive(startDate, endDate);
    }
}
