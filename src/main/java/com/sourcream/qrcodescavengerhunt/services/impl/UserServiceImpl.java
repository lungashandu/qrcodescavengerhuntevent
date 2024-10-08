package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.services.UserService;

import java.util.Optional;
import java.util.Set;

public class UserServiceImpl implements UserService {
    @Override
    public UserEntity registerUser(UserEntity user) {
        return null;
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public UserEntity updateUserRole(String email, Set<Role> role) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }
}
