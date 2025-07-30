package com.sourcream.qrcodescavengerhunt.services.impl;

import com.google.api.Http;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import com.sourcream.qrcodescavengerhunt.repositories.EventRepository;
import com.sourcream.qrcodescavengerhunt.repositories.UserRepository;
import com.sourcream.qrcodescavengerhunt.services.EventService;
import com.sourcream.qrcodescavengerhunt.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EventServiceImpl implements EventService {

    private EventRepository eventRepository;

    private UserRepository userRepository;

    private UserContext userContext;
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);

    public EventServiceImpl(EventRepository eventRepository, UserRepository userRepository, UserContext userContext) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.userContext = userContext;
    }

    @Override
    public EventEntity saveEvent(EventEntity event) {
        try{
            if (event == null) {
                logger.error("Attempted to save a null event.");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event cannot be null");
            }

            String email = userContext.getCurrentUserEmail();
            if (email == null || email.isBlank()){
                logger.error("UserContext returned null or blank email");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised: No user email found");
            }

            UserEntity user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        logger.error("No user found for email: {}", email);
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found. Please complete registration first.");
                    });

            event.setUserEntity(user);
            logger.info("{} is attempting to save an event {}", email, event.getEventName());
            return eventRepository.save(event);

        } catch (ResponseStatusException e){
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while saving event", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save event");
        }
    }

    @Override
    public List<EventEntity> getAllEvents() {
        try{
            List<EventEntity> allEvents = eventRepository.findAll();
            if (allEvents == null){
                logger.warn("eventRepository.findAll returned null");
                return List.of();
            }

            return StreamSupport.stream(allEvents.spliterator(), false)
                    .collect(Collectors.toList());

        } catch (Exception e){
            logger.error("Error fetching all events", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to retrieve events");
        }
    }

    @Override
    public List<EventEntity> getEventsByUser(String email) {
        try{
            String userEmail = userContext.getCurrentUserEmail();
            if (email == null || email.isBlank()){
                logger.warn("Attempted to fetch events with a null or blank email");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email cannot be blank or null");
            }

            Optional<UserEntity> user = userRepository.findByEmail(email);
            logger.info("{} is requesting to view events created by {}",
                    userEmail != null ? userEmail : "Unknown user",
                    email);

            return user.map(userEntity -> {
                List<EventEntity> events = eventRepository.findByUserEntity(userEntity);
                if (events == null) {
                    logger.warn("eventRepository.findByUserEntity returned null for user: {}", email);
                    return List.<EventEntity>of();
                }
                return StreamSupport.stream(events.spliterator(), false)
                        .collect(Collectors.toList());
            }).orElseGet(() -> {
                logger.warn("No user found for email: {}", email);
                return List.<EventEntity>of();
            });

        }catch (Exception e) {
            logger.error("Unexpected error in getEventsByUser for email: {}", email);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to fetch user events");
        }

    }

    @Override
    public Optional<EventEntity> getEventById(Long id) {
        try{
            if (id == null) {
                logger.error("Attempted to fetch an event with null ID.");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event ID cannot be null");
            }

            String email = userContext.getCurrentUserEmail();
            if (email == null || email.isBlank()){
                logger.error("UserContext returned null or blank email");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorised: No user email found");
            }

            logger.info("{} has requested to event {} by id", email, id);
            return eventRepository.findById(id);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error fetch event by ID: {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve event");
        }

    }

    @Override
    public Boolean isExists(Long id) {
        return eventRepository.existsById(id);
    }

    @Override
    public EventEntity partialEventUpdate(Long id, EventEntity event) {

        try{
            if (id == null){
                logger.warn("Attempted to update an event with null ID");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event ID cannot be null");
            }

            if (event == null){
                logger.warn("Attempted to update a null event");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event data is required");
            }

            String userEmail;
            try {
                userEmail = userContext.getCurrentUserEmail();
            } catch (Exception e){
                logger.warn("Failed to retrieve current user email", e);
                userEmail = "Unknown user";
            }

            event.setId(id);

            Optional<EventEntity> result = eventRepository.findById(id);
            if(result.isPresent()){
                EventEntity existingEvent = result.get();
                existingEvent.setEventName(event.getEventName());
                existingEvent.setDescription(event.getDescription());
                existingEvent.setStartTime(event.getStartTime());
                existingEvent.setEndTime(event.getEndTime());

                logger.info("{} is attempting to update event {}", userEmail, id);
                return eventRepository.save(existingEvent);

            } else {
                logger.warn("User {} attempting to update non-existent event with ID {}", userEmail, id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
            }

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during partial update for event ID: {}", id);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update event");
        }
    }

    @Override
    public void deleteEvent(Long id) {
        try {
            if (id == null){
                logger.warn("Attempted to delete event with with null ID");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Event ID cannot be null");
            }

            String userEmail;
            try {
                userEmail = userContext.getCurrentUserEmail();
            } catch (Exception e){
                logger.warn("Failed to retrieve current user email", e);
                userEmail = "Unknown user";
            }

            if (!isExists(id)){
                logger.warn("User {} attempted to delete non-existent event ID {}", userEmail, id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found");
            }

            logger.info("{} is attempting to delete event {}", userEmail, id);
            eventRepository.deleteById(id);

        } catch (ResponseStatusException e) {
            throw e;
        }catch (Exception e) {
            logger.error("Unexpected error deleting event ID {}", id, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete event");
        }



    }
}
