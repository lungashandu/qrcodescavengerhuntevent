package com.sourcream.qrcodescavengerhunt;

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
}
