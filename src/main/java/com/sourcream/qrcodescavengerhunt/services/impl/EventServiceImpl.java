package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.repositories.EventRepository;
import com.sourcream.qrcodescavengerhunt.services.EventService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository){
        this.eventRepository = eventRepository;
    }

    @Override
    public EventEntity saveEvent(EventEntity event, UserEntity user) {
        event.setUserEntity(user);
        return eventRepository.save(event);
    }

    @Override
    public List<EventEntity> getAllEvents() {
        return StreamSupport.stream(eventRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventEntity> getEventsByUser(UserEntity user) {
        return eventRepository.findByUserEntity(user);
    }

    @Override
    public Optional<EventEntity> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public EventEntity updateEvent(Long id, EventEntity event) {
        event.setId(id);
        return eventRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
