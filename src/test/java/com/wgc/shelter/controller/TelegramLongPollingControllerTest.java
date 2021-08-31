package com.wgc.shelter.controller;

import com.wgc.shelter.model.UserActionState;
import com.wgc.shelter.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void clear() {
        userRepository.deleteAll();
    }

    @Test
    void onUpdateReceived() throws TelegramApiException {
        User user = new User(100L, "user", false);
        user.setLanguageCode("en-US");

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
                .text("hello msg")
                .build();
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(hello_msg);

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(update));

        com.wgc.shelter.model.User actual = userRepository.findByTelegramUserId(100L).get();
        Assertions.assertEquals(
                com.wgc.shelter.model.User.builder().telegramUserId(100L).state(UserActionState.NEW_USER).locale("en_US").build(),
                actual);
    }

    @Test
    void onUpdateReceivedBotRequestFailed() throws TelegramApiException {
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
                .text("hello msg")
                .build();
        Mockito.doThrow(new RuntimeException()).when(telegramLongPollingController).execute(hello_msg);

        Assertions.assertThrows(RuntimeException.class, () -> telegramLongPollingController.onUpdateReceived(update));

        Assertions.assertTrue(userRepository.findByTelegramUserId(100L).isEmpty());
    }
}