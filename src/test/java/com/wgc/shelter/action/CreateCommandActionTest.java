package com.wgc.shelter.action;

import com.wgc.shelter.action.factory.KeyboardFactory;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.common.BaseSpringBootTestClass;
import com.wgc.shelter.common.UpdateBotMessageSetup;
import com.wgc.shelter.model.Room;
import com.wgc.shelter.model.RoomState;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class CreateCommandActionTest extends BaseSpringBootTestClass {

    @Test
    @DisplayName("User successfully created room")
    void onUpdateReceived() throws TelegramApiException {
        long telegramUserId = 100L;
        saveUser(telegramUserId, UserActionState.NEW_USER, EN_US.toString());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.CREATE.getCommand(), messageSource.getMessage(MessageCode.INPUT_ROOM_PARTICIPANTS_QUANTITY.getCode(), null, EN_US), null);
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User expectedUser = User.builder()
                .telegramUserId(telegramUserId)
                .state(UserActionState.CREATE_ROOM)
                .locale(EN_US.toString())
                .chatId(String.valueOf(telegramUserId)).build();
        User actualUser = userRepository.findByTelegramUserId(telegramUserId).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedUser, actualUser),

                () -> Assertions.assertNotNull(actualRoom.getLastActionDate()),
                () -> Assertions.assertNotNull(actualRoom.getId()),
                () -> Assertions.assertNotNull(actualRoom.getUniqueNumber()),
                () -> Assertions.assertEquals(
                        Room.builder().ownerId(telegramUserId).state(RoomState.NEW).playersQuantity(4)
                                .players(Collections.singleton(actualUser.getTelegramUserId()))
                                .build(),
                        roomRepository.findByOwnerId(telegramUserId).get().setId(null).setLastActionDate(null).setUniqueNumber(null))
        );
    }

    @Test
    @DisplayName("Failed attempt to create room - bot request fail - rollback transaction")
    void onUpdateReceivedBotRequestFailed() throws TelegramApiException {
        long telegramUserId = 100L;
        User initialUser = saveUser(telegramUserId, UserActionState.NEW_USER, EN_US.toString());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.CREATE.getCommand(), messageSource.getMessage(MessageCode.INPUT_ROOM_PARTICIPANTS_QUANTITY.getCode(), null, EN_US), null);
        Mockito.doThrow(new RuntimeException()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertThrows(RuntimeException.class, () -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        Assertions.assertAll(
                () -> Assertions.assertEquals(initialUser, userRepository.findByTelegramUserId(100L).get()),
                () -> Assertions.assertTrue(roomRepository.findByOwnerId(telegramUserId).isEmpty())
        );
    }

    @Test
    @DisplayName("User already has a room")
    void onUpdateReceivedUserHasRoom() throws TelegramApiException {
        long telegramUserId = 100L;
        User expectedUser = saveUser(telegramUserId, UserActionState.CREATE_ROOM, EN_US.toString());
        Room expectedRoom = saveRoom(telegramUserId, Set.of(telegramUserId), 4, RoomState.NEW, LocalDateTime.now());

        InlineKeyboardButton buttonYes = KeyboardFactory.createInlineKeyboardButton("Confirm", UserCommand.DESTROY.getCommand());
        InlineKeyboardButton buttonNo = KeyboardFactory.createInlineKeyboardButton("Decline", UserCommand.INPUT.getCommand());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.CREATE.getCommand(), messageSource.getMessage(MessageCode.ALREADY_CREATED.getCode(), null, EN_US),
                new InlineKeyboardMarkup(List.of(List.of(buttonYes, buttonNo))));
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedUser, actualUser),
                () -> Assertions.assertNotNull(actualRoom.getLastActionDate()),
                () -> Assertions.assertEquals(expectedRoom.setLastActionDate(null), actualRoom.setLastActionDate(null))
        );
    }

    @Test
    @DisplayName("User is in active session with owned room")
    void onUpdateReceivedUserHasActiveSessionWithOwnedRoom() throws TelegramApiException {
        long telegramUserId = 100L;
        User expectedUser = saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        Room expectedRoom = saveRoom(telegramUserId, Set.of(telegramUserId), 4, RoomState.NEW, LocalDateTime.now());

        InlineKeyboardButton destroyButton = KeyboardFactory.createInlineKeyboardButton("Confirm", UserCommand.DESTROY.getCommand());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.CREATE.getCommand(), messageSource.getMessage(MessageCode.CANT_DO_ACTION_WISH_TO_LEAVE.getCode(), null, EN_US),
                new InlineKeyboardMarkup(List.of(List.of(destroyButton))));
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedUser, actualUser),
                () -> Assertions.assertNotNull(actualRoom.getLastActionDate()),
                () -> Assertions.assertEquals(expectedRoom.setLastActionDate(null), actualRoom.setLastActionDate(null))
        );
    }

}