package com.wgc.shelter.service;

import com.wgc.shelter.model.Room;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface RoomService {

    void createRoom(Room room);

    Room retrieveExistingRoom(Long userTelegramId);

    Optional<Room> findRoom(Long userTelegramId);

    void deleteRoom(Long ownerId);
}
