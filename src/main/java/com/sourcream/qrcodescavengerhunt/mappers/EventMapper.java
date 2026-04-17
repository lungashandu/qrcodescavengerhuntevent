package com.sourcream.qrcodescavengerhunt.mappers;

import com.sourcream.qrcodescavengerhunt.domain.dto.EventDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@org.mapstruct.Mapper(componentModel = "spring",
unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper extends Mapper<EventEntity, EventDto> {

    @Override
    @Mapping(source = "userEntity.id", target = "userId")
    EventDto mapTo(EventEntity entity);

    @Override
    @Mapping(source = "userId", target = "userEntity.id")
    EventEntity mapFrom(EventDto dto);
}
