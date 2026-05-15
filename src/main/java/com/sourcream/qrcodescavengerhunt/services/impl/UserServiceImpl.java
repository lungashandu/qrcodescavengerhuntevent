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

    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity saveUser(UserEntity userEntity) {
        if (userEntity == null) {
            logger.warn("Attempted to save a null UserEntity");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User details must not be null");
        }

        if (userEntity.getEmail() == null || userEntity.getEmail().isBlank()) {
            logger.warn("Attempted to save a user with missing email");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is required");
        }

        if (userRepository.findByEmail(userEntity.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User with email " + userEntity.getEmail() + " already exists");
        }

        logger.info("Saving user with email: {}", userEntity.getEmail());
        return userRepository.save(userEntity);
    }

    @Override
    public Optional<UserEntity> getUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            logger.warn("Attempted to fetch user with null or blank email");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email must not be empty");
        }

        logger.info("Fetching user by email: {}", email);
        return userRepository.findByEmail(email);

    }

    @Override
    public UserEntity updateUserRole(Long id, Role role) {
        if (id == null || id <= 0){
            logger.warn("Attempted to update role with null user ID or user ID is less than 1");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must not be null or less than 1");
        }

        if (role == null) {
            logger.warn("Attempted to update roll with null role for userId = {}", id);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role must not be null");
        }

        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User with id = {} not found for role update", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
                });

        userEntity.setRole(role);
        UserEntity updatedUser = userRepository.save(userEntity);
        logger.info("Updated role for user id = {} to {}", id, role);

        return updatedUser;
    }

    @Override
    public boolean isExists(Long id) {
        if (id == null || id <= 0) {
            logger.warn("Attempted to check existence with null user ID");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User Id must not be null");
        }

        boolean exists = userRepository.existsById(id);
        logger.info("Checked existence for user id = {}, exists = {}", id, exists);

        return exists;
    }

    @Override
    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            logger.warn("Attempted to delete user with null ID");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID must not be null");
        }

        if (!userRepository.existsById(id)){
            logger.warn("Attempted to delete non-existing user with id={}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        userRepository.deleteById(id);
        logger.info("Successfully deleted user with id = {}", id);

    }
}
