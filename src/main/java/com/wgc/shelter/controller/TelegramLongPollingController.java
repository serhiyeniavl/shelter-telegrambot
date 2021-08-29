package com.wgc.shelter.controller;

import com.wgc.shelter.action.UserInputResolver;
import com.wgc.shelter.config.bot.BotCredentialsConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramLongPollingController extends TelegramLongPollingBot {

    BotCredentialsConfiguration credentials;

    UserInputResolver userInputResolver;

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
        userInputResolver
                .isCommand(update, command -> command.handleCommand().accept(this, update))
                .orRawInput(rawInputHandler -> rawInputHandler.handleInput(update));
    }
}
