package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;

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
