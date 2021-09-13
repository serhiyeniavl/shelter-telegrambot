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

@Action
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DestroyCommandAction extends AbstractCommandAction {


    @Override
    @Transactional
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        SendMessage messageToSend = createEmptySendMessageForUserChat(update);
        User owner = getExistingUser(update);
        Locale locale = new Locale(owner.getLocale());
        roomService.findRoom(owner.getTelegramUserId())
                .map(room -> {
                    room.getPlayers().forEach(roomParticipantId -> {
                        User participant = userService.retrieveExistingUser(roomParticipantId);
                        userService.save(participant.setState(UserActionState.NEW_USER));
                    });
                    roomService.deleteRoom(room.getOwnerId());
                    messageToSend.setText(messageSource.getMessage(MessageCode.ROOM_SUCCESSFULLY_DELETED.getCode(), null, locale));
                    return room;
                }).orElseGet(() -> {
                    messageToSend.setText(messageSource.getMessage(MessageCode.USER_DOESNT_HAVE_ROOM.getCode(), null, locale));
                    return null;
                });
        TelegramApiExecutorWrapper.execute(executor, messageToSend);
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.DESTROY;
    }
}
