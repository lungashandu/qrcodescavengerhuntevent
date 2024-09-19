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
@Table(name = "progress")
public class UserProgressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "progress_id_seq")
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId")
    private UserEntity userEntity;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "eventId")
    private EventEntity eventEntity;

    @ManyToOne
    @JoinColumn(name = "locationId")
    private LocationEntity locationEntity;

    @Column(name = "scanTime")
    private String scanTime;

    @Column(name = "score")
    private Integer score;
}
