package com.sourcream.qrcodescavengerhunt.controllers;

import com.sourcream.qrcodescavengerhunt.domain.dto.ProgressSummaryDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.ProgressSummary;
import com.sourcream.qrcodescavengerhunt.mappers.Mapper;
import com.sourcream.qrcodescavengerhunt.services.ProgressService;
import com.sourcream.qrcodescavengerhunt.services.impl.ProgressServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProgressController {

    private Mapper<ProgressSummary, ProgressSummaryDto> progressMapper;
    private ProgressService progressService;
    private static final Logger logger = LoggerFactory.getLogger(ProgressServiceImpl.class);

    public ProgressController(Mapper<ProgressSummary, ProgressSummaryDto> progressMapper, ProgressService progressService) {
        this.progressMapper = progressMapper;
        this.progressService = progressService;
    }

    @PostMapping("/progress/{eventId}/{locationId}")
    public ResponseEntity<ProgressSummaryDto> createProgress(@PathVariable("eventId") Long eventId,
                                                             @PathVariable("locationId") Long locationId){

        ProgressSummary summary = progressService.saveProgress(eventId, locationId);
        logger.info("Progress Summary (controller): score={}, eventName={}, count={}", summary.getScore(), summary.getEventName(), summary.getCount());

        if (summary == null) {
            return ResponseEntity.badRequest().build();
        }

        if (summary.getScore() == null){
            return new ResponseEntity<>(progressMapper.mapTo(summary), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(progressMapper.mapTo(summary), HttpStatus.CREATED);
        }
    }
}
