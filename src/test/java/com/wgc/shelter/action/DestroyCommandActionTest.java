package com.wgc.shelter.action;

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
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class DestroyCommandActionTest extends BaseSpringBootTestClass {

    @Test
    @DisplayName("/destroy command from user input")
    void onDestroyCommandReceived() throws TelegramApiException {
        long telegramUserId = 100L;
        User expectedUser1 = saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User expectedUser2 = saveUser(telegramUserId + 1, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User expectedUser3 = saveUser(telegramUserId + 2, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User expectedUser4 = saveUser(telegramUserId + 3, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());

        saveRoom(telegramUserId, Set.of(telegramUserId, telegramUserId + 1, telegramUserId + 2, telegramUserId + 3), 4,
                RoomState.WAITING_TO_JOIN, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.DESTROY.getCommand(), "Game room was successfully deleted", null);
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser1 = userRepository.findByTelegramUserId(telegramUserId).get();
        User actualUser2 = userRepository.findByTelegramUserId(telegramUserId + 1).get();
        User actualUser3 = userRepository.findByTelegramUserId(telegramUserId + 2).get();
        User actualUser4 = userRepository.findByTelegramUserId(telegramUserId + 3).get();
        Optional<Room> deletedRoom = roomRepository.findByOwnerId(telegramUserId);
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedUser1.setState(UserActionState.NEW_USER), actualUser1),
                () -> Assertions.assertEquals(expectedUser2.setState(UserActionState.NEW_USER), actualUser2),
                () -> Assertions.assertEquals(expectedUser3.setState(UserActionState.NEW_USER), actualUser3),
                () -> Assertions.assertEquals(expectedUser4.setState(UserActionState.NEW_USER), actualUser4),

                () -> Assertions.assertTrue(deletedRoom.isEmpty())
        );
    }

    @Test
    @DisplayName("/destroy callback from inline keyboard")
    void onDestroyCallbackReceived() throws TelegramApiException {
        long telegramUserId = 100L;
        User expectedUser1 = saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User expectedUser2 = saveUser(telegramUserId + 1, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User expectedUser3 = saveUser(telegramUserId + 2, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        User expectedUser4 = saveUser(telegramUserId + 3, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());

        saveRoom(telegramUserId, Set.of(telegramUserId, telegramUserId + 1, telegramUserId + 2, telegramUserId + 3), 4,
                RoomState.WAITING_TO_JOIN, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.DESTROY.getCommand(), messageSource.getMessage(MessageCode.ROOM_SUCCESSFULLY_DELETED.getCode(), null, EN_US), null);
        Message message = new Message();
        message.setText(messageSource.getMessage(MessageCode.CANT_DO_ACTION_WISH_TO_LEAVE.getCode(), null, EN_US));
        botMessageSetup.update().setMessage(null);
        CallbackQuery callbackQuery = new CallbackQuery("callbackQuery", botMessageSetup.user(), message,
                null, UserCommand.DESTROY.getCommand() + " " + telegramUserId, null, null);
        botMessageSetup.update().setCallbackQuery(callbackQuery);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser1 = userRepository.findByTelegramUserId(telegramUserId).get();
        User actualUser2 = userRepository.findByTelegramUserId(telegramUserId + 1).get();
        User actualUser3 = userRepository.findByTelegramUserId(telegramUserId + 2).get();
        User actualUser4 = userRepository.findByTelegramUserId(telegramUserId + 3).get();
        Optional<Room> deletedRoom = roomRepository.findByOwnerId(telegramUserId);
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedUser1.setState(UserActionState.NEW_USER), actualUser1),
                () -> Assertions.assertEquals(expectedUser2.setState(UserActionState.NEW_USER), actualUser2),
                () -> Assertions.assertEquals(expectedUser3.setState(UserActionState.NEW_USER), actualUser3),
                () -> Assertions.assertEquals(expectedUser4.setState(UserActionState.NEW_USER), actualUser4),

                () -> Assertions.assertTrue(deletedRoom.isEmpty())
        );
    }

    @Test
    @DisplayName("/destroy from user who has no room")
    void onDestroyCallbackReceivedWithNoRoomOwned() throws TelegramApiException {
        long telegramUserId = 100L;
        User expectedUser1 = saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());
        saveRoom(telegramUserId + 1, Set.of(telegramUserId + 1), 4, RoomState.WAITING_TO_JOIN, LocalDateTime.now());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.DESTROY.getCommand(), messageSource.getMessage(MessageCode.USER_DOESNT_HAVE_ROOM.getCode(), null, EN_US), null);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser1 = userRepository.findByTelegramUserId(telegramUserId).get();
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedUser1, actualUser1),

                () -> Assertions.assertTrue(roomRepository.findByOwnerId(telegramUserId + 1).isPresent())
        );
    }
}