package com.sourcream.qrcodescavengerhunt.domain.dto;

import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventDto {

    private Long id;

    @NotBlank(message = "Event name is required")
    @NotNull
    @Size(max = 100, message = "Event name must not exceed 100 characters")
    private String eventName;

    @NotNull
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time must be in the present or future")
    private String startTime;

    @NotNull(message = "End time is required")
    @Future(message = "End time must be in the future")
    private String endTime;

    private UserEntity userEntity;
}
