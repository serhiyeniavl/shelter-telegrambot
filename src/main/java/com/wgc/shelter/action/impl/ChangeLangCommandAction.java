package com.wgc.shelter.action.impl;

import com.google.common.collect.Lists;
import com.wgc.shelter.action.AbstractCommandAction;
import com.wgc.shelter.action.annotation.Action;
import com.wgc.shelter.action.factory.KeyboardFactory;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import com.wgc.shelter.config.LanguagesConfiguration;
import com.wgc.shelter.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Action
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ChangeLangCommandAction extends AbstractCommandAction {

    LanguagesConfiguration languagesConfiguration;

    @Override
    @Transactional
    public void handleCommand(TelegramLongPollingBot executor, Update update) {
        SendMessage messageToSend = createEmptySendMessageForUserChat(update);
        User user = getExistingUser(update);
        String possibleCallbackDataValue = UpdateObjectWrapperUtils.parseCallbackData(update).value();
        if (update.hasCallbackQuery() && Objects.nonNull(possibleCallbackDataValue)) {
            changeUserLanguage(update, user, possibleCallbackDataValue)
                    .ifPresent(message -> TelegramApiExecutorWrapper.execute(executor, message));
            return;
        }
        Locale locale = new Locale(user.getLocale());
        List<List<InlineKeyboardButton>> buttons = Lists.newArrayList();
        languagesConfiguration.getAll().forEach(lang ->
                buttons.add(List.of(KeyboardFactory.createInlineKeyboardButton(lang.getName() + lang.getUnicode(), lang.getCode()))));
        messageToSend.setReplyMarkup(new InlineKeyboardMarkup(buttons));
        messageToSend.setText(messageSource.getMessage(MessageCode.CHOOSE_LANG.getCode(), null, locale));
        TelegramApiExecutorWrapper.execute(executor, messageToSend);
    }

    private Optional<SendMessage> changeUserLanguage(Update update, User user, String languageCode) {
        if (languagesConfiguration.containsCode(languageCode)) {
            SendMessage messageToSend = UpdateObjectWrapperUtils.createEmptySendMessageForUserChat(update);
            user = userService.save(user.setLocale(languageCode));
            messageToSend.setText(messageSource.getMessage(MessageCode.LANGUAGE_CHANGED.getCode(), null, new Locale(user.getLocale())));
            return Optional.of(messageToSend);
        }
        return Optional.empty();
    }

    @Override
    public UserCommand commandType() {
        return UserCommand.CHANGE_LANG;
    }
}
