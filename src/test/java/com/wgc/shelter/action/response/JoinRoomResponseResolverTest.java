package com.wgc.shelter.action.response;

import com.wgc.shelter.action.message.MessageCode;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.Set;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class JoinRoomResponseResolverTest extends BaseSpringBootTestClass {


    @Test
    @DisplayName("Join room")
    void joinRoomTest() throws TelegramApiException {
        long telegramUserId = 100L;

        saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        saveUser(telegramUserId + 1, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User initialJoiningUser = saveUser(telegramUserId + 2, UserActionState.JOINING_ROOM, EN_US.toString());

        Room initialRoom = saveRoom(telegramUserId, Set.of(telegramUserId, telegramUserId + 1), 4, RoomState.WAITING_TO_JOIN, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId + 2, "user", EN_US.toString(),
                initialRoom.getUniqueNumber().toString(), messageSource.getMessage(MessageCode.JOINED_THE_ROOM.getCode(), null, EN_US), null, false);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(initialJoiningUser.getTelegramUserId()).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(initialJoiningUser.setState(UserActionState.WAITING_OTHERS_TO_JOIN), actualUser),

                () -> Assertions.assertTrue(actualRoom.getLastActionDate().isAfter(initialRoom.getLastActionDate())),
                () -> Assertions.assertEquals(initialRoom.setPlayers(Set.of(telegramUserId, telegramUserId + 1, telegramUserId + 2)).setLastActionDate(null),
                        actualRoom.setLastActionDate(null))
        );
    }

    @Test
    @DisplayName("Join already full room")
    void joinFullRoomTest() throws TelegramApiException {
        long telegramUserId = 100L;

        saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        saveUser(telegramUserId + 1, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User initialJoiningUser = saveUser(telegramUserId + 2, UserActionState.JOINING_ROOM, EN_US.toString());

        Room initialRoom = saveRoom(telegramUserId, Set.of(telegramUserId, telegramUserId + 1), 2, RoomState.WAITING_TO_JOIN, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId + 2, "user", EN_US.toString(),
                initialRoom.getUniqueNumber().toString(), messageSource.getMessage(MessageCode.ROOM_IS_FULL.getCode(), null, EN_US), null, false);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(initialJoiningUser.getTelegramUserId()).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(initialJoiningUser, actualUser),

                () -> Assertions.assertEquals(initialRoom.setLastActionDate(null), actualRoom.setLastActionDate(null))
        );
    }

    @Test
    @DisplayName("Input invalid room number")
    void inputInvalidRoomNumberTest() throws TelegramApiException {
        long telegramUserId = 100L;

        saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        saveUser(telegramUserId + 1, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User initialJoiningUser = saveUser(telegramUserId + 2, UserActionState.JOINING_ROOM, EN_US.toString());

        Room initialRoom = saveRoom(telegramUserId, Set.of(telegramUserId, telegramUserId + 1), 4, RoomState.WAITING_TO_JOIN, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId + 2, "user", EN_US.toString(),
                "invalid", messageSource.getMessage(MessageCode.INVALID_ROOM_NUMBER.getCode(), null, EN_US), null, false);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(initialJoiningUser.getTelegramUserId()).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(initialJoiningUser, actualUser),

                () -> Assertions.assertEquals(initialRoom.setLastActionDate(null), actualRoom.setLastActionDate(null))
        );
    }

    @Test
    @DisplayName("Join non existing room")
    void joinNonExistingRoomTest() throws TelegramApiException {
        long telegramUserId = 100L;

        saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        saveUser(telegramUserId + 1, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User initialJoiningUser = saveUser(telegramUserId + 2, UserActionState.JOINING_ROOM, EN_US.toString());

        Room initialRoom = saveRoom(telegramUserId, Set.of(telegramUserId, telegramUserId + 1), 4, RoomState.WAITING_TO_JOIN, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId + 2, "user", EN_US.toString(),
                String.valueOf((initialRoom.getUniqueNumber() + 1)), messageSource.getMessage(MessageCode.ROOM_NOT_FOUND.getCode(), null, EN_US), null, false);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(initialJoiningUser.getTelegramUserId()).get();
        Room actualRoom = roomRepository.findByOwnerId(telegramUserId).get();

        Assertions.assertAll(
                () -> Assertions.assertEquals(initialJoiningUser, actualUser),

                () -> Assertions.assertEquals(initialRoom.setLastActionDate(null), actualRoom.setLastActionDate(null))
        );
    }
}
