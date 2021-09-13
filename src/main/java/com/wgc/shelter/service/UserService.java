package com.wgc.shelter.service;

import com.wgc.shelter.model.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

public interface UserService {

    Optional<User> findByTelegramUserId(Long telegramUserId);

    @Transactional(propagation = Propagation.SUPPORTS)
    User retrieveExistingUser(Long telegramUserId);

    @Transactional(propagation = Propagation.SUPPORTS)
    User addNewUser(Long telegramUserId, String chaId, Locale locale);

    @Transactional(propagation = Propagation.SUPPORTS)
    User save(User user);
}
