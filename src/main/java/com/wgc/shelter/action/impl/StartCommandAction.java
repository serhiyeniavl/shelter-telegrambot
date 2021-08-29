package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.controller.TelegramLongPollingController;
import com.wgc.shelter.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.function.BiConsumer;

@Action
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StartCommandAction implements CommandAction {

    UserService userService;
    MessageSource messageSource;

    @Override
    @Transactional
    public BiConsumer<TelegramLongPollingController, Update> handleCommand() {
        return (executor, update) -> {
            Long telegramUserId = update.getMessage().getFrom().getId();
            if (userService.findByTelegramUserId(telegramUserId).isEmpty()) {
                userService.addNewUser(telegramUserId);
                var messageToSend = SendMessage.builder()
                        .chatId(String.valueOf(update.getMessage().getChatId()))
                        .text(messageSource.getMessage("hello", null, Locale.ENGLISH)) //TODO: message prop put in enum
                        .build();
                TelegramApiExecutorWrapper.execute(executor, messageToSend);
            }
        };
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.START;
    }
}
