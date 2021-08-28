package com.wgc.shelter.config.bot;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.model.UserCommand;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class BotCommandsConfiguration {


    @Bean("botCommands")
    Map<UserCommand, CommandAction> registerCommands(List<CommandAction> commands) {
        return commands.stream().collect(Collectors.toMap(CommandAction::commandType, Function.identity()));
    }
}
