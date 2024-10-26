package com.sourcream.qrcodescavengerhunt;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;

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
                .startTime("2024-07-01T10:00:00")
                .endTime("2024-07-01T16:00:00")
                .userEntity(user)
                .build();
    }

    public static EventEntity createTestEventB(final UserEntity user) {
        return EventEntity.builder()
                .id(2L)
                .eventName("Museum Mystery Hunt")
                .description("Solve riddles and clues to uncover secrets of the museum exhibits.")
                .startTime("2024-08-10T12:00:00")
                .endTime("2024-08-10T15:00:00")
                .userEntity(user)
                .build();
    }

    public static EventEntity createTestEventC(final UserEntity user) {
        return EventEntity.builder()
                .id(3L)
                .eventName("Autumn Adventure Quest")
                .description("Explore the beautiful autumn scenery while completing challenges across multiple locations.")
                .startTime("2024-09-15T09:00:00")
                .endTime("2024-09-15T17:00:00")
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
}
