package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;

import java.util.Optional;

public interface UserService {

    UserEntity saveUser(UserEntity user);

    Optional<UserEntity> getUserByEmail(String email);

    UserEntity updateUserRole(Long id, Role role);

    boolean isExists(Long id);

    void deleteUser(Long id);
}
