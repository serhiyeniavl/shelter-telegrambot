package com.wgc.shelter.action;

import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.common.BaseSpringBootTestClass;
import com.wgc.shelter.common.UpdateBotMessageSetup;
import com.wgc.shelter.model.UserActionState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class InputPlayersQuantityCommandActionTest extends BaseSpringBootTestClass {

    @Test
    @DisplayName("User response with \"decline\" answer and bot request to input quantity again")
    void inputPlayersQuantityTest() throws TelegramApiException {
        long telegramUserId = 100L;
        saveUser(telegramUserId, UserActionState.CREATE_ROOM, EN_US.toString());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.INPUT.getCommand(), messageSource.getMessage(MessageCode.INPUT_ROOM_PARTICIPANTS_QUANTITY.getCode(), null, EN_US), null);
        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());
    }

    @Test
    @DisplayName("If bot got /input_players_quantity from user's keyboard")
    void inputPlayersQuantityFromUserKeyboardTest() {
        long telegramUserId = 100L;
        saveUser(telegramUserId, UserActionState.WAITING_OTHERS_TO_JOIN, EN_US.toString());

        Mockito.verifyNoInteractions(telegramLongPollingController);
    }

}