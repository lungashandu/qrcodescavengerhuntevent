package com.sourcream.qrcodescavengerhunt.mappers.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary;
import com.sourcream.qrcodescavengerhunt.domain.dto.ProgressSummaryDto;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProgressSummaryMapperImpl implements Mapper<ProgressSummary, ProgressSummaryDto> {

    private final ModelMapper modelMapper;

    public ProgressSummaryMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public ProgressSummaryDto mapTo(ProgressSummary progressSummary) {
        return modelMapper.map(progressSummary, ProgressSummaryDto.class);
    }

    @Override
    public ProgressSummary mapFrom(ProgressSummaryDto progressSummaryDto) {
        return modelMapper.map(progressSummaryDto, ProgressSummary.class);
    }
}
