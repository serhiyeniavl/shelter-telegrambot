package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.model.UserCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Action
public class CreateCommandAction implements CommandAction {
    @Override
    public SendMessage handleCommand(Update update) {
        return null;
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.CREATE;
    }
}
