package com.sourcream.qrcodescavengerhunt.domain.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "locations")
public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_id_seq")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "eventId")
    private EventEntity eventEntity;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "qrCodeUrl", nullable = false)
    private String qrCodeUrl;

    @Column(name = "hint")
    private String hint;

    @Column(name = "challenge", nullable = false)
    private String challenge;
}
