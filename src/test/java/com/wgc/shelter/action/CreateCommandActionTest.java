package com.wgc.shelter.action;

import com.wgc.shelter.common.MongoClearAllCollections;
import com.wgc.shelter.common.UpdateBotMessageSetup;
import com.wgc.shelter.controller.TelegramLongPollingController;
import com.wgc.shelter.model.Room;
import com.wgc.shelter.model.RoomState;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Collections;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class CreateCommandActionTest extends MongoClearAllCollections {

    @SpyBean
    private TelegramLongPollingController telegramLongPollingController;

    @Test
    void onUpdateReceived() throws TelegramApiException {
        long telegramUserId = 100L;
        userRepository.save(User.builder()
                .telegramUserId(telegramUserId)
                .state(UserActionState.NEW_USER)
                .locale("en_US")
                .build());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", "en-US",
                "/create", "input participants quantity");
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();
        Assertions.assertAll(
                () -> Assertions.assertEquals(
                        User.builder().telegramUserId(telegramUserId).state(UserActionState.CREATE_ROOM).locale("en_US").build(),
                        actualUser),

                () -> Assertions.assertNotNull(actualRoom.getLastActionDate()),
                () -> Assertions.assertNotNull(actualRoom.getId()),
                () -> Assertions.assertEquals(
                        Room.builder().ownerId(telegramUserId).state(RoomState.NEW).playersQuantity(4)
                                .players(Collections.singleton(actualUser.getTelegramUserId()))
                                .build(),
                        roomRepository.findByOwnerId(telegramUserId).get().setId(null).setLastActionDate(null))
        );


    }

    @Test
    void onUpdateReceivedBotRequestFailed() throws TelegramApiException {
        long telegramUserId = 100L;
        User initialUser = userRepository.save(User.builder()
                .telegramUserId(telegramUserId)
                .state(UserActionState.NEW_USER)
                .locale("en_US")
                .build());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", "en-US",
                "/create", "input participants quantity");
        Mockito.doThrow(new RuntimeException()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertThrows(RuntimeException.class, () -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        Assertions.assertAll(
                () -> Assertions.assertEquals(initialUser, userRepository.findByTelegramUserId(100L).get()),
                () -> Assertions.assertTrue(roomRepository.findByOwnerId(telegramUserId).isEmpty())
        );
    }
}