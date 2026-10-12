package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface EventService {

    EventEntity saveEvent(EventEntity event);

    List<EventEntity> getAllEvents();

    List<EventEntity> getActiveEvents();

    Slice<EventEntity> getPastEvents(Pageable pageable);

    List<EventEntity> getMyEvents();

    Optional<EventEntity> getEventById(Long id);

    Boolean isExists(Long id);

    EventEntity partialEventUpdate(Long id, EventEntity event);

    void deleteEvent(Long id);
}
