package com.wgc.shelter.action;

import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.common.BaseSpringBootTestClass;
import com.wgc.shelter.common.UpdateBotMessageSetup;
import com.wgc.shelter.model.UserActionState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class DescriptionCommandActionTest extends BaseSpringBootTestClass {

    @Test
    @DisplayName("Description")
    void descriptionCommandTest() throws TelegramApiException {

        saveUser(100L, UserActionState.NEW_USER, EN_US.toString());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(100L, "user", EN_US.toString(),
                UserCommand.DESCRIPTION.getCommand(), messageSource.getMessage(MessageCode.DESCRIPTION.getCode(), null, EN_US), null);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));
    }

}