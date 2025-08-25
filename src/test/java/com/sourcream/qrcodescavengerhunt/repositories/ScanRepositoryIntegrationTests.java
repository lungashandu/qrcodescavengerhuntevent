package com.sourcream.qrcodescavengerhunt.repositories;

import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.domain.entities.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ScanRepositoryIntegrationTests {
    private final ScanRepository underTest;
    private final ProgressRepository progressRepository;

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;

    @Autowired
    public ScanRepositoryIntegrationTests(ScanRepository underTest,
                                          ProgressRepository progressRepository,
                                          UserRepository userRepository,
                                          LocationRepository locationRepository,
                                          EventRepository eventRepository) {
        this.underTest = underTest;
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.locationRepository = locationRepository;
        this.eventRepository = eventRepository;
    }

    @Test
    public void testThatWhatUserHasAlreadyScannedTrueIsReturned(){
        UserEntity user = TestDataUtil.createTestUserA();
        userRepository.save(user);
        EventEntity event = TestDataUtil.createTestEventA(user);
        eventRepository.save(event);
        LocationEntity location = TestDataUtil.createTestLocationA(event);
        locationRepository.save(location);
        ProgressEntity progress = TestDataUtil.createTestProgressA(user, event, location);
        progressRepository.save(progress);
        ScanEntity scan = TestDataUtil.createTestScanA();
        underTest.save(scan);

        Boolean result = underTest.existsByUserIdAndLocationId(user.getId(), location.getId());

        assertThat(result).isTrue();
    }
}
