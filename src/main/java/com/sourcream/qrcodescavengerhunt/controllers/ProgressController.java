package com.sourcream.qrcodescavengerhunt.controllers;

import com.sourcream.qrcodescavengerhunt.domain.dto.EventProgressOverview;
import com.sourcream.qrcodescavengerhunt.domain.dto.LeaderboardEntryDto;
import com.sourcream.qrcodescavengerhunt.domain.dto.ProgressSummaryDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import com.sourcream.qrcodescavengerhunt.services.EventService;
import com.sourcream.qrcodescavengerhunt.services.ProgressService;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import com.sourcream.qrcodescavengerhunt.services.impl.ProgressServiceImpl;
import com.sourcream.qrcodescavengerhunt.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
public class ProgressController {

    private Mapper<ProgressSummary, ProgressSummaryDto> progressMapper;
    private ProgressService progressService;
    private UserService userService;

    private EventService eventService;
    private UserContext userContext;
    private static final Logger logger = LoggerFactory.getLogger(ProgressController.class);

    public ProgressController(Mapper<ProgressSummary, ProgressSummaryDto> progressMapper, ProgressService progressService, UserService userService, EventService eventService, UserContext userContext) {
        this.progressMapper = progressMapper;
        this.progressService = progressService;
        this.userService = userService;
        this.eventService = eventService;
        this.userContext = userContext;
    }

    @PostMapping("/progress/{eventId}/{locationId}")
    public ResponseEntity<?> createProgress(@PathVariable("eventId") Long eventId,
                                                             @PathVariable("locationId") Long locationId){

        if (eventId == null || locationId == null) {
            logger.warn("Invalid request: eventId = {} or locationId = {} is null", eventId, locationId);
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "You must provide both an event ID and a location ID",
                    "timestamp", Instant.now()
            ));
        }

        ProgressSummary summary = progressService.saveProgress(eventId, locationId);
        logger.info("Progress Summary (controller): score={}, eventName={}, count={}", summary.getScore(), summary.getEventName(), summary.getCount());

        if (summary == null) {
            logger.warn("Progress summary was null for eventId={}, locationId={}", eventId, locationId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "We could not save your progress. Please try again later.",
                    "timestamp", Instant.now()
            ));
        }

        ProgressSummaryDto responseDto = progressMapper.mapTo(summary);

        if (summary.getScore() == null || summary.getScore() == 0L){
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        }
    }

    @GetMapping("/progress/events/{id}")
    public ResponseEntity<?> getEventProgress(@PathVariable Long id) {
        String email = userContext.getCurrentUserEmail();
        UserEntity user = userService.getUserByEmail(email).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "User not found"
        ));

        EventEntity event = eventService.getEventById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Event not found"
        ));

        EventProgressOverview overview = progressService.getEventProgressOverview(user, event);

        return ResponseEntity.ok(overview);
    }

    @GetMapping("/progress/{eventId}/top")
    public ResponseEntity<?> getLeaderboard(@PathVariable Long eventId) {
        try {
            if (eventId == null || eventId <= 0) {
                logger.warn("Invalid leaderboard request: eventId = {}", eventId);
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Event ID must be a positive number",
                        "timestamp", Instant.now()
                ));
            }

            List<LeaderboardEntryDto> leaderboard = progressService.getLeaderboardForEventsTop10(eventId);

            if (leaderboard.isEmpty()) {
                logger.info("No leaderboard data found for eventId = {}", eventId);
                return ResponseEntity.ok(Map.of(
                        "message", "No leaderboard data available yet for this event",
                        "timestamp", Instant.now()
                ));
            }

            return ResponseEntity.ok(leaderboard);

        } catch (ResponseStatusException e) {
            logger.error("Error fetching leaderboard for eventId={}: {}", eventId, e.getReason());
            return ResponseEntity.status(e.getStatusCode()).body(Map.of(
                    "error", Objects.requireNonNull(e.getReason()),
                    "timestamp", Instant.now()
            ));
        } catch (Exception e) {
            logger.error("Unexpected error retrieving leaderboard for eventId={}", eventId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unexpected server error while retrieving leaderboard",
                    "timestamp", Instant.now()
            ));
        }

    }

    @GetMapping("/progress/{eventId}/leaderboard/me")
    public ResponseEntity<?> getMyLeaderboardPosition(@PathVariable Long eventId, Authentication authentication) {
        try {
            if (eventId == null || eventId <= 0){
                logger.warn("Invalid eventId provided for leaderboard request: {}", eventId);
                return ResponseEntity.badRequest().body(Map.of(
                        "error", "Event ID must be a positive number",
                        "timestamp", Instant.now()
                ));
            }

            UserEntity user = userContext.getCurrentUser();
            LeaderboardEntryDto userEntry = progressService.getUserLeaderboardPosition(user, eventId);

            return ResponseEntity.ok(userEntry);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error fetching leaderboard position for eventId={}", eventId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Unexpected server error while retrieving leaderboard position",
                    "timestamp", Instant.now()
            ));
        }




    }


}
