package com.wgc.shelter.service.impl;

import com.wgc.shelter.model.Room;
import com.wgc.shelter.repository.RoomRepository;
import com.wgc.shelter.service.RoomService;
import com.wgc.shelter.service.exception.ShelterBotException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoomServiceImpl implements RoomService {

    RoomRepository roomRepository;


    @Override
    public void save(Room room) {
        roomRepository.save(room);
    }

    @Override
    public Room retrieveExistingRoom(Long userTelegramId) {
        return findRoom(userTelegramId)
                .orElseThrow(() -> new ShelterBotException("Room not found"));
    }

    @Override
    public Optional<Room> findRoom(Long userTelegramId) {
        return roomRepository.findByOwnerId(userTelegramId);
    }

    @Override
    public Optional<Room> findRoomByParticipant(Long participantId) {
        return roomRepository.findByPlayersContains(participantId);
    }

    @Override
    public void deleteRoom(Long ownerId) {
        roomRepository.deleteByOwnerId(ownerId);
    }
}
