package com.sourcream.qrcodescavengerhunt.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class EventValidationUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void validateEventIsActive(String startDateStr , String endDateStr) {
        LocalDateTime now = LocalDateTime.now();

        try {

            if (startDateStr != null && !startDateStr.isBlank()) {
                LocalDateTime startDate = LocalDateTime.parse(startDateStr, FORMATTER);

                if (now.isBefore(startDate)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This event has not started yet");
                }
            }

            if (endDateStr != null && !endDateStr.isBlank()) {
                LocalDateTime endDate = LocalDateTime.parse(endDateStr, FORMATTER);

                if (now.isAfter(endDate)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This event has already ended");
                }
            }

        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid event date format: " + e);
        }
    }
}
