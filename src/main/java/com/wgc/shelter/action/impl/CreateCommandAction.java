package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.AbstractCommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.factory.KeyboardFactory;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.model.Room;
import com.wgc.shelter.model.RoomState;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

@Action
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CreateCommandAction extends AbstractCommandAction {

    private static final Integer ROOM_MINIMUM_PLAYERS_QUANTITY = 4;


    @Override
    @Transactional
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        SendMessage messageToSend = createEmptySendMessageForUserChat(update);
        User user = getExistingUser(update);
        Locale locale = new Locale(user.getLocale());
        UserActionState userState = user.getState();
        if (Objects.equals(userState, UserActionState.NEW_USER)) {
            createNewRoom(messageToSend, user.getTelegramUserId(), user, locale);
        } else if (Objects.equals(userState, UserActionState.CREATE_ROOM)) {
            sendRoomAlreadyCreated(messageToSend, locale);
        } else {
            sendCantCreateRoomWithActiveSession(messageToSend, user, locale);
        }
        TelegramApiExecutorWrapper.execute(executor, messageToSend);
    }

    private void sendCantCreateRoomWithActiveSession(SendMessage messageToSend, User user, Locale locale) {
        messageToSend.setReplyMarkup(new InlineKeyboardMarkup(List.of(List.of(createLeaveOrDestroyButton(user)))));
        messageToSend.setText(messageSource.getMessage(MessageCode.CANT_DO_ACTION_WISH_TO_LEAVE.getCode(), null, locale));
    }

    private void createNewRoom(SendMessage messageToSend, Long userTelegramId, User user, Locale locale) {
        roomService.save(Room.builder()
                .ownerId(userTelegramId)
                .state(RoomState.NEW)
                .uniqueNumber(generateRandomNumber())
                .lastActionDate(LocalDateTime.now())
                .playersQuantity(ROOM_MINIMUM_PLAYERS_QUANTITY)
                .players(Collections.singleton(user.getTelegramUserId()))
                .build());
        userService.save(user.setState(UserActionState.CREATE_ROOM));

        messageToSend.setText(messageSource.getMessage(MessageCode.INPUT_ROOM_PARTICIPANTS_QUANTITY.getCode(),
                null, locale));
    }

    private Long generateRandomNumber() {
        while (true) {
            long number = 1000 + new Random().nextLong(8999);
            if (roomService.findWaitingRoomByNumber(number).isEmpty()) {
                return number;
            }
        }
    }

    private void sendRoomAlreadyCreated(SendMessage messageToSend, Locale locale) {
        InlineKeyboardButton buttonYes = KeyboardFactory.createInlineKeyboardButton(
                messageSource.getMessage(MessageCode.ANSWER_YES.getCode(), null, locale), UserCommand.DESTROY.getCommand());
        InlineKeyboardButton buttonNo = KeyboardFactory.createInlineKeyboardButton(
                messageSource.getMessage(MessageCode.ANSWER_NO.getCode(), null, locale), UserCommand.INPUT.getCommand());

        messageToSend.setText(messageSource.getMessage(MessageCode.ALREADY_CREATED.getCode(), null, locale));
        messageToSend.setReplyMarkup(new InlineKeyboardMarkup(List.of(List.of(buttonYes, buttonNo))));
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.CREATE;
    }
}
