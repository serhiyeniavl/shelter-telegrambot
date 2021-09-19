package com.wgc.shelter.action;

import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.common.BaseSpringBootTestClass;
import com.wgc.shelter.common.UpdateBotMessageSetup;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class StartCommandActionTest extends BaseSpringBootTestClass {

    @Test
    @DisplayName("New user")
    void onUpdateReceived() throws TelegramApiException {
        long telegramUserId = 100L;

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", "en-US",
                UserCommand.START.getCommand(), "hello msg", null);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        //send available commands
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(Mockito.any(BotApiMethod.class));
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(Mockito.any(BotApiMethod.class));

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User expected = User.builder()
                .telegramUserId(telegramUserId)
                .state(UserActionState.NEW_USER)
                .locale("en_US")
                .chatId(String.valueOf(telegramUserId)).build();
        User actual = userRepository.findByTelegramUserId(telegramUserId).get();

        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("New user bot request failed - no user was saved")
    void onUpdateReceivedBotRequestFailed() throws TelegramApiException {
        long telegramUserId = 100L;

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", "en-US",
                UserCommand.START.getCommand(), "hello msg", null);
        Mockito.doThrow(new RuntimeException()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertThrows(RuntimeException.class, () -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        Assertions.assertTrue(userRepository.findByTelegramUserId(100L).isEmpty());
    }
}