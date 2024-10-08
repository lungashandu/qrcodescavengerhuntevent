package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.repositories.UserRepository;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity saveUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserEntity updateUserRole(Long id, Role role) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userEntity.setRole(role);
        return userRepository.save(userEntity);
    }

    @Override
    public boolean isExists(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
