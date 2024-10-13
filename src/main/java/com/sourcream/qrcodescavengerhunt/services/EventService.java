package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.List;
import java.util.Optional;

public interface EventService {

    EventEntity saveEvent(EventEntity event);

    List<EventEntity> getAllEvents();

    List<EventEntity> getEventsByUser(String email);

    Optional<EventEntity> getEventById(Long id);

    Boolean isExists(Long id);

    EventEntity partialEventUpdate(Long id, EventEntity event);

    void deleteEvent(Long id);
}
