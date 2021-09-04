package com.wgc.shelter.service;

import com.wgc.shelter.model.Room;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface RoomService {

    @Transactional(propagation = Propagation.SUPPORTS)
    void createRoom(Room room);
}
