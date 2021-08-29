package com.wgc.shelter.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;

@SpringBootTest
@ActiveProfiles("test")
class TelegramLongPollingControllerTest {

    @SpyBean
    private TelegramLongPollingController telegramLongPollingController;

    @Test
    void onUpdateReceived() throws TelegramApiException { //TODO: clean up this mess a little bit, transactions doesnt work
        User user = new User(100L, "user", false);

        Message message = new Message();
        message.setMessageId(1);
        message.setFrom(user);
        message.setText("/start");
        message.setEntities(Collections.singletonList(new MessageEntity(EntityType.BOTCOMMAND, 0, 15)));
        message.setChat(new Chat(1L, "private"));

        Update update = new Update();
        update.setUpdateId(1);
        update.setMessage(message);

        SendMessage hello_msg = SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text("hello msg") //TODO: message prop put in enum
                .build();
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(hello_msg);

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(update));
    }
}