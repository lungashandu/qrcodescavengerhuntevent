package com.sourcream.qrcodescavengerhunt.domain.projection;

public class ScannedLocationCard {
    private Long locationId;
    private String name;
    private int score;
    private String scanTime;

    public ScannedLocationCard(Long locationId, String name, int score, String scanTime) {
        this.locationId = locationId;
        this.name = name;
        this.score = score;
        this.scanTime = scanTime;
    }

    public Long getLocationId() {
        return locationId;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public String getScanTime() {
        return scanTime;
    }
}
