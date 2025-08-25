package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.dto.LeaderboardDataDto;
import com.sourcream.qrcodescavengerhunt.domain.dto.LeaderboardEntryDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;

import java.util.List;

public interface ProgressService {

    ProgressSummary saveProgress(Long eventID, Long locationID);

    Boolean scanLocation(Long userId, Long locationId);

    ProgressSummary getProgressSummary(UserEntity user, EventEntity event);

    Integer getNumberOfScannedQRCodes(UserEntity user, EventEntity event);

    Integer calculateScoreForScan(UserEntity user, EventEntity event);

    List<LeaderboardEntryDto> getLeaderboardForEventsTop10(Long eventId);

    List<LeaderboardDataDto> getFullLeaderboardData(Long eventId);

    LeaderboardDataDto getIndividualUserStats(UserEntity user, Long eventId);

    LeaderboardEntryDto getUserLeaderboardPosition(UserEntity user, Long eventId);
}
