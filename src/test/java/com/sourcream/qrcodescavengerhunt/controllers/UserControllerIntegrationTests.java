package com.sourcream.qrcodescavengerhunt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.security.config.TestSecurityConfig;
import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public class UserControllerIntegrationTests {
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UserService userService;

    @Autowired
    public UserControllerIntegrationTests(MockMvc mockMvc, UserService userService){
        this.mockMvc = mockMvc;
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Test
    public void testThatCreateUserSuccessfullyReturnsHttp201Created() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user.setId(null);
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.status().isCreated()
        );
    }

    @Test
    public void testThatCreateUserSuccefullyReturnsSavedUser() throws Exception {
        UserEntity user = TestDataUtil.createTestUserA();
        user.setId(null);
        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.sub").value("113456789012345678901")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.fullname").value("John Doe")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.email").value("john.doe@example.com")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.role").value("USER")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.createAt").value("23-01-15T10:30:00")
        );
    }

    @Test
    public void testThatGetUserReturnsHttpCode200WhenUserExists() throws Exception {
        UserEntity userEntity = TestDataUtil.createTestUserA();
        userService.saveUser(userEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatGetUserReturnsUserWhenUserExists() throws Exception {
        UserEntity userEntity = TestDataUtil.createTestUserA();
        userService.saveUser(userEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.sub").value("113456789012345678901")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.fullname").value("John Doe")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.email").value("john.doe@example.com")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.role").value("USER")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.createAt").value("23-01-15T10:30:00")
        );
    }

    @Test
    public void testThatGetUserReturnsHttpCode404WhenNoUserExists() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/users/john.doe@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNotFound()
        );
    }

    @Test
    public void testThatRoleUpdateReturnsHttpStatus200() throws Exception {
        UserEntity userEntity = TestDataUtil.createTestUserA();
        UserEntity savedUserEntity = userService.saveUser(userEntity);

        String roleUpdateJson = objectMapper.writeValueAsString(Role.ADMIN);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/users/" + savedUserEntity.getId() + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.status().isOk()
        );
    }

    @Test
    public void testThatRoleUpdateReturnsUpdatedUser() throws Exception {
        UserEntity userEntity = TestDataUtil.createTestUserA();
        UserEntity savedUserEntity = userService.saveUser(userEntity);

        String roleUpdateJson = objectMapper.writeValueAsString(Role.ADMIN);

        mockMvc.perform(
                MockMvcRequestBuilders.patch("/users/" + savedUserEntity.getId() + "/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(roleUpdateJson)
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.id").isNumber()
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.sub").value("113456789012345678901")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.fullname").value("John Doe")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.email").value("john.doe@example.com")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.role").value("ADMIN")
        ).andExpect(
                MockMvcResultMatchers.jsonPath("$.createAt").value("23-01-15T10:30:00")
        );
    }

    @Test
    public void testThatDeleteUserReturnsHttpStatus204ForNonExistingUser() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }

    @Test
    public void testThatDeleteUserReturnsHttpStatus204ForExistingUser() throws Exception {
        UserEntity userEntity = TestDataUtil.createTestUserA();
        UserEntity savedUserEntity = userService.saveUser(userEntity);

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/users/" + savedUserEntity.getId())
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(
                MockMvcResultMatchers.status().isNoContent()
        );
    }
}
