package com.wgc.shelter.action;

import com.wgc.shelter.action.model.UserCommand;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandAction {

    @Transactional
    void handleCommand(final TelegramLongPollingBot executor, final Update update);

    UserCommand commandType();
}
