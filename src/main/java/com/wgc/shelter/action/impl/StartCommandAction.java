package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.AbstractCommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import com.wgc.shelter.service.RoomService;
import com.wgc.shelter.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Action
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StartCommandAction extends AbstractCommandAction {

    @Autowired
    public StartCommandAction(UserService userService, RoomService roomService, MessageSource messageSource) {
        super(userService, roomService, messageSource);
    }

    @Override
    @Transactional
    public void handleCommand(final TelegramLongPollingBot executor, final Update update) {
        Message message = update.getMessage();
        Long telegramUserId = UpdateObjectWrapperUtils.getUserTelegramId(update);
        if (userService.findByTelegramUserId(telegramUserId).isEmpty()) {
            Locale locale = Locale.forLanguageTag(message.getFrom().getLanguageCode());
            userService.addNewUser(telegramUserId, locale);
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
