package com.sourcream.qrcodescavengerhunt.domain.dto;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProgressDto {

    private Long id;

    private UserEntity userEntity;

    private EventEntity eventEntity;

    private LocationEntity locationEntity;

    private String scanTime;

    private Integer score;
}
