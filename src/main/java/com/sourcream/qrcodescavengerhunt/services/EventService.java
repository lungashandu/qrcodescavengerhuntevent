package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface EventService {

    EventEntity saveEvent(EventEntity event, UserEntity user);

    List<EventEntity> getAllEvents();

    List<EventEntity> getEventsByUser(UserEntity user);

    Optional<EventEntity> getEventById(Long id);

    EventEntity updateEvent(Long id, EventEntity event);

    void deleteEvent(Long id);
}
