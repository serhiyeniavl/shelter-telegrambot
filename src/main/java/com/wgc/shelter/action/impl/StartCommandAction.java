package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.AbstractCommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Action
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StartCommandAction extends AbstractCommandAction {


    @Override
    @Transactional
    public void handleCommand(final TelegramLongPollingBot executor, final Update update) {
        Message message = update.getMessage();
        Long telegramUserId = UpdateObjectWrapperUtils.getUserTelegramId(update);
        if (userService.findByTelegramUserId(telegramUserId).isEmpty()) {
            Locale locale = Locale.forLanguageTag(message.getFrom().getLanguageCode());
            userService.addNewUser(telegramUserId, UpdateObjectWrapperUtils.getChaId(update), locale);
            var messageToSend = SendMessage.builder()
                    .chatId(UpdateObjectWrapperUtils.getChaId(update))
                    .text(messageSource.getMessage(MessageCode.HELLO.getCode(), null, locale))
                    .build();
            TelegramApiExecutorWrapper.execute(executor, messageToSend);
        }
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.START;
    }
}
