package com.wgc.shelter.action.utils;

import com.wgc.shelter.action.model.UserCommand;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.text.TextStringBuilder;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;
import java.util.Optional;

@UtilityClass
public class UpdateObjectWrapperUtils {


    public @NonNull Long getUserTelegramId(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        } else {
            return update.getMessage().getFrom().getId();
        }
    }

    public static String getChaId(Update update) {
        return Objects.nonNull(update.getMessage())
                ? String.valueOf(update.getMessage().getChatId())
                : String.valueOf(update.getCallbackQuery().getFrom().getId());
    }

    public static TextStringBuilder toString(Update update) {
        TextStringBuilder updateString = new TextStringBuilder();
        Optional.ofNullable(update.getMessage()).ifPresent(message ->
                updateString.append("ChatId=").append(message.getChatId()).append(';')
                        .append("Text=").append(message.getText()).append(';')
                        .append("MessageId=").append(message.getMessageId()).append(';')
                        .appendNewLine()
                        .append("UserId=").append(message.getFrom().getId()).append(';')
                        .append("FirstName=").append(message.getFrom().getFirstName()).append(';')
                        .append("LangCode=").append(message.getFrom().getLanguageCode()).append(';')
                        .append("UserName=").append(message.getFrom().getUserName()).append(';')
                        .appendNewLine());
        Optional.ofNullable(update.getCallbackQuery()).ifPresent(callbackQuery ->
                updateString.append("CallbackQuery=").append(callbackQuery.getData()).append(';')
                        .append("CallbackMessageText=").append(callbackQuery.getMessage().getText())
                        .appendNewLine()
                        .append("UserId=").append(callbackQuery.getFrom().getId()).append(';')
                        .append("FirstName=").append(callbackQuery.getFrom().getFirstName()).append(';')
                        .append("LangCode=").append(callbackQuery.getFrom().getLanguageCode()).append(';')
                        .append("UserName=").append(callbackQuery.getFrom().getUserName()).append(';')

        );
        return updateString;
    }

    public static UserCallbackData parseCallbackData(Update update) {
        String data = update.getCallbackQuery().getData();
        String[] split = data.split("\\s+");
        return (split.length > 1) ? new UserCallbackData(UserCommand.fromValue(split[0]), split[1]) : new UserCallbackData(UserCommand.fromValue(split[0]), null);
    }

    public record UserCallbackData(UserCommand userCommand, String value) {
    }
}
