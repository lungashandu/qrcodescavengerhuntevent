package com.sourcream.qrcodescavengerhunt.controllers;

import com.sourcream.qrcodescavengerhunt.domain.dto.UserDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.mappers.UserMapper;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import com.sourcream.qrcodescavengerhunt.util.AccessControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
public class UserController {

    private final UserService userService;

    private final UserMapper userMapper;
    private final AccessControlService accessControlService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, UserMapper userMapper, AccessControlService accessControlService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.accessControlService = accessControlService;
    }

    @GetMapping(path = "/users/{email}")
    public ResponseEntity<?> getUser(@PathVariable("email") String email) {
        if (email == null || email.isBlank()){
            logger.warn("Attempted getting user details with null or empty email");
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Email must not be null or empty",
                    "timestamp", Instant.now()
            ));
        }

        accessControlService.requireSameUserOrAdmin(email);
        Optional<UserEntity> foundUser = userService.getUserByEmail(email);
        if (foundUser.isPresent()){
            UserDto userDto = userMapper.mapTo(foundUser.get());
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "User with email " + email + " was not found",
                    "timestamp", Instant.now()));
        }
    }

    //@DeleteMapping(path = "/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        if (id == null || id <= 0){
            logger.warn("Attempted to delete user with null or 0 id");
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "User ID cannot be null or zero",
                    "timestamp", Instant.now()
            ));
        }

        if (!userService.isExists(id)) {
            logger.warn("Attempted to delete non-existent user with id {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "User with id " + id + " was not found",
                    "timestamp", Instant.now()
            ));
        }
        userService.deleteUser(id);
        logger.info("User with id {} deleted successfully", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    //@PatchMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable("id") Long id, @RequestBody Role role) {
        if (id == null || id <= 0) {
            logger.warn("Attempted to update user's role with null or zero id");
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "User ID must be a positive number",
                    "timestamp", Instant.now()
            ));
        }

        if (role == null) {
            logger.warn("Attempted to update user's role with no new role provided");
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Role must not be null",
                    "timestamp", Instant.now()
            ));
        }

        if(!userService.isExists(id)){
            logger.warn("Attempted to update role of user that doesn't exist");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "User with ID " + id + " not found",
                    "timestamp", Instant.now()
            ));
        }

        UserEntity userEntity = userService.updateUserRole(id, role);

        return new ResponseEntity<>(userMapper.mapTo(userEntity), HttpStatus.OK);
    }

}
