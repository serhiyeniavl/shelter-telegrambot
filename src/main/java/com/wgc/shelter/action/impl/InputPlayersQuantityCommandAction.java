package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.AbstractCommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.Objects;

@Action
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InputPlayersQuantityCommandAction extends AbstractCommandAction {


    @Override
    @Transactional(readOnly = true)
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        SendMessage messageToSend = createEmptySendMessageForUserChat(update);
        User user = getExistingUser(update);
        Locale locale = new Locale(user.getLocale());
        UserActionState userState = user.getState();
        if (Objects.equals(userState, UserActionState.CREATE_ROOM)) {
            messageToSend.setText(messageSource.getMessage(MessageCode.INPUT_ROOM_PARTICIPANTS_QUANTITY.getCode(),
                    null, locale));
            TelegramApiExecutorWrapper.execute(executor, messageToSend);
        }
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.INPUT;
    }
}
