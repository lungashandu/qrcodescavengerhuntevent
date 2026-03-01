package com.sourcream.qrcodescavengerhunt.domain.dto;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDto {

    private Long id;

    @NotNull(message = "Each location has to be associated with an event")
    private EventEntity eventEntity;

    private String qrCodeUrl;

    @NotNull(message = "Must give hint of the next location")
    private String hint;

    private String challenge;
}
