package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.AbstractCommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import com.wgc.shelter.config.LanguagesConfiguration;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.glassfish.jersey.internal.guava.Sets;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

@Action
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StartCommandAction extends AbstractCommandAction {

    private static final String COMMAND = "command.";

    LanguagesConfiguration languagesConfiguration;

    @Override
    @Transactional
    public void handleCommand(final TelegramLongPollingBot executor, final Update update) {
        Message message = update.getMessage();
        Long telegramUserId = UpdateObjectWrapperUtils.getUserTelegramId(update);
        if (userService.findByTelegramUserId(telegramUserId).isEmpty()) {
            Locale locale = Locale.forLanguageTag(message.getFrom().getLanguageCode());
            userService.addNewUser(telegramUserId, UpdateObjectWrapperUtils.getChaId(update), locale);
            var messageToSend = SendMessage.builder()
                    .chatId(UpdateObjectWrapperUtils.getChaId(update))
                    .text(messageSource.getMessage(MessageCode.HELLO.getCode(), null, locale))
                    .build();
            TelegramApiExecutorWrapper.execute(executor, messageToSend);
            addCommandsListForUser(UpdateObjectWrapperUtils.getChaId(update), executor);
        }
    }

    private void addCommandsListForUser(String chatId, TelegramLongPollingBot executor) {
        languagesConfiguration.getAll().forEach(lang -> {
            Set<BotCommand> botCommands = Sets.newHashSet();
            Arrays.stream(UserCommand.values()).forEach(c -> {
                if (!c.isHidden()) {
                    String command = c.getCommand().substring(1);
                    botCommands.add(BotCommand.builder().description(messageSource.getMessage(COMMAND.concat(command), null, new Locale(lang.getCode())))
                            .command(command).build());
                }
            });
            TelegramApiExecutorWrapper.execute(executor, SetMyCommands.builder()
                    .languageCode(lang.getCode())
                    .commands(botCommands)
                    .scope(BotCommandScopeChat.builder().chatId(chatId).build())
                    .build());
        });
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.START;
    }
}
