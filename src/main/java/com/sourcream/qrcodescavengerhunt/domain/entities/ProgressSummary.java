package com.sourcream.qrcodescavengerhunt.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class ProgressSummary {
    private Long score;
    private String eventName;
    private String locationName;
    private Long count;

    public ProgressSummary(Long score, String eventName, String locationName, Long count) {
        this.score = score;
        this.eventName = eventName;
        this.locationName = locationName;
        this.count = count;
    }

    public ProgressSummary(Long score, String eventName, Long count) {
        this.score = score;
        this.eventName = eventName;
        this.count = count;
    }

}
