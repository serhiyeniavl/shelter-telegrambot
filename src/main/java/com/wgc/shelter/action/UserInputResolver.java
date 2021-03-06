package com.wgc.shelter.action;

import com.wgc.shelter.action.impl.UnknownCommandAction;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import com.wgc.shelter.aop.annotation.Loggable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserInputResolver {

    Map<String, CommandAction> commands;

    UserRawInputHandler userInputHandler;

    public UserInputResolver(@Qualifier("botCommands") Map<String, CommandAction> commands, UserRawInputHandler userInputHandler) {
        this.commands = commands;
        this.userInputHandler = userInputHandler;
    }

    @Loggable
    public RawInput isCommand(Update update, Consumer<CommandAction> commandAction) {
        Message message = update.getMessage();
        if (Objects.nonNull(message) && message.isCommand()) {
            commandAction.accept(this.commands.getOrDefault(message.getText(), new UnknownCommandAction()));
            return new RawInput(null, false);
        } else if (update.hasCallbackQuery() && UpdateObjectWrapperUtils.isCallbackDataIsCommand(update.getCallbackQuery().getData())) {
            UpdateObjectWrapperUtils.UserCallbackData userCallbackData = UpdateObjectWrapperUtils.parseCallbackData(update);
            commandAction.accept(this.commands.getOrDefault(userCallbackData.userCommand().getCommand(), new UnknownCommandAction()));
            return new RawInput(null, false);
        } else {
            return new RawInput(userInputHandler, true);
        }
    }

    public static class RawInput {

        UserRawInputHandler inputHandler;
        boolean isRawInput;

        public RawInput(@Nullable UserRawInputHandler userInputHandler, boolean invoke) {
            this.inputHandler = userInputHandler;
            this.isRawInput = invoke;
        }

        public void orRawInput(Consumer<UserRawInputHandler> rawInputAction) {
            if (isRawInput) {
                Optional.ofNullable(inputHandler).ifPresent(rawInputAction);
            }
        }
    }
}
