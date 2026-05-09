package com.sourcream.qrcodescavengerhunt.repositories;

import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.domain.entities.*;
import com.sourcream.qrcodescavengerhunt.domain.projection.ScannedLocationCard;
import com.sourcream.qrcodescavengerhunt.domain.projection.UserLeaderboardProjection;
import com.sourcream.qrcodescavengerhunt.security.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
@AutoConfigureTestDatabase
public class ProgressRepositoryIntegrationTests {

    private final ProgressRepository underTest;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProgressRepositoryIntegrationTests(ProgressRepository underTest, EventRepository eventRepository, LocationRepository locationRepository, UserRepository userRepository) {
        this.underTest = underTest;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    @Test
    public void testThatProgressCanBeCreatedAndRecalled(){
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);
        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);
        LocationEntity location = TestDataUtil.createTestLocationA(event);
        locationRepository.save(location);
        ProgressEntity progress = TestDataUtil.createTestProgressA(user, event, location);
        underTest.save(progress);

        Optional<ProgressEntity> result = underTest.findById(progress.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(progress);
    }

    @Test
    public void testThatMultipleProgressEntrieCanBeRetrieved() {
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);
        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);
        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationRepository.save(locationA);
        ProgressEntity progressA = TestDataUtil.createTestProgressA(user, event, locationA);
        underTest.save(progressA);

        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationRepository.save(locationB);
        ProgressEntity progressB = TestDataUtil.createTestProgressB(user, event, locationB);
        underTest.save(progressB);

        LocationEntity locationC = TestDataUtil.createTestLocationC(event);
        locationRepository.save(locationC);
        ProgressEntity progressC = TestDataUtil.createTestProgressC(user, event, locationC);
        underTest.save(progressC);

        Iterable<ProgressEntity> result = underTest.findByUserEntityAndEventEntity(user, event);
        assertThat(result).hasSize(3)
                .containsExactly(progressA, progressB, progressC);
    }


    @Test
    public void testThatSummaryOfProgressCanBeRetrievedWhenMultipleProgressEntitiesExist() {
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);
        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationRepository.save(locationA);
        ProgressEntity progressA = TestDataUtil.createTestProgressA(user, event, locationA);
        underTest.save(progressA);

        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationRepository.save(locationB);
        ProgressEntity progressB = TestDataUtil.createTestProgressB(user, event, locationB);
        underTest.save(progressB);

        LocationEntity locationC = TestDataUtil.createTestLocationC(event);
        locationRepository.save(locationC);
        ProgressEntity progressC = TestDataUtil.createTestProgressC(user, event, locationC);
        underTest.save(progressC);

        ProgressSummary summary = TestDataUtil.createTestProgressSummary();

        ProgressSummary result = underTest.getProgressSummary(user, event);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(summary);
    }

    @Test
    public void testThatSummaryOfProgressCanBeRetrievedWhenOneProgressEntitiesExist() {
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);
        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationRepository.save(locationA);
        ProgressEntity progressA = TestDataUtil.createTestProgressA(user, event, locationA);
        underTest.save(progressA);

        ProgressSummary summary = TestDataUtil.createTestProgressSummaryForOneRecord();

        ProgressSummary result = underTest.getProgressSummary(user, event);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(summary);
    }

    @Test
    public void findTop10LeaderboardByEvent() {
        UserEntity userA = TestDataUtil.createTestUserA();
        userRepository.save(userA);
        UserEntity userB = TestDataUtil.createTestUserB();
        userRepository.save(userB);

        EventEntity event = TestDataUtil.createTestEventA(userA);
        eventRepository.save(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationRepository.save(locationA);
        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationRepository.save(locationB);
        LocationEntity locationC = TestDataUtil.createTestLocationC(event);
        locationRepository.save(locationC);

        //User A progress
        ProgressEntity progressAForUserA = TestDataUtil.createTestProgressA(userA, event, locationA);
        underTest.save(progressAForUserA);
        ProgressEntity progressBForUserA = TestDataUtil.createTestProgressB(userA, event, locationB);
        underTest.save(progressBForUserA);
        ProgressEntity progressCForUserA = TestDataUtil.createTestProgressC(userA, event, locationC);
        underTest.save(progressCForUserA);

        //User B progress
        ProgressEntity progressAForUserB = TestDataUtil.createTestProgressA(userB, event, locationA);
        underTest.save(progressAForUserB);
        ProgressEntity progressBForUserB = TestDataUtil.createTestProgressB(userB, event, locationB);
        underTest.save(progressBForUserB);

        List<UserLeaderboardProjection> leaderboard = underTest.findTop10LeaderboardDataByEvent(event);

        UserLeaderboardProjection expectedUserA = TestDataUtil.createLeaderboardProjectionA();
        UserLeaderboardProjection expectedUserB = TestDataUtil.createLeaderboardProjectionB();

        assertThat(leaderboard)
                .isNotNull()
                .hasSize(2)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(expectedUserA, expectedUserB);
    }

    @Test
    public void findAllLeaderboardDataByEvent() {
        UserEntity userA = TestDataUtil.createTestUserA();
        userRepository.save(userA);
        UserEntity userB = TestDataUtil.createTestUserB();
        userRepository.save(userB);
        UserEntity userC = TestDataUtil.createTestUserC();

        EventEntity event = TestDataUtil.createTestEventA(userA);
        eventRepository.save(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationRepository.save(locationA);
        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationRepository.save(locationB);
        LocationEntity locationC = TestDataUtil.createTestLocationC(event);
        locationRepository.save(locationC);

        //User A progress
        ProgressEntity progressAForUserA = TestDataUtil.createTestProgressA(userA, event, locationA);
        underTest.save(progressAForUserA);
        ProgressEntity progressBForUserA = TestDataUtil.createTestProgressB(userA, event, locationB);
        underTest.save(progressBForUserA);
        ProgressEntity progressCForUserA = TestDataUtil.createTestProgressC(userA, event, locationC);
        underTest.save(progressCForUserA);

        //User B progress
        ProgressEntity progressAForUserB = TestDataUtil.createTestProgressA(userB, event, locationA);
        underTest.save(progressAForUserB);
        ProgressEntity progressBForUserB = TestDataUtil.createTestProgressB(userB, event, locationB);
        underTest.save(progressBForUserB);

        //User C progress
        ProgressEntity progressAForUserC = TestDataUtil.createTestProgressA(userC, event, locationA);
        underTest.save(progressAForUserC);

        UserLeaderboardProjection expectedUserA = TestDataUtil.createLeaderboardProjectionA();
        UserLeaderboardProjection expectedUserB = TestDataUtil.createLeaderboardProjectionB();
        UserLeaderboardProjection expectedUserC = TestDataUtil.createLeaderboardProjectionC();

        List<UserLeaderboardProjection> results = underTest.findAllLeaderboardDataByEvent(event);

        assertThat(results)
                .hasSize(3)
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactly(expectedUserA, expectedUserB, expectedUserC);
    }

    @Test
    public void findUserLeaderboardData_shouldReturnSpecificUserState() {
        UserEntity userA = TestDataUtil.createTestUserA();
        userRepository.save(userA);
        UserEntity userB = TestDataUtil.createTestUserB();
        userRepository.save(userB);
        UserEntity userC = TestDataUtil.createTestUserC();

        EventEntity event = TestDataUtil.createTestEventA(userA);
        eventRepository.save(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationRepository.save(locationA);
        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationRepository.save(locationB);
        LocationEntity locationC = TestDataUtil.createTestLocationC(event);
        locationRepository.save(locationC);

        //User A progress
        ProgressEntity progressAForUserA = TestDataUtil.createTestProgressA(userA, event, locationA);
        underTest.save(progressAForUserA);
        ProgressEntity progressBForUserA = TestDataUtil.createTestProgressB(userA, event, locationB);
        underTest.save(progressBForUserA);
        ProgressEntity progressCForUserA = TestDataUtil.createTestProgressC(userA, event, locationC);
        underTest.save(progressCForUserA);

        //User B progress
        ProgressEntity progressAForUserB = TestDataUtil.createTestProgressA(userB, event, locationA);
        underTest.save(progressAForUserB);
        ProgressEntity progressBForUserB = TestDataUtil.createTestProgressB(userB, event, locationB);
        underTest.save(progressBForUserB);

        //User C progress
        ProgressEntity progressAForUserC = TestDataUtil.createTestProgressA(userC, event, locationA);
        underTest.save(progressAForUserC);

        UserLeaderboardProjection expectedUser = TestDataUtil.createLeaderboardProjectionB();

        Optional<UserLeaderboardProjection> result = underTest.findUserLeaderboardData(userB.getEmail(), event.getId());

        assertThat(result)
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expectedUser);
    }

    @Test
    public void findAllLeaderboardDataByEvent_whenNoParticipants_shouldReturnEmptyList() {
        UserEntity userA = TestDataUtil.createTestUserA();
        userRepository.save(userA);
        EventEntity event = TestDataUtil.createTestEventA(userA);
        eventRepository.save(event);

        List<UserLeaderboardProjection> results = underTest.findAllLeaderboardDataByEvent(event);

        assertThat(results).isEmpty();
    }

    @Test
    public void findUserLeaderboardData_whenUserHasNoProgress_shouldReturnEmpty() {
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);

        Optional<UserLeaderboardProjection> result = underTest.findUserLeaderboardData(user.getEmail(), event.getId());

        assertThat(result).isEmpty();
    }

    @Test
    public void findScanCardsByUserAndEvents_shhouldReturnCardsOrderedByScanTimeDesc() {
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationRepository.save(locationA);

        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationRepository.save(locationB);

        ProgressEntity progressA = TestDataUtil.createTestProgressA(user, event, locationA);
        underTest.save(progressA);

        ProgressEntity progressB = TestDataUtil.createTestProgressB(user, event, locationB);
        underTest.save(progressB);

        List<ScannedLocationCard> results = underTest.findScanCardsByUserAndEvent(user, event);

        assertThat(results).hasSize(2);

        assertThat(results.get(0).getName()).isEqualTo(locationB.getName());

        assertThat(results.get(0).getScore()).isEqualTo(progressB.getScore());

        assertThat(results.get(1).getName()).isEqualTo(locationA.getName());
    }

    @Test
    public void countByUserEntityAndEventEntity_shouldReturnCorrectCount() {
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationRepository.save(locationA);

        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationRepository.save(locationB);

        ProgressEntity progressA = TestDataUtil.createTestProgressA(user, event, locationA);
        underTest.save(progressA);

        ProgressEntity progressB = TestDataUtil.createTestProgressB(user, event, locationB);
        underTest.save(progressB);

        long count = underTest.countByUserEntityAndEventEntity(user, event);

        assertThat(count).isEqualTo(2);
    }

    @Test
    public void findFirstByUserEntityAndEventEntityOrderByScanTimeDesc_ShouldReturnCorrectProgress() {
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);

        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationRepository.save(locationA);

        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        locationRepository.save(locationB);

        LocationEntity locationC = TestDataUtil.createTestLocationC(event);
        locationRepository.save(locationC);

        ProgressEntity progressA = TestDataUtil.createTestProgressA(user, event, locationA);
        underTest.save(progressA);

        ProgressEntity progressB = TestDataUtil.createTestProgressB(user, event, locationB);
        underTest.save(progressB);

        ProgressEntity progressC = TestDataUtil.createTestProgressC(user, event, locationC);
        underTest.save(progressC);

        Optional<ProgressEntity> result = underTest.findFirstByUserEntityAndEventEntityOrderByIdDesc(user,event);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(progressC);
    }
}
