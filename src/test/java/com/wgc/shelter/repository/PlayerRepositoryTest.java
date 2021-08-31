package com.wgc.shelter.repository;

import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
class PlayerRepositoryTest {

    @Autowired
    private UserRepository playerRepository;

    @BeforeEach
    void clean() {
        playerRepository.deleteAll();
    }

//    @Test
//    void saveTest() {
//        long telegramUserId = 4312412341L;
//        User expected = new User(telegramUserId, UserActionState.NEW_USER);
//        playerRepository.save(expected);
//
//        Assertions.assertEquals(expected, playerRepository.findByTelegramUserId(telegramUserId).get());
//    }

}