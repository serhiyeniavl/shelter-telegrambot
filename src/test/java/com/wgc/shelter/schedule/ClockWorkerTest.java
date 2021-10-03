package com.wgc.shelter.schedule;

import com.wgc.shelter.common.BaseSpringBootTestClass;
import com.wgc.shelter.model.RoomState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

class ClockWorkerTest extends BaseSpringBootTestClass {


    @Autowired
    ClockWorker clockWorker;

    @Test
    @DisplayName("Delete all inactive rooms")
    void clearInactiveRooms() {
        saveRoom(100L, Set.of(), 4, RoomState.WAITING_TO_JOIN, LocalDateTime.now());
        saveRoom(101L, Set.of(), 4, RoomState.WAITING_TO_JOIN, LocalDateTime.now().minus(7000L, ChronoUnit.MILLIS));
        saveRoom(102L, Set.of(), 4, RoomState.WAITING_TO_JOIN, LocalDateTime.now().minus(710000L, ChronoUnit.MILLIS));

        clockWorker.clearInactiveRooms();

        Assertions.assertEquals(2, roomRepository.findAll().size());
    }

}