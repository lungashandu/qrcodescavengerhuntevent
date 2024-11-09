package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;

public interface ProgressService {

    ProgressSummary saveProgress(Long eventID, Long locationID);

    ProgressSummary getProgressSummary(UserEntity user, EventEntity event);

    Integer getNumberOfScannedQRCodes(UserEntity user, EventEntity event);

    Integer calculateScoreForScan(UserEntity user, EventEntity event);

    Integer getLatestScore(UserEntity user, EventEntity event);
}
