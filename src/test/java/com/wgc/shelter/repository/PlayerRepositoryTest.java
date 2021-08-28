package com.wgc.shelter.repository;

import com.wgc.shelter.model.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

@DataMongoTest
class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    @BeforeEach
    void clean() {
        playerRepository.deleteAll();
    }

    @Test
    void saveTest() {
        long telegramUserId = 4312412341L;
        Player expected = new Player(null, telegramUserId);
        playerRepository.save(expected);

        Assertions.assertEquals(expected, playerRepository.findByTelegramUserId(telegramUserId).get());
    }

}