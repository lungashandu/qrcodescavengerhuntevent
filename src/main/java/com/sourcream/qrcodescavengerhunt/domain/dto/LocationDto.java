package com.sourcream.qrcodescavengerhunt.domain.dto;

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
    private Long eventId;

    @NotNull(message= "Event name cannot be null for any location")
    private String eventName;

    @NotNull(message = "Each location must have a name")
    private String name;

    private String qrCodeUrl;

    @NotNull(message = "Must give hint of the next location")
    private String hint;

    private String challenge;
}
