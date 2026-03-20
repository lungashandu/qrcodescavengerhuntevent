package com.sourcream.qrcodescavengerhunt.mappers;

import com.sourcream.qrcodescavengerhunt.domain.dto.LocationDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.LocationEntity;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;


@org.mapstruct.Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LocationMapper extends Mapper<LocationEntity, LocationDto> {

    @Override
    @Mapping(source = "eventEntity.id", target = "eventId")
    @Mapping(source = "eventEntity.eventName", target = "eventName")
    LocationDto mapTo(LocationEntity location);

    @Override
    @Mapping(source = "eventId", target = "eventEntity.id")
    LocationEntity mapFrom(LocationDto locationDto);
}
