package com.wgc.shelter.config.bot;

import com.wgc.shelter.action.CommandAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class BotCommandsConfiguration {


    @Bean("botCommands")
    Map<String, CommandAction> registerCommands(List<CommandAction> commands) {
        return commands.stream().collect(Collectors.toMap(commandAction -> commandAction.commandType().getCommand(), Function.identity()));
    }
}
