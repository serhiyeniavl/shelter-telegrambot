package com.wgc.shelter.service;

import com.wgc.shelter.model.User;

import java.util.Optional;

public interface UserService {

    Optional<User> findByTelegramUserId(Long telegramUserId);

    User addNewUser(Long telegramUserId);
}
