package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import com.sourcream.qrcodescavengerhunt.services.LocationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {



    public LocationServiceImpl() {
    }

    @Override
    public LocationEntity saveLocation(LocationEntity locationEntity) {

        return null;
    }

    @Override
    public List<LocationEntity> getLocationByEvent(EventEntity event) {
        return null;
    }

    @Override
    public Optional<LocationEntity> getLocationById(Long id) {
        return Optional.empty();
    }

    @Override
    public LocationEntity updateLocation(Long id, LocationEntity location) {
        return null;
    }

    @Override
    public void deleteLocation(Long id) {

    }
}
