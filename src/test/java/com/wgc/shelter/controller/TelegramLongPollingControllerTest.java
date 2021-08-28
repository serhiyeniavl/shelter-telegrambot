package com.wgc.shelter.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TelegramLongPollingControllerTest {

    @Autowired
    private TelegramLongPollingController bot;


    @Test
    void onUpdateReceived() {
        bot.onUpdateReceived(null);
    }
}