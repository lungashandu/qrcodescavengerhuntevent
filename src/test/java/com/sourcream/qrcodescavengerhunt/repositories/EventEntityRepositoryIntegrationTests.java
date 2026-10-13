package com.sourcream.qrcodescavengerhunt.repositories;

import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventVisibility;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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

    private final EventRepository underTest;
    private final UserRepository userRepository;

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

        EventEntity privateEvent = TestDataUtil.createTestEventB(user);
        privateEvent.setStartTime(now.minusHours(1).toString());
        privateEvent.setEndTime(now.plusHours(1).toString());
        privateEvent.setVisibility(EventVisibility.PRIVATE);
        underTest.save(privateEvent);

        EventEntity futureEvent = TestDataUtil.createTestEventD(user);
        futureEvent.setStartTime(now.plusHours(1).toString());
        futureEvent.setEndTime(now.plusHours(2).toString());
        underTest.save(futureEvent);

        Pageable pageable = PageRequest.of(0, 10);

        String currentTime = LocalDateTime.now().toString();
        Slice<EventEntity> result = underTest.findByVisibilityAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual( EventVisibility.PUBLIC,
                currentTime, currentTime, pageable
        );

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(activeEvent);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);

    }

    @Test
    public void testThatEventsCanBeRetrievedByUserId() {
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);

        UserEntity user2 = TestDataUtil.createTestUserB();
        userRepository.save(user2);

        EventEntity eventByUser1 = TestDataUtil.createTestEventA(user);
        underTest.save(eventByUser1);

        EventEntity eventByUser2 = TestDataUtil.createTestEventB(user2);
        underTest.save(eventByUser2);

        List<EventEntity> result = underTest.findByUserEntityId(user.getId());

        assertThat(result)
                .hasSize(1)
                .containsExactly(eventByUser1);
    }

    @Test
    void testThatPastPublicEventsCanBeRetrievedInSlices() {
        UserEntity user = userRepository.save(TestDataUtil.createTestUserA());

        EventEntity pastEventOne = TestDataUtil.createTestEventA(user);
        pastEventOne.setStartTime(LocalDateTime.now().minusHours(3).toString());
        pastEventOne.setEndTime(LocalDateTime.now().minusHours(1).toString());
        underTest.save(pastEventOne);

        EventEntity pastEventTwo = TestDataUtil.createTestEventB(user);
        pastEventTwo.setStartTime(LocalDateTime.now().minusHours(6).toString());
        pastEventTwo.setEndTime(LocalDateTime.now().minusHours(3).toString());
        underTest.save(pastEventTwo);

        EventEntity pastEventThree = TestDataUtil.createTestEventC(user);
        pastEventThree.setStartTime(LocalDateTime.now().minusHours(12).toString());
        pastEventThree.setEndTime(LocalDateTime.now().minusHours(10).toString());
        underTest.save(pastEventThree);

        EventEntity activeEvent = TestDataUtil.createTestEventD(user);
        activeEvent.setStartTime(LocalDateTime.now().minusHours(3).toString());
        activeEvent.setEndTime(LocalDateTime.now().plusHours(3).toString());
        underTest.save(activeEvent);

        Pageable pageable = PageRequest.of(0, 10);

        Slice<EventEntity> result = underTest.findByVisibilityAndEndTimeLessThanEqual(EventVisibility.PUBLIC, LocalDateTime.now().toString(), pageable);

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0)).isEqualTo(pastEventOne);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.getNumber()).isZero();
        assertThat(result.getSize()).isEqualTo(10);


    }
}
