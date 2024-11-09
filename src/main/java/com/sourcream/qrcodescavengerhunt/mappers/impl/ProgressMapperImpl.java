package com.sourcream.qrcodescavengerhunt.mappers.impl;

import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary;
import com.sourcream.qrcodescavengerhunt.domain.dto.ProgressSummaryDto;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import org.modelmapper.ModelMapper;

public class ProgressMapperImpl implements Mapper<ProgressSummary, ProgressSummaryDto> {

    private final ModelMapper modelMapper;

    public ProgressMapperImpl(ModelMapper modelMapper) {
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
