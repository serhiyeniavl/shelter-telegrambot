package com.wgc.shelter.action;

import com.wgc.shelter.action.factory.KeyboardFactory;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.common.BaseSpringBootTestClass;
import com.wgc.shelter.common.UpdateBotMessageSetup;
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

import java.util.List;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class JoinCommandActionTest extends BaseSpringBootTestClass {

    @Test
    @DisplayName("Join room")
    void joinRoomTest() throws TelegramApiException {
        long telegramUserId = 100L;

        User expected = saveUser(telegramUserId, UserActionState.NEW_USER, EN_US.toString());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.JOIN.getCommand(), messageSource.getMessage(MessageCode.INPUT_ROOM_NUMBER.getCode(), null, EN_US), null);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));
        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actual = userRepository.findByTelegramUserId(telegramUserId).get();
        Assertions.assertEquals(expected.setState(UserActionState.JOINING_ROOM), actual);
    }

    @Test
    @DisplayName("Can't join room due to active session")
    void cantJoinRoomDueToActiveSessionTest() throws TelegramApiException {
        long telegramUserId = 100L;

        User expected = saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());

        InlineKeyboardButton leaveButton = KeyboardFactory.createInlineKeyboardButton("Yes", UserCommand.LEAVE.getCommand());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.JOIN.getCommand(), messageSource.getMessage(MessageCode.CANT_DO_ACTION_WISH_TO_LEAVE.getCode(), null, EN_US),
                new InlineKeyboardMarkup(List.of(List.of(leaveButton))));

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actual = userRepository.findByTelegramUserId(telegramUserId).get();
        Assertions.assertEquals(expected.setState(UserActionState.WAITING_OTHERS_TO_JOIN), actual);
    }
}