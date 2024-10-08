package com.sourcream.qrcodescavengerhunt.controllers;

import com.sourcream.qrcodescavengerhunt.domain.dto.UserDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    private UserService userService;

    private Mapper<UserEntity, UserDto> userMapper;

    public UserController(UserService userService, Mapper<UserEntity, UserDto> userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping(path = "/users")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserEntity userEntity = userMapper.mapFrom(userDto);
        UserEntity savedUserEntity = userService.saveUser(userEntity);

        return new ResponseEntity<>(userMapper.mapTo(savedUserEntity), HttpStatus.CREATED);
    }

    @GetMapping(path = "/users/{email}")
    public ResponseEntity<UserDto> getUser(@PathVariable("email") String email) {
        Optional<UserEntity> foundUser = userService.getUserByEmail(email);

        return foundUser.map(userEntity -> {
            UserDto userDto = userMapper.mapTo(userEntity);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        }).orElse(
                new ResponseEntity<>(HttpStatus.NOT_FOUND)
        );
    }

    @DeleteMapping(path = "/users/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable("id") Long id, @RequestBody Role role) {
        if(!userService.isExists(id)){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        UserEntity userEntity = userService.updateUserRole(id, role);
        return new ResponseEntity<>(userMapper.mapTo(userEntity), HttpStatus.OK);
    }

}
