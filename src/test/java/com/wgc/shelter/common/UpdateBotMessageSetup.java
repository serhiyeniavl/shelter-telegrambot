package com.wgc.shelter.common;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.EntityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Collections;

@Data
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UpdateBotMessageSetup {

    Update update;
    Message message;
    User user;
    SendMessage messageToSend;

    public static UpdateBotMessageSetup buildUpdateObject(Long id, String firstName, String languageCode, String command, String messageText,
                                                          InlineKeyboardMarkup replyMarkup) {
        User user = new User(id, firstName, false);
        user.setLanguageCode(languageCode);

        Message message = new Message();
        message.setMessageId(1);
        message.setFrom(user);
        message.setText(command);
        message.setEntities(Collections.singletonList(new MessageEntity(EntityType.BOTCOMMAND, 0, 15)));
        message.setChat(new Chat(1L, "private"));

        Update update = new Update();
        update.setUpdateId(1);
        update.setMessage(message);

        SendMessage messageToSend = SendMessage.builder()
                .chatId(String.valueOf(update.getMessage().getChatId()))
                .text(messageText)
                .replyMarkup(replyMarkup)
                .build();

        return new UpdateBotMessageSetup(update, message, user, messageToSend);
    }
}
