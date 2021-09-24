package com.wgc.shelter.action.response.impl;

import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.response.UserResponseResolver;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import com.wgc.shelter.config.RoomConfiguration;
import com.wgc.shelter.model.Room;
import com.wgc.shelter.model.RoomState;
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
public class CreateRoomResponseResolver implements UserResponseResolver {

    RoomConfiguration roomConfiguration;
    MessageSource messageSource;
    RoomService roomService;
    UserService userService;

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> Optional<Method> doResolve(User user, Update update) {
        String data = UpdateObjectWrapperUtils.getData(update);
        if (StringUtils.isNumeric(data.trim())) {
            SendMessage messageToSend = UpdateObjectWrapperUtils.createEmptySendMessageForUserChat(update);
            Integer quantity = Integer.valueOf(data);
            if (isBetween(quantity)) {
                Room room = roomService.retrieveExistingRoom(user.getTelegramUserId());
                userService.save(user.setState(UserActionState.WAITING_OTHERS_TO_JOIN));
                roomService.save(room.setPlayersQuantity(quantity)
                        .setState(RoomState.WAITING_TO_JOIN)
                        .setLastActionDate(LocalDateTime.now()));

                messageToSend.setText(messageSource
                        .getMessage(MessageCode.ROOM_SUCCESSFULLY_CREATED_WAIT_FOR_OTHERS.getCode(), new Object[]{room.getUniqueNumber()}, new Locale(user.getLocale())));
            } else {
                messageToSend.setText(messageSource
                        .getMessage(MessageCode.QUANTITY_SHOULD_BE_BETWEEN.getCode(),
                                new Object[]{roomConfiguration.getMin(), roomConfiguration.getMax()}, new Locale(user.getLocale())));
            }
            return Optional.of((Method) messageToSend);
        }
        return Optional.empty();
    }

    private boolean isBetween(Integer quantity) {
        return roomConfiguration.getMin() <= quantity && quantity <= roomConfiguration.getMax();
    }

    @Override
    public UserActionState userState() {
        return UserActionState.CREATE_ROOM;
    }
}
