package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.controller.TelegramLongPollingController;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiConsumer;

public class UnknownCommandAction implements CommandAction {

    @Override
    public BiConsumer<TelegramLongPollingController, Update> handleCommand() {
        return (executor, update) -> {

        };
    }

    @Override
    public UserCommand commandType() {
        return null;
    }
}
