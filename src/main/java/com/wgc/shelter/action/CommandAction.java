package com.wgc.shelter.action;

import com.wgc.shelter.action.model.UserCommand;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandAction {

    void handleCommand(final TelegramLongPollingBot executor, final Update update);

    UserCommand commandType();
}
