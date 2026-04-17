package com.sourcream.qrcodescavengerhunt.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table (name = "scans", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "location_id"})})
public class ScanEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "scanned_at", nullable = false)
    private Instant scannedAt;
}
