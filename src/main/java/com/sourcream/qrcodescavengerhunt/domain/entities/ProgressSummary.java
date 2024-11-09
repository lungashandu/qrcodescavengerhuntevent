package com.sourcream.qrcodescavengerhunt.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgressSummary {
    private Integer score;

    private String eventName;

    private Integer numberOfScannedQRCodes;
}
