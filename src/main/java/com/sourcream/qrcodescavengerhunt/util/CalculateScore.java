package com.sourcream.qrcodescavengerhunt.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class CalculateScore {

    public int totalScore(String eventStartDate, int noOfLocationsScanned) {
        final int BASESCORE = 100;
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE);
        Long daysPassed = ChronoUnit.DAYS.between(
                LocalDate.parse(eventStartDate,DateTimeFormatter.ISO_DATE),
                LocalDate.parse(timeNow));
        return (BASESCORE + scorePerLocationScanned(noOfLocationsScanned)) + Math.max(BASESCORE - (int)(daysPassed * 5), 0);
    }

    private int scorePerLocationScanned(int locationsScanned) {
        return 5 * locationsScanned;
    }
}
