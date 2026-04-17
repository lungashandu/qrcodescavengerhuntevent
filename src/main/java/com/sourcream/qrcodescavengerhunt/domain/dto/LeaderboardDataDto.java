package com.sourcream.qrcodescavengerhunt.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardDataDto {
    private String fullname;
    private String email;
    private Long score;
    private Long scannedLocations;
}
