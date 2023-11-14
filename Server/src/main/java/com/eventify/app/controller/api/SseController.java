package com.eventify.app.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.eventify.app.service.SseService;

@RestController
@RequestMapping("/teller")
public class SseController {
    private final Logger logger = LoggerFactory.getLogger(SseController.class);

    @GetMapping("/subscribe/{subscriberId}")
    public SseEmitter streamSse(@PathVariable String subscriberId) {
        SseEmitter emitter = new SseEmitter(3_600_000L);
        logger.info("Emitter created with timeout {} for subscriberId {}", emitter.getTimeout(), subscriberId);
        SseService.addEmitter(subscriberId, emitter);

        emitter.onTimeout(() -> {
            logger.info("Emitter timed out");
            emitter.complete();
            SseService.removeEmitter(subscriberId);
        });

        emitter.onCompletion(() -> {
            logger.info("Emitter completed");
            SseService.removeEmitter(subscriberId);
        });

        return emitter;
    }
}

record SimpleResponse(String content) {
}


