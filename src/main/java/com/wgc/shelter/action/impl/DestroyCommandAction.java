package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import com.wgc.shelter.service.RoomService;
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

@Action
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DestroyCommandAction implements CommandAction {

    UserService userService;
    RoomService roomService;
    MessageSource messageSource;

    @Override
    @Transactional
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        SendMessage messageToSend = SendMessage.builder()
                .chatId(UpdateObjectWrapperUtils.getChaId(update))
                .text("")
                .build();
        Long telegramUserId = getTelegramUserId(update);
        User owner = userService.retrieveExistingUser(telegramUserId);
        Locale locale = new Locale(owner.getLocale());
        roomService.findRoom(telegramUserId)
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

    private Long getTelegramUserId(Update update) {
        if (update.hasCallbackQuery()) {
            return Long.valueOf(UpdateObjectWrapperUtils.parseCallbackData(update).getSecond());
        } else {
            return update.getMessage().getFrom().getId();
        }
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.DESTROY;
    }
}
