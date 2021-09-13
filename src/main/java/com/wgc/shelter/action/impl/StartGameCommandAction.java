package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.AbstractCommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Action
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StartGameCommandAction extends AbstractCommandAction {

    GameCreatorService gameCreatorService;

    @Override
    @Transactional
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        SendMessage messageToSend = createEmptySendMessageForUserChat(update);
        User user = getExistingUser(update);
        Locale locale = new Locale(user.getLocale());
        if (Objects.equals(UserActionState.WAITING_OTHERS_TO_JOIN, user.getState())) {
            roomService.findNonStartedRoom(user.getTelegramUserId())
                    .map(room -> {
                        if (room.getPlayersQuantity().equals(room.getPlayers().size())) {
                            Map<Long, String> gameSetups = gameCreatorService.createGame(room.getPlayers(), locale);
                            room.getPlayers().forEach(playerId -> {
                                User player = userService.retrieveExistingUser(playerId);
                                SendMessage individualMessage = createEmptySendMessageForUserChat(player.getChatId());
                                individualMessage.setText(gameSetups.get(playerId));
                                TelegramApiExecutorWrapper.execute(executor, individualMessage);
                                userService.save(player.setState(UserActionState.NEW_USER));
                            });
                            roomService.save(room.setState(RoomState.STARTED));
                        } else {
                            //send to wait more people (maybe propose to decrease quantity and start with people who joined if this is enough to start)
                        }
                        return room;
                    }).orElseGet(() -> {
//started or not found
                        return null;
                    });
            messageToSend.setText(messageSource.getMessage(MessageCode.INPUT_ROOM_NUMBER.getCode(), null, locale));
        } else {
            messageToSend.setReplyMarkup(new InlineKeyboardMarkup(List.of(List.of(createLeaveOrDestroyButton(user)))));
            messageToSend.setText(messageSource.getMessage(MessageCode.CANT_DO_ACTION_WISH_TO_LEAVE.getCode(), null, locale));
        }
        TelegramApiExecutorWrapper.execute(executor, messageToSend);
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.START_GAME;
    }
}
