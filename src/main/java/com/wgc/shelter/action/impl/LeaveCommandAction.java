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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Action
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LeaveCommandAction extends AbstractCommandAction {


    @Override
    @Transactional
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        SendMessage messageToSend = createEmptySendMessageForUserChat(update);
        User user = getExistingUser(update);
        Locale locale = new Locale(user.getLocale());
        roomService.findRoomByParticipant(user.getTelegramUserId())
                .map(room -> {
                    if (Objects.equals(user.getTelegramUserId(), room.getOwnerId())) {
                        messageToSend.setReplyMarkup(new InlineKeyboardMarkup(List.of(List.of(createLeaveOrDestroyButton(user)))));
                        messageToSend.setText(messageSource.getMessage(MessageCode.CANT_LEAVE_WISH_TO_DELETE.getCode(), null, locale));
                        return room;
                    }
                    room.getPlayers().remove(user.getTelegramUserId());
                    roomService.save(room);
                    userService.save(user.setState(UserActionState.NEW_USER));
                    messageToSend.setText(messageSource.getMessage(MessageCode.ROOM_LEFT.getCode(), null, locale));
                    return room;
                }).orElseGet(() -> {
                    userService.save(user.setState(UserActionState.NEW_USER));
                    messageToSend.setText(messageSource.getMessage(MessageCode.NO_ACTIVE_ROOM.getCode(), null, locale));
                    return null;
                });
        TelegramApiExecutorWrapper.execute(executor, messageToSend);
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.LEAVE;
    }
}
