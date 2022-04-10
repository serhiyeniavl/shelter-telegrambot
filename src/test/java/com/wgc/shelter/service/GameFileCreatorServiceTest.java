package com.wgc.shelter.service;

import com.wgc.shelter.common.BaseSpringBootTestClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

class GameFileCreatorServiceTest extends BaseSpringBootTestClass {

    @Autowired
    GameCreatorService gameCreatorService;

    @Test
    @DisplayName("Create game test")
    void createGameTest() {
        Map<Long, String> userText = gameCreatorService.createGame(Set.of(100L, 101L), Locale.forLanguageTag("ru"));

        System.out.println(userText.get(100L));
        System.out.println(userText.get(101L));

        Assertions.assertAll(
                () -> userText.values().forEach(v -> Assertions.assertTrue(v.contains("Player №")))
        );
    }
}
