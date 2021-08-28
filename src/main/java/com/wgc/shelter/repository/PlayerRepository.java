package com.wgc.shelter.repository;

import com.wgc.shelter.model.Player;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PlayerRepository extends MongoRepository<Player, String> {

    Optional<Player> findByTelegramUserId(Long telegramUserId);
}
