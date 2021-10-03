package com.wgc.shelter.schedule;

import com.wgc.shelter.service.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClockWorker {

    @DurationUnit(ChronoUnit.MILLIS)
    @Value("${clear.room.inactivity.time.in.mills}")
    private Duration inactivityTime;

    private final RoomService roomService;

    @Scheduled(fixedDelayString = "${clear.room.delay.in.mills}", initialDelayString = "${clear.room.initial.delay.in.mills}")
    void clearInactiveRooms() {
        log.debug("Inactive rooms cleaning started...");
        roomService.clearInactiveRooms(inactivityTime);
        log.debug("Inactive rooms cleaning finished...");
    }
}
