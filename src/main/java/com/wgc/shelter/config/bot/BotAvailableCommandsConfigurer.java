package com.wgc.shelter.config.bot;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.config.LanguagesConfiguration;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.glassfish.jersey.internal.guava.Sets;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

//@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BotAvailableCommandsConfigurer {

    private static final String COMMAND = "command.";

    Map<String, CommandAction> botCommands;
    LanguagesConfiguration languagesConfiguration;
    MessageSource messageSource;

    public BotAvailableCommandsConfigurer(LanguagesConfiguration languagesConfiguration,
                                          MessageSource messageSource,
                                          @Qualifier("botCommands") Map<String, CommandAction> botCommands) {
        this.botCommands = botCommands;
        this.languagesConfiguration = languagesConfiguration;
        this.messageSource = messageSource;
    }


}
