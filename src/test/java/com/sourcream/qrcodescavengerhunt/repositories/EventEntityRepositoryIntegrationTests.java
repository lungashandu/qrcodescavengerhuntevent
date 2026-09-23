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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EventEntityRepositoryIntegrationTests {

    private EventRepository underTest;
    private UserRepository userRepository;

    @Autowired
    public EventEntityRepositoryIntegrationTests(EventRepository underTest, UserRepository userRepository){
        this.underTest = underTest;
        this.userRepository = userRepository;
    }

    @Test
    public void testThatEventCanBeCreatedAndRecalled() {
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);
        EventEntity event = TestDataUtil.createTestEventA(user);
        underTest.save(event);

        Optional<EventEntity> result = underTest.findById(event.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(event);
    }

    @Test
    public void testThatMultipleEventsCanBeCreatedAndRecalled() {
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);

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
        userRepository.save(user);

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
        userRepository.save(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        underTest.save(event);

        underTest.deleteById(event.getId());

        Optional<EventEntity> result = underTest.findById(event.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void testThatActiveEventsCanBeRetrieved() {
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        EventEntity activeEvent = TestDataUtil.createTestEventA(user);
        activeEvent.setStartTime(now.minusHours(1).toString());
        activeEvent.setEndTime(now.plusHours(1).toString());
        underTest.save(activeEvent);

        EventEntity futureEvent = TestDataUtil.createTestEventD(user);
        futureEvent.setStartTime(now.plusHours(1).toString());
        futureEvent.setEndTime(now.plusHours(2).toString());
        underTest.save(futureEvent);

        String currentTime = LocalDateTime.now().toString();
        List<EventEntity> result = underTest.findByStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                currentTime, currentTime
        );
        assertThat(result)
                .hasSize(1)
                .containsExactly(activeEvent);

    }
}
