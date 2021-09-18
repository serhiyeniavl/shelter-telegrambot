package com.wgc.shelter.controller;

import com.wgc.shelter.action.UserInputResolver;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.config.bot.BotCredentialsConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeAllPrivateChats;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TelegramLongPollingController extends TelegramLongPollingBot {

    BotCredentialsConfiguration credentials;

    UserInputResolver userInputResolver;

    @PostConstruct
    void init() {
        TelegramApiExecutorWrapper.execute(this, SetMyCommands.builder()
                .languageCode("en")
                .command(BotCommand.builder().description("test desc in english").command("test").build())
                .scope(BotCommandScopeAllPrivateChats.builder().build())
                .build());
        TelegramApiExecutorWrapper.execute(this, SetMyCommands.builder()
                .languageCode("ru")
                .command(BotCommand.builder().description("Тест на русском").command("test").build())
                .scope(BotCommandScopeAllPrivateChats.builder().build())
                .build());
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
        userInputResolver
                .isCommand(update, command -> command.handleCommand(this, update))
                .orRawInput(rawInputHandler -> rawInputHandler.handleInput(update));
    }
}
