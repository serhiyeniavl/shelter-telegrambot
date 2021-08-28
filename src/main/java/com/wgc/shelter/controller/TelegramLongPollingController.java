package com.wgc.shelter.controller;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.config.bot.BotCredentialsConfiguration;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramLongPollingController extends TelegramLongPollingBot {

    BotCredentialsConfiguration credentials;

    Map<UserCommand, CommandAction> commands;

    public TelegramLongPollingController(BotCredentialsConfiguration credentials,
                                         @Qualifier("botCommands") Map<UserCommand, CommandAction> commands) {
        this.credentials = credentials;
        this.commands = commands;
    }

    @Override
    public String getBotUsername() {
        return credentials.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return credentials.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
    }
}
