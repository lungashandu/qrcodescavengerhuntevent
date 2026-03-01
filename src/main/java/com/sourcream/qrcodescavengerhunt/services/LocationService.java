package com.sourcream.qrcodescavengerhunt.services;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;

import java.util.List;
import java.util.Optional;

public interface LocationService {

    LocationEntity saveLocation(LocationEntity locationEntity) throws Exception;

    List<LocationEntity> getLocationByEvent(EventEntity event);

    Optional<LocationEntity> getLocationById(Long id);

    Boolean isExists(Long id);

    LocationEntity updateLocation(Long id, LocationEntity location);

    void deleteLocation(Long id);
}
