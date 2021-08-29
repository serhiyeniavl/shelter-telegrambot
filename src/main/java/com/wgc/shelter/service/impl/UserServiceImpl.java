package com.wgc.shelter.service.impl;

import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import com.wgc.shelter.repository.UserRepository;
import com.wgc.shelter.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;


    @Override
    public Optional<User> findByTelegramUserId(Long telegramUserId) {
        return userRepository.findByTelegramUserId(telegramUserId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public User addNewUser(Long telegramUserId) {
        return userRepository.save(User.builder()
                .telegramUserId(telegramUserId)
                .state(UserActionState.NEW_USER)
                .build());
    }
}