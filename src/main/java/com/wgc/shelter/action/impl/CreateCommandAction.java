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
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

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
        UserActionState userState = user.getState();
        if (Objects.equals(userState, UserActionState.NEW_USER)) {
            createNewRoom(messageToSend, userTelegramId, user, locale);
        } else if (Objects.equals(userState, UserActionState.CREATE_ROOM)) {
            sendRoomAlreadyCreated(messageToSend, locale, userTelegramId);
        } else {
            sendCantCreateRoomWithActiveSession(messageToSend, userTelegramId, locale);
        }
        TelegramApiExecutorWrapper.execute(executor, messageToSend);
    }

    private void sendCantCreateRoomWithActiveSession(SendMessage messageToSend, Long userTelegramId, Locale locale) {
        String command = roomService.findRoom(userTelegramId)
                .map(room -> UserCommand.DESTROY.getCommand().concat(" " + userTelegramId))
                .orElseGet(UserCommand.LEAVE::getCommand);

        InlineKeyboardButton leaveOrDestroyApproveButton = KeyboardFactory.createInlineKeyboardButton(
                messageSource.getMessage(MessageCode.ANSWER_YES.getCode(), null, locale), command);

        messageToSend.setReplyMarkup(new InlineKeyboardMarkup(List.of(List.of(leaveOrDestroyApproveButton))));
        messageToSend.setText(messageSource.getMessage(MessageCode.CANT_CREATE_ROOM_WISH_TO_LEAVE.getCode(), null, locale));
    }

    private void createNewRoom(SendMessage messageToSend, Long userTelegramId, User user, Locale locale) {
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
    }

    private void sendRoomAlreadyCreated(SendMessage messageToSend, Locale locale, Long userTelegramId) {
        InlineKeyboardButton buttonYes = KeyboardFactory.createInlineKeyboardButton(
                messageSource.getMessage(MessageCode.ANSWER_YES.getCode(), null, locale), UserCommand.DESTROY.getCommand() + " " + userTelegramId);
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
