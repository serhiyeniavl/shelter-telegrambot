package com.wgc.shelter.action;

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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actual = userRepository.findByTelegramUserId(telegramUserId).get();
        Assertions.assertEquals(expected.setState(UserActionState.JOINING_ROOM), actual);
    }
}