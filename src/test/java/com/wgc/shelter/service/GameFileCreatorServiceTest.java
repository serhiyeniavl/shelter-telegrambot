package com.wgc.shelter.service;

import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.common.BaseSpringBootTestClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GameFileCreatorServiceTest extends BaseSpringBootTestClass {

    @Autowired
    GameCreatorService gameCreatorService;

    @Test
    @DisplayName("Create game test")
    void createGameTest() {
        Map<Long, String> userText = gameCreatorService.createGame(Set.of(100L, 101L), Locale.forLanguageTag("en"));

        Assertions.assertAll(
                () -> userText.values().forEach(v -> Assertions.assertTrue(v.contains("Player №")))
        );
    }

}