package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.AbstractCommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import com.wgc.shelter.service.RoomService;
import com.wgc.shelter.service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Locale;

@Action
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JoinCommandAction extends AbstractCommandAction {

    @Autowired
    public JoinCommandAction(UserService userService, RoomService roomService, MessageSource messageSource) {
        super(userService, roomService, messageSource);
    }

    @Override
    @Transactional
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        SendMessage messageToSend = createEmptySendMessageForUserChat(update);
        User user = getExistingUser(update);
        Locale locale = new Locale(user.getLocale());
        if (List.of(UserActionState.NEW_USER, UserActionState.JOINING_ROOM).contains(user.getState())) {
            userService.save(user.setState(UserActionState.JOINING_ROOM));
            messageToSend.setText(messageSource.getMessage(MessageCode.INPUT_ROOM_NUMBER.getCode(), null, locale));
        } else {
            messageToSend.setReplyMarkup(new InlineKeyboardMarkup(List.of(List.of(createLeaveOrDestroyButton(user)))));
            messageToSend.setText(messageSource.getMessage(MessageCode.CANT_DO_ACTION_WISH_TO_LEAVE.getCode(), null, locale));
        }
        TelegramApiExecutorWrapper.execute(executor, messageToSend);
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.JOIN;
    }
}
