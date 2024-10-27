package com.sourcream.qrcodescavengerhunt.mappers.impl;

import com.sourcream.qrcodescavengerhunt.domain.dto.LocationDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class LocationMapperImpl implements Mapper<LocationEntity, LocationDto> {

    private final ModelMapper modelMapper;

    public LocationMapperImpl(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }

    @Override
    public LocationDto mapTo(LocationEntity locationEntity) {
        return modelMapper.map(locationEntity, LocationDto.class);
    }

    @Override
    public LocationEntity mapFrom(LocationDto locationDto) {
        return modelMapper.map(locationDto, LocationEntity.class);
    }
}
