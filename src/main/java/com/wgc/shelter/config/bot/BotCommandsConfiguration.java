package com.wgc.shelter.config.bot;

import com.wgc.shelter.action.CommandAction;
import com.wgc.shelter.action.response.UserResponseResolver;
import com.wgc.shelter.model.UserActionState;
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

    @Bean("rawInputCommands")
    Map<UserActionState, UserResponseResolver> registerRawInputResolvers(List<UserResponseResolver> resolvers) {
        return resolvers.stream().collect(Collectors.toMap(UserResponseResolver::userState, Function.identity()));
    }
}
