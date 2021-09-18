package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.AbstractCommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.model.User;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Action
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DescriptionCommandAction extends AbstractCommandAction {


    @Override
    @Transactional
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        SendMessage messageToSend = createEmptySendMessageForUserChat(update);
        User user = getExistingUser(update);
        messageToSend.setText(messageSource.getMessage(MessageCode.DESCRIPTION.getCode(), null, new Locale(user.getLocale())));
        TelegramApiExecutorWrapper.execute(executor, messageToSend);
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.DESCRIPTION;
    }
}
