package com.wgc.shelter.action.response;

import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.common.BaseSpringBootTestClass;
import com.wgc.shelter.common.UpdateBotMessageSetup;
import com.wgc.shelter.config.RoomConfiguration;
import com.wgc.shelter.model.Room;
import com.wgc.shelter.model.RoomState;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.Set;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class CreateRoomResponseResolverTest extends BaseSpringBootTestClass {

    @Autowired
    private RoomConfiguration roomConfiguration;

    @Test
    @DisplayName("Correct quantity was received")
    void inputQuantityTest() throws TelegramApiException {
        long telegramUserId = 100L;
        String quantity = "7";

        User initialUser = saveUser(telegramUserId, UserActionState.CREATE_ROOM, EN_US.toString());
        Room initialRoom = saveRoom(telegramUserId, Set.of(telegramUserId), 4, RoomState.NEW, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                quantity, messageSource.getMessage(MessageCode.ROOM_SUCCESSFULLY_CREATED_WAIT_FOR_OTHERS.getCode(),
                        new Object[] {initialRoom.getUniqueNumber()}, EN_US), null,
                false);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(initialUser.setState(UserActionState.WAITING_OTHERS_TO_JOIN), actualUser),

                () -> Assertions.assertTrue(actualRoom.getLastActionDate().isAfter(initialRoom.getLastActionDate())),
                () -> Assertions.assertEquals(initialRoom.setState(RoomState.WAITING_TO_JOIN).setPlayersQuantity(Integer.valueOf(quantity))
                        .setLastActionDate(null), actualRoom.setLastActionDate(null))
        );
    }

    @Test
    @DisplayName("Incorrect quantity was received")
    void inputInvalidQuantityTest() throws TelegramApiException {
        long telegramUserId = 100L;
        String quantity = "342";

        User initialUser = saveUser(telegramUserId, UserActionState.CREATE_ROOM, EN_US.toString());
        Room initialRoom = saveRoom(telegramUserId, Set.of(telegramUserId), 4, RoomState.NEW, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                quantity, messageSource.getMessage(MessageCode.QUANTITY_SHOULD_BE_BETWEEN.getCode(),
                        new Object[]{roomConfiguration.getMin(), roomConfiguration.getMax()}, EN_US), null,
                false);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(initialUser, actualUser),

                () -> Assertions.assertEquals(initialRoom.setLastActionDate(null), actualRoom.setLastActionDate(null))
        );
    }

    @Test
    @DisplayName("Not numeric input was received")
    void notNumberInputQuantityTest() throws TelegramApiException {
        long telegramUserId = 100L;
        String quantity = "342fas";

        User initialUser = saveUser(telegramUserId, UserActionState.CREATE_ROOM, EN_US.toString());
        Room initialRoom = saveRoom(telegramUserId, Set.of(telegramUserId), 4, RoomState.NEW, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                quantity, messageSource.getMessage(MessageCode.QUANTITY_SHOULD_BE_BETWEEN.getCode(),
                        new Object[]{roomConfiguration.getMin(), roomConfiguration.getMax()}, EN_US), null,
                false);

        Mockito.verifyNoInteractions(telegramLongPollingController);

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(initialUser, actualUser),

                () -> Assertions.assertEquals(initialRoom.setLastActionDate(null), actualRoom.setLastActionDate(null))
        );
    }

}