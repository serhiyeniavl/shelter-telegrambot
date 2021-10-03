package com.wgc.shelter.repository;

import com.wgc.shelter.model.Room;
import com.wgc.shelter.model.RoomState;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, Long> {

    Optional<Room> findByOwnerId(Long ownerId);

    Optional<Room> findByPlayersContains(Long player);

    void deleteByOwnerId(Long ownerId);

    Optional<Room> findByOwnerIdAndStateNot(Long userTelegramId, RoomState started);

    Optional<Room> findByUniqueNumberAndStateIs(Long number, RoomState waitingToJoin);

    List<Room> findAllByLastActionDateLessThanEqual(LocalDateTime maxTime);
}
