package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.repositories.EventRepository;
import com.sourcream.qrcodescavengerhunt.repositories.UserRepository;
import com.sourcream.qrcodescavengerhunt.services.EventService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;

    private UserRepository userRepository;

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository){
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    @Override
    public EventEntity saveEvent(EventEntity event) {
        OidcUser oidcUser = (OidcUser) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Optional<UserEntity> user = userRepository.findByEmail(oidcUser.getEmail());
        if(user.isPresent()){
            event.setUserEntity(user.get());
        }else {
            throw new RuntimeException("User unauthorized");
        }
        return eventRepository.save(event);
    }

    @Override
    public List<EventEntity> getAllEvents() {
        return StreamSupport.stream(eventRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventEntity> getEventsByUser(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);
        return user.map(userEntity -> StreamSupport.stream(
                eventRepository.findByUserEntity(userEntity).spliterator(),
                false).collect(Collectors.toList())).orElseGet(() -> null);
    }

    @Override
    public Optional<EventEntity> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    @Override
    public Boolean isExists(Long id) {
        return eventRepository.existsById(id);
    }

    @Override
    public EventEntity partialEventUpdate(Long id, EventEntity event) {
        event.setId(id);
        Optional<EventEntity> result = eventRepository.findById(id);
        if(result.isPresent()){
            EventEntity existingEvent = result.get();
            existingEvent.setEventName(event.getEventName());
            existingEvent.setDescription(event.getDescription());
            existingEvent.setStartTime(event.getStartTime());
            existingEvent.setEndTime(event.getEndTime());

            return eventRepository.save(existingEvent);
        } else {
            throw new RuntimeException("Event does not exist");
        }
    }

    @Override
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }
}
