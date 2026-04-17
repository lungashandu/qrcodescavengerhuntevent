package com.sourcream.qrcodescavengerhunt.domain.projection;

public interface UserLeaderboardProjection {
    String getFullname();
    String getEmail();
    Long getTotalScore();
    Long getLocationsScanned();
}
