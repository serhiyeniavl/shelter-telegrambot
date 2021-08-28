package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.model.UserCommand;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiConsumer;

@Action
public class CreateCommandAction implements CommandAction {

    @Override
    public BiConsumer<TelegramLongPollingBot, Update> handleCommand() {
        return (executor, update) -> {

        };
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.CREATE;
    }
}
