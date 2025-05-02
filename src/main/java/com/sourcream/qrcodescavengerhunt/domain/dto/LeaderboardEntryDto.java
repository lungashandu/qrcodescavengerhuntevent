package com.sourcream.qrcodescavengerhunt.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntryDto {
    private String fullname;
    private Long score;
    private Long scannedLocations;
}
