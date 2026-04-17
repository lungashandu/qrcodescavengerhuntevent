package com.sourcream.qrcodescavengerhunt.repositories;

import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserEntityRepositoryIntegrationTests {

    private UserRepository underTest;

    @Autowired
    public UserEntityRepositoryIntegrationTests(UserRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatUserCanBeCreatedAndRecalled() {
        UserEntity user = TestDataUtil.createTestUserA();
        underTest.save(user);
        Optional<UserEntity> result = underTest.findByEmail(user.getEmail());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);
    }

    @Test
    public void testThatUserCanBeRetrievedUsingEmail() {
        UserEntity userA = TestDataUtil.createTestUserA();
        underTest.save(userA);

        UserEntity userB = TestDataUtil.createTestUserB();
        underTest.save(userB);

        UserEntity userC = TestDataUtil.createTestUserC();
        underTest.save(userC);

        Optional<UserEntity> result = underTest.findByEmail(userC.getEmail());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(userC);
    }

    @Test
    public void testThatUserRoleCanBeUpdated() {
        UserEntity user = TestDataUtil.createTestUserA();
        underTest.save(user);

        user.setRole(Role.ADMIN);
        underTest.save(user);

        Optional<UserEntity> result = underTest.findByEmail(user.getEmail());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(user);

        Iterable<UserEntity> results = underTest.findAll();
        assertThat(results).hasSize(1)
                .containsExactly(user);
    }

    @Test
    public void testThatUserCanBeDeleted() {
        UserEntity user = TestDataUtil.createTestUserA();
        underTest.save(user);
        underTest.deleteById(user.getId());

        Optional<UserEntity> result = underTest.findByEmail(user.getEmail());
        assertThat(result).isEmpty();
    }
}
