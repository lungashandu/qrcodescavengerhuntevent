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
    private String nextLocationHint;
    private List<ScannedLocationCard> scannedLocations;

    public EventProgressOverview(Long eventId, String eventName, String eventDescription, long scannedCount, long totalLocations, long remaining, List<ScannedLocationCard> scannedLocations, String nextLocationHint) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.scannedCount = scannedCount;
        this.totalLocations = totalLocations;
        this.remaining = remaining;
        this.scannedLocations = scannedLocations;
        this.nextLocationHint = nextLocationHint;
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

    public String getNextLocationHint() {
        return nextLocationHint;
    }
}
