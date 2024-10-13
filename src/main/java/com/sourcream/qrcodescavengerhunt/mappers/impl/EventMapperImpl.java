package com.sourcream.qrcodescavengerhunt.mappers.impl;

import com.sourcream.qrcodescavengerhunt.domain.dto.EventDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.EventEntity;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EventMapperImpl implements Mapper<EventEntity, EventDto> {

    private ModelMapper modelMapper;

    public EventMapperImpl(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }
    @Override
    public EventDto mapTo(EventEntity event) {
        return modelMapper.map(event, EventDto.class);
    }

    @Override
    public EventEntity mapFrom(EventDto eventDto) {
        return modelMapper.map(eventDto, EventEntity.class);
    }
}
