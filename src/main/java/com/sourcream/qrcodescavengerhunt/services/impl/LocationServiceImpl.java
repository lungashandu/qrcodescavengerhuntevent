package com.sourcream.qrcodescavengerhunt.services.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import com.sourcream.qrcodescavengerhunt.repositories.LocationRepository;
import com.sourcream.qrcodescavengerhunt.services.LocationService;
import com.sourcream.qrcodescavengerhunt.util.QRCodeGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class LocationServiceImpl implements LocationService {

    LocationRepository locationRepository;

    QRCodeGenerator qrCodeGenerator;

    public LocationServiceImpl(LocationRepository locationRepository, QRCodeGenerator qrCodeGenerator) {
        this.locationRepository = locationRepository;
        this.qrCodeGenerator = qrCodeGenerator;
    }

    @Override
    public LocationEntity saveLocation(LocationEntity locationEntity) throws Exception {
        String qrCodeDownloadURL = qrCodeGenerator.generateQRCodeAndUpload(locationEntity.getName());
        locationEntity.setQrCodeUrl(qrCodeDownloadURL);
        return locationRepository.save(locationEntity);
    }

    @Override
    public List<LocationEntity> getLocationByEvent(EventEntity event) {
        if (locationRepository.findByEventEntity(event).isEmpty()){
            return null;
        } else {
            return StreamSupport.stream(
                    locationRepository.findByEventEntity(event).spliterator(), false).collect(Collectors.toList());
        }

    }

    @Override
    public Optional<LocationEntity> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    @Override
    public LocationEntity updateLocation(Long id, LocationEntity location) {
        return locationRepository.findById(id).map(existingLocation -> {
            if (location.getName() != null){existingLocation.setName(location.getName());}
            if (location.getHint() != null){existingLocation.setHint(location.getHint());}
            if (location.getChallenge() != null){existingLocation.setChallenge(location.getChallenge());}

            return locationRepository.save(existingLocation);
        }).orElseThrow(() ->
                new RuntimeException("Location does not exist"));
    }

    @Override
    public void deleteLocation(Long id) {
        locationRepository.deleteById(id);
    }
}
