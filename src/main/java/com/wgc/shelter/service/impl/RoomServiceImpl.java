package com.wgc.shelter.service.impl;

import com.wgc.shelter.model.Room;
import com.wgc.shelter.repository.RoomRepository;
import com.wgc.shelter.service.RoomService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoomServiceImpl implements RoomService {

    RoomRepository roomRepository;


    @Override
    public void createRoom(Room room) {
        roomRepository.save(room);
    }
}
