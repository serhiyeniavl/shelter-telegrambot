package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Action
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StartCommandAction implements CommandAction {

    UserService userService;
    MessageSource messageSource;

    @Override
    public void handleCommand(final TelegramLongPollingBot executor, final Update update) {
        Message message = update.getMessage();
        Long telegramUserId = message.getFrom().getId();
        if (userService.findByTelegramUserId(telegramUserId).isEmpty()) {
            Locale locale = Locale.forLanguageTag(message.getFrom().getLanguageCode());
            userService.addNewUser(telegramUserId, locale);
            var messageToSend = SendMessage.builder()
                    .chatId(String.valueOf(update.getMessage().getChatId()))
                    .text(messageSource.getMessage("hello", null, locale))
                    .build();
            TelegramApiExecutorWrapper.execute(executor, messageToSend);
        }
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.START;
    }
}
