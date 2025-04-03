package com.sourcream.qrcodescavengerhunt.repositories;

import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.domain.entities.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProgressRepositoryIntegrationTests {

    private final ProgressRepository underTest;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;

    @Autowired
    public ProgressRepositoryIntegrationTests(ProgressRepository underTest, EventRepository eventRepository, LocationRepository locationRepository) {
        this.underTest = underTest;
        this.eventRepository = eventRepository;
        this.locationRepository = locationRepository;
    }

    @Test
    public void testThatProgressCanBeCreatedAndRecalled(){
        UserEntity user = TestDataUtil.createTestUserA();
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
        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);
        event.setUserEntity(null);
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
        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);
        event.setUserEntity(null);

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
        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);
        event.setUserEntity(null);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        locationRepository.save(locationA);
        ProgressEntity progressA = TestDataUtil.createTestProgressA(user, event, locationA);
        underTest.save(progressA);

        ProgressSummary summary = TestDataUtil.createTestProgressSummaryForOneRecord();

        ProgressSummary result = underTest.getProgressSummary(user, event);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(summary);
    }
}
