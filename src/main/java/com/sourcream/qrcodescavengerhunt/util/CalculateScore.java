package com.sourcream.qrcodescavengerhunt.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class CalculateScore {

    public int totalScore(String eventStartDate, int noOfLocationsScanned) {
        final int BASESCORE = 100;
        final int MAX_BONUS = 50;
        final int PENALTY_RATE = 2;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime eventStart = LocalDateTime.parse(eventStartDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        long daysPassed = ChronoUnit.DAYS.between(
                eventStart.toLocalDate(),
                now.toLocalDate());

        long timePassed = ChronoUnit.HOURS.between(
                eventStart,
                now);

        int timeBonus = Math.max(0, MAX_BONUS - (int) (timePassed * PENALTY_RATE));

        return (BASESCORE + timeBonus + scorePerLocationScanned(noOfLocationsScanned)) + Math.max(BASESCORE - (int)(daysPassed * 5), 0);
    }

    private int scorePerLocationScanned(int locationsScanned) {
        return 5 * locationsScanned;
    }
}
