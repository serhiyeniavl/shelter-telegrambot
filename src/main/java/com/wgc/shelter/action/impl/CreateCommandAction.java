package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.factory.KeyboardFactory;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import com.wgc.shelter.model.Room;
import com.wgc.shelter.model.RoomState;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

@Action
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateCommandAction implements CommandAction {

    private static final Integer ROOM_MINIMUM_PLAYERS_QUANTITY = 4;

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
        Long userTelegramId = UpdateObjectWrapperUtils.getUserTelegramId(update);
        User user = userService.retrieveExistingUser(userTelegramId);
        Locale locale = new Locale(user.getLocale());
        if (Objects.equals(user.getState(), UserActionState.NEW_USER)) {
            roomService.createRoom(Room.builder()
                    .ownerId(userTelegramId)
                    .state(RoomState.NEW)
                    .lastActionDate(LocalDateTime.now())
                    .playersQuantity(ROOM_MINIMUM_PLAYERS_QUANTITY)
                    .players(Collections.singleton(user.getTelegramUserId()))
                    .build());
            userService.save(user.setState(UserActionState.CREATE_ROOM));

            messageToSend.setText(messageSource.getMessage(MessageCode.INPUT_ROOM_PARTICIPANTS_QUANTITY.getCode(),
                    null, locale));
        } else {
            if (Objects.equals(user.getState(), UserActionState.CREATE_ROOM)) {
                roomAlreadyCreated(messageToSend, locale);
            }
        }
        TelegramApiExecutorWrapper.execute(executor, messageToSend);
    }

    private void roomAlreadyCreated(SendMessage messageToSend, Locale locale) {

        InlineKeyboardButton buttonYes = KeyboardFactory.createInlineKeyboardButton(
                messageSource.getMessage(MessageCode.ANSWER_YES.getCode(), null, locale));
        InlineKeyboardButton buttonNo = KeyboardFactory.createInlineKeyboardButton(
                messageSource.getMessage(MessageCode.ANSWER_NO.getCode(), null, locale));

        messageToSend.setText(messageSource.getMessage(MessageCode.ALREADY_CREATED.getCode(), null, locale));
        messageToSend.setReplyMarkup(new InlineKeyboardMarkup(KeyboardFactory.createInlineKeyboardButtonRow(buttonYes, buttonNo)));
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.CREATE;
    }
}
