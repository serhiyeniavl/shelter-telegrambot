package com.wgc.shelter.service.impl;

import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import com.wgc.shelter.repository.UserRepository;
import com.wgc.shelter.service.UserService;
import com.wgc.shelter.service.exception.ShelterBotException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Locale;
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
    public User retrieveExistingUser(Long telegramUserId) {
        return findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new ShelterBotException("User not found"));
    }

    @Override
    public User addNewUser(Long telegramUserId, String chaId, Locale locale) {
        return userRepository.save(User.builder()
                .telegramUserId(telegramUserId)
                .chatId(chaId)
                .state(UserActionState.NEW_USER)
                .locale(locale.toString())
                .build());
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }
}
