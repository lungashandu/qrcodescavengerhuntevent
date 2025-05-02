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

    Optional<ProgressEntity> findFirstByUserEntityAndEventEntityOrderByScanTimeDesc(@Param("user")UserEntity user, @Param("event") EventEntity event);

    @Query("SELECT new com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary(SUM(p.score), e.eventName, COUNT(p)) " +
            "FROM ProgressEntity p JOIN p.eventEntity e " +
            "WHERE p.userEntity = :user AND p.eventEntity = :event")
    ProgressSummary getProgressSummary(@Param("user") UserEntity user, @Param("event") EventEntity event);

    @Query("SELECT u.fullname, u.email, SUM(p.score) as totalScore, COUNT(p) as locationsScanned " +
            "FROM ProgressEntity p " +
            "JOIN p.userEntity u " +
            "WHERE p.eventEntity = :event " +
            "GROUP BY u.fullname, u.email " +  // Group by both name and email
            "ORDER BY totalScore DESC " +
            "LIMIT 10")
    List<Object[]> findTop10LeaderboardDataByEvent(EventEntity event);

    @Query("SELECT u.fullname, u.email, SUM(p.score) as totalScore, COUNT(p) as locationsScanned " +
            "FROM ProgressEntity p " +
            "JOIN p.userEntity u " +
            "WHERE p.eventEntity = :event " +
            "GROUP BY u.fullname, u.email " +
            "ORDER BY totalScore DESC")
    List<Object[]> findAllLeaderboardDataByEvent(EventEntity event);

    @Query("SELECT u.fullname, u.email, SUM(p.score) as totalScore, COUNT(p) as locationsScanned " +
            "FROM ProgressEntity p " +
            "JOIN p.userEntity u " +
            "WHERE p.eventEntity.id = :eventId AND u.email = :email " +
            "GROUP BY u.fullname, u.email")
    Object[] findUserLeaderboardData(@Param("email") String email, @Param("eventId") Long eventId);
}
