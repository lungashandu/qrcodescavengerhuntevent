package com.sourcream.qrcodescavengerhunt.repositories;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgressRepository extends JpaRepository<ProgressEntity, Long> {
    List<ProgressEntity> findByUserEntityAndEventEntity(UserEntity user, EventEntity event);

    @Query("SELECT p FROM ProgressEntity p WHERE p.userEntity =:user AND p.eventEntity =:event ORDER BY p.scanTime DESC")
    Optional<ProgressEntity> findLatestScoreByUserEntityAndEventEntity(@Param("user")UserEntity user, @Param("event") EventEntity event);

    @Query("SELECT new com.example.ProgressSummaryDTO(p.score, e.eventName, COUNT(p)) " +
            "FROM ProgressEntity p JOIN p.eventEntity e " +
            "WHERE p.userEntity = :user AND p.eventEntity = :event " +
            "GROUP BY p.score, e.eventName")
    ProgressSummary getProgressSummary(@Param("user") UserEntity user, @Param("event") EventEntity event);

}
