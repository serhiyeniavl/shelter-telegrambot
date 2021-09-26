package com.wgc.shelter.service;

import com.wgc.shelter.common.BaseSpringBootTestClass;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GameCreatorServiceImplTest extends BaseSpringBootTestClass {

    @Autowired
    GameCreatorService gameCreatorService;

    @Test
    @DisplayName("Create game test")
    void createGameTest() {
        gameCreatorService.createGame(Set.of(100L), EN_US);
    }

}