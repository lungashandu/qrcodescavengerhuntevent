package com.sourcream.qrcodescavengerhunt.mappers;

import com.sourcream.qrcodescavengerhunt.domain.dto.ProgressSummaryDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary;
import org.springframework.stereotype.Component;

@Component
public class ProgressSummaryMapper {

    public ProgressSummaryDto mapTo(ProgressSummary progressSummary) {
        if (progressSummary == null) {
            return null;
        }

        return ProgressSummaryDto.builder()
                .score(progressSummary.getScore() != null ?
                        progressSummary.getScore().intValue() : null)
                .eventName(progressSummary.getEventName())
                .count(progressSummary.getCount())
                .build();
    }

}
