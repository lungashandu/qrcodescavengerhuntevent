package com.sourcream.qrcodescavengerhunt.repositories;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.domain.projection.ScannedLocationCard;
import com.sourcream.qrcodescavengerhunt.domain.projection.UserLeaderboardProjection;
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


    @Query("""
    SELECT new com.sourcream.qrcodescavengerhunt.domain.projection.ScannedLocationCard(
        l.id,
        l.name,
        p.score,
        p.scanTime
    )
    FROM ProgressEntity p
    JOIN p.locationEntity l
    WHERE p.userEntity = :user
    AND p.eventEntity = :event
    ORDER BY p.scanTime DESC
""")
    List<ScannedLocationCard> findScanCardsByUserAndEvent(
            @Param("user") UserEntity user,
            @Param("event") EventEntity event
    );

    long countByUserEntityAndEventEntity(UserEntity user, EventEntity event);

    @Query("SELECT new com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary(SUM(p.score), e.eventName, COUNT(p)) " +
            "FROM ProgressEntity p JOIN p.eventEntity e " +
            "WHERE p.userEntity = :user AND p.eventEntity = :event")
    ProgressSummary getProgressSummary(@Param("user") UserEntity user, @Param("event") EventEntity event);

    @Query("SELECT u.fullname as fullname, u.email as email, " +
            "SUM(p.score) as totalScore, COUNT(p) as locationsScanned " +
            "FROM ProgressEntity p " +
            "JOIN p.userEntity u " +
            "WHERE p.eventEntity = :event " +
            "GROUP BY u.fullname, u.email " +
            "ORDER BY SUM(p.score) DESC " +
            "LIMIT 10")
    List<UserLeaderboardProjection> findTop10LeaderboardDataByEvent(EventEntity event);

    @Query("SELECT u.fullname as fullname, u.email as email, " +
            "SUM(p.score) as totalScore, COUNT(p) as locationsScanned " +
            "FROM ProgressEntity p " +
            "JOIN p.userEntity u " +
            "WHERE p.eventEntity = :event " +
            "GROUP BY u.fullname, u.email " +
            "ORDER BY SUM(p.score) DESC")
    List<UserLeaderboardProjection> findAllLeaderboardDataByEvent(EventEntity event);

    @Query("SELECT u.fullname as fullname, u.email as email, " +
            "SUM(p.score) as totalScore, COUNT(p) as locationsScanned " +
            "FROM ProgressEntity p " +
            "JOIN p.userEntity u " +
            "WHERE p.eventEntity.id = :eventId AND u.email = :email " +
            "GROUP BY u.fullname, u.email")
    Optional<UserLeaderboardProjection> findUserLeaderboardData(@Param("email") String email, @Param("eventId") Long eventId);
}
