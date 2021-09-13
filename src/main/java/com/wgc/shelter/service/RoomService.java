package com.wgc.shelter.service;

import com.wgc.shelter.model.Room;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional(propagation = Propagation.SUPPORTS)
public interface RoomService {

    void save(Room room);

    Room retrieveExistingRoom(Long userTelegramId);

    Optional<Room> findRoom(Long userTelegramId);

    Optional<Room> findNonStartedRoom(Long userTelegramId);

    Optional<Room> findRoomByParticipant(Long participantId);

    void deleteRoom(Long ownerId);
}
