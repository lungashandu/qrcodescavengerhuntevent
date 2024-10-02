package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;

import java.util.Optional;
import java.util.Set;

public interface UserService {

    UserEntity registerUser(UserEntity user);

    Optional<UserEntity> getUserByEmail(String email);

    UserEntity updateUserRole(String email, Set<Role> role);

    void deleteUser(Long id);
}
