package com.sourcream.qrcodescavengerhunt.domain.dto;

import com.sourcream.qrcodescavengerhunt.domain.projection.ScannedLocationCard;

import java.util.List;

public class EventProgressOverview {
    private Long eventId;
    private String eventName;
    private String eventDescription;

    private long scannedCount;
    private long totalLocations;
    private long remaining;

    private List<ScannedLocationCard> scannedLocations;

    public EventProgressOverview(Long eventId, String eventName, String eventDescription, long scannedCount, long totalLocations, long remaining, List<ScannedLocationCard> scannedLocations) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.scannedCount = scannedCount;
        this.totalLocations = totalLocations;
        this.remaining = remaining;
        this.scannedLocations = scannedLocations;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {return eventDescription;}

    public long getScannedCount() {
        return scannedCount;
    }

    public long getTotalLocations() {
        return totalLocations;
    }

    public long getRemaining() {
        return remaining;
    }

    public List<ScannedLocationCard> getScannedLocations() {
        return scannedLocations;
    }
}
