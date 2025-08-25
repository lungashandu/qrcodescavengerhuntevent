package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.repositories.UserRepository;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity saveUser(UserEntity userEntity) {
        try {
            if (userEntity == null) {
                logger.warn("Attempted to save a null UserEntity");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User details must not be null");
            }

            if (userEntity.getEmail() == null || userEntity.getEmail().isBlank()) {
                logger.warn("Attempted to save a user with missing email");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
            }

            logger.info("Saving user with email: {}", userEntity.getEmail());
            return userRepository.save(userEntity);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while saving user", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to save user due to unexpected error");
        }

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
