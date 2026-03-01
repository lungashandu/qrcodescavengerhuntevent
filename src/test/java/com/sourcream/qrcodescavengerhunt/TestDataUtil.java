package com.sourcream.qrcodescavengerhunt;

import com.sourcream.qrcodescavengerhunt.domain.entities.*;
import com.sourcream.qrcodescavengerhunt.domain.projection.UserLeaderboardProjection;

import java.time.Instant;

public final class TestDataUtil {

    public static UserEntity createTestUserA() {
        return UserEntity.builder()
                .id(1L)
                .sub("113456789012345678901")
                .fullname("John Doe")
                .email("john.doe@example.com")
                .role(Role.USER)
                .createAt("23-01-15T10:30:00")
                .build();
    }

    public static UserEntity createTestUserB() {
        return UserEntity.builder()
                .id(2L)
                .sub("213456789012345678901")
                .fullname("Jane Smith")
                .email("jane.smith@example.com")
                .role(Role.ADMIN)
                .createAt("23-02-20T14:45:00")
                .build();
    }

    public static UserEntity createTestUserC() {
        return UserEntity.builder()
                .id(3L)
                .sub("313456789012345678901")
                .fullname("Alice Williams")
                .email("alice.williams@example.com")
                .role(Role.USER)
                .createAt("23-03-10T09:15:00")
                .build();
    }

    public static EventEntity createTestEventA(final UserEntity user) {
        return EventEntity.builder()
                .id(1l)
                .eventName("Summer Scavenger Hunt")
                .description("A fun-filled scavenger hunt around the city to explore hidden gems and landmarks.")
                .startTime("2026-07-01T10:23:11")
                .endTime("2026-07-01T16:00:00")
                .userEntity(user)
                .build();
    }

    public static EventEntity createTestEventB(final UserEntity user) {
        return EventEntity.builder()
                .id(2L)
                .eventName("Museum Mystery Hunt")
                .description("Solve riddles and clues to uncover secrets of the museum exhibits.")
                .startTime("2026-08-10T12:00:00")
                .endTime("2026-08-10T15:00:00")
                .userEntity(user)
                .build();
    }

    public static EventEntity createTestEventC(final UserEntity user) {
        return EventEntity.builder()
                .id(3L)
                .eventName("Autumn Adventure Quest")
                .description("Explore the beautiful autumn scenery while completing challenges across multiple locations.")
                .startTime("2026-09-15T09:00:00")
                .endTime("2026-09-15T17:00:00")
                .userEntity(user)
                .build();
    }

    public static LocationEntity createTestLocationA(final EventEntity event) {
        return LocationEntity.builder()
                .id(1L)
                .eventEntity(event)
                .name("City Center")
                .qrCodeUrl("https://firebase.com/qrcode/citycenter.png")
                .hint("Find the tallest building")
                .challenge("Take a picture in front of it")
                .build();
    }

    public static LocationEntity createTestLocationB(final EventEntity event) {
        return LocationEntity.builder()
                .id(2L)
                .eventEntity(event)
                .name("Old Town Park")
                .qrCodeUrl("https://firebase.com/qrcode/oldtownpark.png")
                .hint("Look for the big oak tree")
                .challenge("Find the hidden plaque beneath the tree")
                .build();
    }

    public static LocationEntity createTestLocationC(final EventEntity event) {
        return LocationEntity.builder()
                .id(3L)
                .eventEntity(event)
                .name("Museum Entrance")
                .qrCodeUrl("https://firebase.com/qrcode/museum.png")
                .hint("Find the entrance with the lion statues")
                .challenge("Snap a selfie with one of the lions")
                .build();
    }

    public static ProgressEntity createTestProgressA(final UserEntity user,
                                                     final EventEntity event,
                                                     final LocationEntity location){
        return ProgressEntity.builder()
                .id(1L)
                .userEntity(user)
                .eventEntity(event)
                .locationEntity(location)
                .scanTime("2024-09-20T10:45:00")
                .score(10)
                .build();
    }

    public static ProgressEntity createTestProgressB(final UserEntity user,
                                                     final EventEntity event,
                                                     final LocationEntity location){
        return ProgressEntity.builder()
                .id(2L)
                .userEntity(user)
                .eventEntity(event)
                .locationEntity(location)
                .scanTime("2024-09-20T12:30:00")
                .score(15)
                .build();
    }

    public static ProgressEntity createTestProgressC(final UserEntity user,
                                                     final EventEntity event,
                                                     final LocationEntity location){
        return ProgressEntity.builder()
                .id(3L)
                .userEntity(user)
                .eventEntity(event)
                .locationEntity(location)
                .scanTime("2024-09-20T14:20:00")
                .score(20)
                .build();
    }

    public static ProgressSummary createTestProgressSummary(){
        return ProgressSummary.builder()
                .score(45L)
                .eventName("Summer Scavenger Hunt")
                .count(3L)
                .build();
    }

    public static ProgressSummary createTestProgressSummaryForOneRecord(){
        return ProgressSummary.builder()
                .score(10L)
                .eventName("Summer Scavenger Hunt")
                .count(1L)
                .build();
    }

    public static ProgressSummary createTestProgressSummaryUrl(){
        return ProgressSummary.builder()
                .score(100L)
                .eventName("Summer Scavenger Hunt")
                .count(1L)
                .build();
    }

    public static ProgressSummary createTestProgressSummaryForAnonymousUser(){
        return ProgressSummary.builder()
                .score(null)
                .eventName("Summer Scavenger Hunt")
                .count(1L)
                .build();
    }

    public static UserLeaderboardProjection createLeaderboardProjectionA() {
        return new UserLeaderboardProjection() {
            @Override
            public String getFullname() {
                return "John Doe";
            }

            @Override
            public String getEmail() {
                return "john.doe@example.com";
            }

            @Override
            public Long getTotalScore() {
                return 45L;
            }

            @Override
            public Long getLocationsScanned() {
                return 3L;
            }
        };
    }

    public static UserLeaderboardProjection createLeaderboardProjectionB() {
        return new UserLeaderboardProjection() {
            @Override
            public String getFullname() {
                return "Jane Smith";
            }

            @Override
            public String getEmail() {
                return "jane.smith@example.com";
            }

            @Override
            public Long getTotalScore() {
                return 25L;
            }

            @Override
            public Long getLocationsScanned() {
                return 2L;
            }
        };
    }

    public static UserLeaderboardProjection createLeaderboardProjectionC() {
        return new UserLeaderboardProjection() {
            @Override
            public String getFullname() {
                return "Alice Williams";
            }

            @Override
            public String getEmail() {
                return "alice.williams@example.com";
            }

            @Override
            public Long getTotalScore() {
                return 10L;
            }

            @Override
            public Long getLocationsScanned() {
                return 1L;
            }
        };
    }

    public static ScanEntity createTestScanA() {
        return ScanEntity.builder()
                .id(1L)
                .userId(1L)
                .locationId(1L)
                .scannedAt(Instant.parse("2023-01-15T10:30:00Z"))
                .build();
    }
}
