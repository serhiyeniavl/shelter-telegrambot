package com.wgc.shelter.repository;

import com.wgc.shelter.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, Long> {

    Optional<Room> findByOwnerId(Long ownerId);

    Optional<Room> findByPlayersContains(Long player);

    void deleteByOwnerId(Long ownerId);
}
