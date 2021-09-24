package com.wgc.shelter.action.response.impl;

import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.response.UserResponseResolver;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import com.wgc.shelter.service.RoomService;
import com.wgc.shelter.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JoinRoomResponseResolver implements UserResponseResolver {

    MessageSource messageSource;
    RoomService roomService;
    UserService userService;

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> Optional<Method> doResolve(User user, Update update) {
        String data = UpdateObjectWrapperUtils.getData(update);
        SendMessage messageToSend = UpdateObjectWrapperUtils.createEmptySendMessageForUserChat(update);
        Locale locale = new Locale(user.getLocale());
        if (StringUtils.isNumeric(data.trim())) {
            Long number = Long.valueOf(data);
            roomService.findWaitingRoomByNumber(number)
                    .map(room -> {
                        if (room.getPlayers().size() >= room.getPlayersQuantity()) {
                            messageToSend.setText(messageSource.getMessage(MessageCode.ROOM_IS_FULL.getCode(), null, locale));
                            return room;
                        }
                        room.getPlayers().add(user.getTelegramUserId());
                        roomService.save(room.setLastActionDate(LocalDateTime.now()));
                        userService.save(user.setState(UserActionState.WAITING_OTHERS_TO_JOIN));

                        messageToSend.setText(messageSource.getMessage(MessageCode.JOINED_THE_ROOM.getCode(), null, locale));
                        return room;
                    })
                    .orElseGet(() -> {
                        messageToSend.setText(messageSource.getMessage(MessageCode.ROOM_NOT_FOUND.getCode(), null, locale));
                        return null;
                    });
        } else {
            messageToSend.setText(messageSource
                    .getMessage(MessageCode.INVALID_ROOM_NUMBER.getCode(), null, locale));
        }
        return Optional.of((Method) messageToSend);
    }

    @Override
    public UserActionState userState() {
        return UserActionState.JOINING_ROOM;
    }
}
