package com.wgc.shelter.repository;

import com.wgc.shelter.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByTelegramUserId(Long telegramUserId);
}
