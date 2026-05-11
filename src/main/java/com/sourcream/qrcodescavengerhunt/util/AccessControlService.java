package com.sourcream.qrcodescavengerhunt.util;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.Role;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AccessControlService {
    private final UserContext userContext;

    public AccessControlService(UserContext userContext) {
        this.userContext = userContext;
    }

    public UserEntity currentUser() {
        return userContext.getCurrentUser();
    }

    public void requireEventOwnerOrAdmin(EventEntity event) {
        UserEntity user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return;
        }

        if (event == null || event.getUserEntity() == null || event.getUserEntity().getId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        if (!event.getUserEntity().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own events");
        }
    }

    public void requireSameUserOrAdmin(String email) {
        UserEntity user = currentUser();
        if (user.getRole() == Role.ADMIN) {
            return;
        }

        if (!user.getEmail().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own events");
        }
    }
}
