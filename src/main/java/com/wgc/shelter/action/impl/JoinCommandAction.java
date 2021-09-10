package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import com.wgc.shelter.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;
import java.util.Objects;

@Action
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JoinCommandAction implements CommandAction {

    UserService userService;
    MessageSource messageSource;

    @Override
    @Transactional
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        SendMessage messageToSend = SendMessage.builder()
                .chatId(UpdateObjectWrapperUtils.getChaId(update))
                .text("")
                .build();
        Long userTelegramId = UpdateObjectWrapperUtils.getUserTelegramId(update);
        User user = userService.retrieveExistingUser(userTelegramId);
        Locale locale = new Locale(user.getLocale());
        UserActionState userState = user.getState();
        if (Objects.equals(UserActionState.NEW_USER, userState)) {
            user.setState(UserActionState.JOINING_ROOM);
            userService.save(user);
            messageToSend.setText(messageSource.getMessage(MessageCode.INPUT_ROOM_NUMBER.getCode(), null, locale));
        }
        TelegramApiExecutorWrapper.execute(executor, messageToSend);
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.JOIN;
    }
}
