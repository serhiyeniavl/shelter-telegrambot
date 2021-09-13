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
import java.util.List;
import java.util.Set;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class LeaveCommandActionTest extends BaseSpringBootTestClass {

    @Test
    @DisplayName("Leave the room")
    void leaveRoom() throws TelegramApiException {
        long telegramUserId = 100L;

        User expectedUser = saveUser(telegramUserId + 1, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        Room expectedRoom = saveRoom(telegramUserId, Set.of(101L, 102L, 103L, 104L), 4, RoomState.WAITING_TO_JOIN, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId + 1, "user", EN_US.toString(),
                UserCommand.LEAVE.getCommand(), messageSource.getMessage(MessageCode.ROOM_LEFT.getCode(), null, EN_US), null);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId + 1).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedUser.setState(UserActionState.NEW_USER), actualUser),
                () -> Assertions.assertFalse(actualRoom.getPlayers().contains(expectedUser.getTelegramUserId()))
        );
    }

    @Test
    @DisplayName("User has no room")
    void leaveNonExistingRoomTest() throws TelegramApiException {
        long telegramUserId = 100L;

        User expectedUser = saveUser(telegramUserId + 1, UserActionState.NEW_USER, EN_US.toString());
        saveRoom(telegramUserId, Set.of(106L, 102L, 103L, 104L), 4, RoomState.WAITING_TO_JOIN, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId + 1, "user", EN_US.toString(),
                UserCommand.LEAVE.getCommand(), messageSource.getMessage(MessageCode.NO_ACTIVE_ROOM.getCode(), null, EN_US), null);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId + 1).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedUser, actualUser),
                () -> Assertions.assertFalse(actualRoom.getPlayers().contains(expectedUser.getTelegramUserId()))
        );
    }

    @Test
    @DisplayName("User is room holder should confirm leaving (destroy)")
    void leaveOwnedRoom() throws TelegramApiException {
        long telegramUserId = 100L;

        User expectedUser = saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        Room expectedRoom = saveRoom(telegramUserId, Set.of(106L, 102L, 103L, 104L, telegramUserId), 5, RoomState.WAITING_TO_JOIN, null);

        InlineKeyboardButton destroyButton = KeyboardFactory.createInlineKeyboardButton("Confirm", UserCommand.DESTROY.getCommand());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.LEAVE.getCommand(), messageSource.getMessage(MessageCode.CANT_LEAVE_WISH_TO_DELETE.getCode(), null, EN_US),
                new InlineKeyboardMarkup(List.of(List.of(destroyButton))));

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedUser, actualUser),
                () -> Assertions.assertEquals(expectedRoom, actualRoom)
        );
    }
}