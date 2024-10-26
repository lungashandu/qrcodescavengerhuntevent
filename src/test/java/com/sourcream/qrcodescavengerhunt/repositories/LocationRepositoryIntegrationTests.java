package com.sourcream.qrcodescavengerhunt.repositories;

import com.sourcream.qrcodescavengerhunt.TestDataUtil;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
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
public class LocationRepositoryIntegrationTests {
    private LocationRepository underTest;

    @Autowired
    public LocationRepositoryIntegrationTests(LocationRepository underTest) {
        this.underTest = underTest;
    }

    @Test
    public void testThatLocationCanBeCreatedAndRecalled() {
        EventEntity event = TestDataUtil.createTestEventA(null);
        LocationEntity location = TestDataUtil.createTestLocationA(event);
        underTest.save(location);

        Optional<LocationEntity> result = underTest.findById(location.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(location);
    }

    @Test
    public void testThatLocationCanBeRetrievedByEvent() {
        EventEntity event = TestDataUtil.createTestEventA(null);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        underTest.save(locationA);

        LocationEntity locationB = TestDataUtil.createTestLocationB(event);
        underTest.save(locationB);

        LocationEntity locationC = TestDataUtil.createTestLocationC(event);
        underTest.save(locationC);

        Iterable<LocationEntity> result = underTest.findByEventEntity(event);
        assertThat(result).hasSize(3).containsExactly(locationA, locationB, locationC);
    }

    @Test
    public void testThatLocationCanBeUpdated() {
        EventEntity event = TestDataUtil.createTestEventA(null);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        underTest.save(locationA);

        locationA.setName("UPDATED");
        underTest.save(locationA);

        Optional<LocationEntity> result = underTest.findById(locationA.getId());
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(locationA);
    }

    @Test
    public void testThatEventCanBeDeleted() {
        EventEntity event = TestDataUtil.createTestEventA(null);

        LocationEntity locationA = TestDataUtil.createTestLocationA(event);
        underTest.save(locationA);

        underTest.deleteById(locationA.getId());

        Optional<LocationEntity> result = underTest.findById(locationA.getId());
        assertThat(result).isEmpty();
    }
}
