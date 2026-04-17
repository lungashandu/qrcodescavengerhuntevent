package com.sourcream.qrcodescavengerhunt.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgressSummaryDto {

    private Integer score;
    private String eventName;
    private String locationName;
    private Long count;
}
