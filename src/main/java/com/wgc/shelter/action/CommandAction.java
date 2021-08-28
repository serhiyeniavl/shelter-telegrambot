package com.wgc.shelter.action;

import com.wgc.shelter.action.model.UserCommand;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface CommandAction {

    BotApiMethod handleCommand(Update update);

    UserCommand commandType();
}
