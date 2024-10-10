package com.sourcream.qrcodescavengerhunt.repositories;

import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
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
public class EventEntityRepositoryIntegrationTests {

    private EventRepository underTest;

    @Autowired
    public EventEntityRepositoryIntegrationTests(EventRepository underTest){
        this.underTest = underTest;
    }

    @Test
    public void testThatEventCanBeCreatedAndRecalled() {
        UserEntity user = TestDataUtil.createTestUserA();
        EventEntity event = TestDataUtil.createTestEventA(user);
        underTest.save(event);

        Optional<EventEntity> result = underTest.findById(event.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(event);
    }

    @Test
    public void testThatMultipleEventsCanBeCreatedAndRecalled() {
        UserEntity user = TestDataUtil.createTestUserA();

        EventEntity eventA = TestDataUtil.createTestEventA(user);
        underTest.save(eventA);

        EventEntity eventB = TestDataUtil.createTestEventB(user);
        underTest.save(eventB);

        EventEntity eventC = TestDataUtil.createTestEventC(user);
        underTest.save(eventC);

        Iterable<EventEntity> result = underTest.findAll();
        assertThat(result)
                .hasSize(3)
                .containsExactly(eventA, eventB, eventC);
    }

    @Test
    public void testThatEventCanBeUpdated() {
        UserEntity user = TestDataUtil.createTestUserA();

        EventEntity event = TestDataUtil.createTestEventA(user);
        underTest.save(event);

        event.setEventName("UPDATED");
        underTest.save(event);

        Optional<EventEntity> result = underTest.findById(event.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(event);
    }

    @Test
    public void testThatEventCanBeDeleted() {
        UserEntity user = TestDataUtil.createTestUserA();

        EventEntity event = TestDataUtil.createTestEventA(user);
        underTest.save(event);

        underTest.deleteById(event.getId());

        Optional<EventEntity> result = underTest.findById(event.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testThatEventCanBeRetrievedByUser() {
        UserEntity user = TestDataUtil.createTestUserA();

        EventEntity event = TestDataUtil.createTestEventA(user);
        underTest.save(event);

        Iterable<EventEntity> result = underTest.findByUserEntity(user);
        assertThat(result)
                .hasSize(1)
                .containsExactly(event);

    }
}
