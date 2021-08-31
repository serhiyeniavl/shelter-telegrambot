package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.model.UserCommand;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UnknownCommandAction implements CommandAction {

    @Override
    public void handleCommand(TelegramLongPollingBot executor, Update update) {

    }

    @Override
    public UserCommand commandType() {
        return null;
    }
}
