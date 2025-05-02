package com.sourcream.qrcodescavengerhunt.controllers;

import com.sourcream.qrcodescavengerhunt.domain.dto.LeaderboardEntryDto;
import com.sourcream.qrcodescavengerhunt.domain.dto.ProgressSummaryDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import com.sourcream.qrcodescavengerhunt.services.ProgressService;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import com.sourcream.qrcodescavengerhunt.services.impl.ProgressServiceImpl;
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

import java.util.List;
import java.util.Optional;

@RestController
public class ProgressController {

    private Mapper<ProgressSummary, ProgressSummaryDto> progressMapper;
    private ProgressService progressService;
    private UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(ProgressServiceImpl.class);

    public ProgressController(Mapper<ProgressSummary, ProgressSummaryDto> progressMapper, ProgressService progressService, UserService userService) {
        this.progressMapper = progressMapper;
        this.progressService = progressService;
        this.userService = userService;
    }

    @PostMapping("/progress/{eventId}/{locationId}")
    public ResponseEntity<ProgressSummaryDto> createProgress(@PathVariable("eventId") Long eventId,
                                                             @PathVariable("locationId") Long locationId){

        ProgressSummary summary = progressService.saveProgress(eventId, locationId);
        logger.info("Progress Summary (controller): score={}, eventName={}, count={}", summary.getScore(), summary.getEventName(), summary.getCount());

        if (summary == null) {
            return ResponseEntity.badRequest().build();
        }

        if (summary.getScore() == null){
            return new ResponseEntity<>(progressMapper.mapTo(summary), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(progressMapper.mapTo(summary), HttpStatus.CREATED);
        }
    }

    @GetMapping("/progress/{eventId}/top")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard(@PathVariable Long eventId) {
        List<LeaderboardEntryDto> leaderboard = progressService.getLeaderboardForEventsTop10(eventId);
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/progress/{eventId}/leaderboard/me")
    public ResponseEntity<LeaderboardEntryDto> getMyLeaderboardPosition(@PathVariable Long eventId, Authentication authentication) {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        UserEntity user = userService.getUserByEmail(oidcUser.getEmail()).orElseThrow(() ->
                new UsernameNotFoundException("User Not Found"));

        LeaderboardEntryDto userEntry = progressService.getUserLeaderboardPosition(user, eventId);

        return ResponseEntity.ok(userEntry);
    }


}
