package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.config.RoomConfiguration;
import com.wgc.shelter.model.Room;
import com.wgc.shelter.model.User;
import com.wgc.shelter.service.GameCreatorService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

@Action
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StartGameAnywayCommandAction extends StartGameCommandAction {

    @Autowired
    public StartGameAnywayCommandAction(GameCreatorService gameCreatorService, RoomConfiguration roomConfiguration) {
        super(gameCreatorService, roomConfiguration);
    }

    @Override
    @Transactional
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        User user = getExistingUser(update);
        Locale locale = new Locale(user.getLocale());
        Room room = roomService.retrieveExistingRoom(user.getTelegramUserId());
        roomService.save(room.setPlayersQuantity(room.getPlayers().size()));
        generateGame(executor, locale, room);
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.START_GAME_ANYWAY;
    }
}
