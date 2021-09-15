package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.AbstractCommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.factory.KeyboardFactory;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.config.RoomConfiguration;
import com.wgc.shelter.model.Room;
import com.wgc.shelter.model.RoomState;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import com.wgc.shelter.service.GameCreatorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Action
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class StartGameCommandAction extends AbstractCommandAction {

    GameCreatorService gameCreatorService;
    RoomConfiguration roomConfiguration;

    @Override
    @Transactional
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        SendMessage messageToSend = createEmptySendMessageForUserChat(update);
        User user = getExistingUser(update);
        Locale locale = new Locale(user.getLocale());
        boolean roomStarted = false;
        if (Objects.equals(UserActionState.WAITING_OTHERS_TO_JOIN, user.getState())) {
            roomStarted = roomService.findNonStartedRoom(user.getTelegramUserId())
                    .map(room -> room.getPlayersQuantity() <= room.getPlayers().size()
                            ? generateGame(executor, locale, room)
                            : waitForAllOrStartAnywayIfEnough(messageToSend, locale, room))
                    .orElseGet(() -> notFoundOrAlreadyStarted(messageToSend, locale));
        } else {
            messageToSend.setText(messageSource.getMessage(MessageCode.CANT_DO_ACTION_RIGHT_NOW_SEE_HELP.getCode(), null, locale));
        }

        if (!roomStarted) {
            TelegramApiExecutorWrapper.execute(executor, messageToSend);
        }
    }

    private Boolean notFoundOrAlreadyStarted(SendMessage messageToSend, Locale locale) {
        messageToSend.setText(messageSource.getMessage(MessageCode.NON_STARTED_ROOM_NOT_FOUND.getCode(), null, locale));
        return false;
    }

    private boolean waitForAllOrStartAnywayIfEnough(SendMessage messageToSend, Locale locale, Room room) {
        if (room.getPlayers().size() >= roomConfiguration.getMin()) {
            InlineKeyboardButton startAnywayButton = KeyboardFactory.createInlineKeyboardButton(
                    messageSource.getMessage(MessageCode.START_ANYWAY.getCode(), null, locale), UserCommand.START_GAME_ANYWAY.getCommand());
            messageToSend.setReplyMarkup(new InlineKeyboardMarkup(List.of(List.of(startAnywayButton))));
        }
        messageToSend.setText(messageSource.getMessage(MessageCode.WAIT_TO_JOIN_ALL_PLAYERS.getCode(), null, locale));
        return false;
    }

    protected boolean generateGame(TelegramLongPollingBot executor, Locale locale, Room room) {
        Map<Long, String> gameSetups = gameCreatorService.createGame(room.getPlayers(), locale);
        room.getPlayers().forEach(playerId -> {
            User player = userService.retrieveExistingUser(playerId);
            SendMessage individualMessage = createEmptySendMessageForUserChat(player.getChatId());
            individualMessage.setText(gameSetups.get(playerId));
            TelegramApiExecutorWrapper.execute(executor, individualMessage);
            userService.save(player.setState(UserActionState.NEW_USER));
        });
        roomService.save(room.setState(RoomState.STARTED).setLastActionDate(LocalDateTime.now()));
        return true;
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.START_GAME;
    }
}
