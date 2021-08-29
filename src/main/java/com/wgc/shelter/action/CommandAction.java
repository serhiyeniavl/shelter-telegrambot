package com.wgc.shelter.action;

import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.controller.TelegramLongPollingController;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.function.BiConsumer;

public interface CommandAction {

    BiConsumer<TelegramLongPollingController, Update> handleCommand();

    UserCommand commandType();
}
