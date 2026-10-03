package com.sourcream.qrcodescavengerhunt.repositories;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    List<EventEntity> findByVisibilityAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(EventVisibility visibility, String startTime, String endTime);

    List<EventEntity> findByUserEntityId(Long userId);
}
