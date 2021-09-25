package com.wgc.shelter.common;

import com.wgc.shelter.controller.TelegramLongPollingController;
import com.wgc.shelter.model.Room;
import com.wgc.shelter.model.RoomState;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import com.wgc.shelter.repository.RoomRepository;
import com.wgc.shelter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseSpringBootTestClass {

    public static final Locale EN_US = new Locale("en_US");

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoomRepository roomRepository;
    @Autowired
    protected MessageSource messageSource;

    @SpyBean
    protected TelegramLongPollingController telegramLongPollingController;


    @BeforeEach
    void clear() {
        userRepository.deleteAll();
        roomRepository.deleteAll();
    }

    protected User saveUser(Long telegramUserId, UserActionState state, String locale) {
        return userRepository.save(User.builder()
                .telegramUserId(telegramUserId)
                .chatId(String.valueOf(telegramUserId))
                .state(state)
                .locale(locale)
                .build());
    }

    protected Room saveRoom(long telegramUserId, Set<Long> players, int playersQuantity, RoomState state, LocalDateTime lastActionDate) {
        return roomRepository.save(Room.builder()
                .ownerId(telegramUserId)
                .lastActionDate(lastActionDate)
                .state(state)
                .players(players)
                .playersQuantity(playersQuantity)
                .uniqueNumber(new Random().nextLong(9999))
                .build());
    }

}
