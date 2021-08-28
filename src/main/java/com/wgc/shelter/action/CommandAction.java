package com.wgc.shelter.action;

import com.wgc.shelter.action.model.UserCommand;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiConsumer;

public interface CommandAction {

    BiConsumer<TelegramLongPollingBot, Update> handleCommand();

    UserCommand commandType();
}
