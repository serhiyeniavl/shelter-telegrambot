package com.wgc.shelter.action;

import com.wgc.shelter.action.factory.KeyboardFactory;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.common.BaseSpringBootTestClass;
import com.wgc.shelter.common.UpdateBotMessageSetup;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Locale;

import static com.wgc.shelter.common.UpdateBotMessageSetup.buildUpdateObject;

class ChangeLangCommandActionTest extends BaseSpringBootTestClass {

    @Test
    @DisplayName("Change language command")
    void changeLanguageTest() throws TelegramApiException {
        long telegramUserId = 100;

        User initialUser = saveUser(telegramUserId, UserActionState.NEW_USER, EN_US.toString());

        InlineKeyboardButton russian = KeyboardFactory.createInlineKeyboardButton("Русский\uD83C\uDDF7\uD83C\uDDFA", UserCommand.CHANGE_LANG.getCommand() + " ru");
        InlineKeyboardButton english = KeyboardFactory.createInlineKeyboardButton("English\uD83C\uDDFA\uD83C\uDDF8", UserCommand.CHANGE_LANG.getCommand() + " en");

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.CHANGE_LANG.getCommand(), messageSource.getMessage(MessageCode.CHOOSE_LANG.getCode(), null, EN_US),
                new InlineKeyboardMarkup(List.of(List.of(russian), List.of(english))));

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId).get();
        Assertions.assertEquals(initialUser, actualUser);
    }

    @Test
    @DisplayName("Change language from user's callback")
    void changeLanguageCallbackDataTest() throws TelegramApiException {
        long telegramUserId = 100;

        User initialUser = saveUser(telegramUserId, UserActionState.NEW_USER, EN_US.toString());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.CHANGE_LANG.getCommand(), messageSource.getMessage(MessageCode.LANGUAGE_CHANGED.getCode(), null,
                        Locale.forLanguageTag("ru")), null);

        Message message = new Message();
        message.setText(messageSource.getMessage(MessageCode.CHOOSE_LANG.getCode(), null, EN_US));
        botMessageSetup.update().setMessage(null);
        CallbackQuery callbackQuery = new CallbackQuery("callbackQuery", botMessageSetup.user(), message,
                null, UserCommand.CHANGE_LANG.getCommand() + " ru", null, null);
        botMessageSetup.update().setCallbackQuery(callbackQuery);

        Mockito.doReturn(new Message()).when(telegramLongPollingController).execute(botMessageSetup.messageToSend());

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId).get();
        Assertions.assertEquals(initialUser.setLocale("ru"), actualUser);
    }

    @Test
    @DisplayName("Change language from user's callback - invalid language was received")
    void invalidLanguageCallbackDataTest() throws TelegramApiException {
        long telegramUserId = 100;

        User initialUser = saveUser(telegramUserId, UserActionState.NEW_USER, EN_US.toString());

        UpdateBotMessageSetup botMessageSetup = buildUpdateObject(telegramUserId, "user", EN_US.toString(),
                UserCommand.CHANGE_LANG.getCommand(), messageSource.getMessage(MessageCode.LANGUAGE_CHANGED.getCode(), null,
                        EN_US), null);

        Message message = new Message();
        message.setText(messageSource.getMessage(MessageCode.CHOOSE_LANG.getCode(), null, EN_US));
        botMessageSetup.update().setMessage(null);
        CallbackQuery callbackQuery = new CallbackQuery("callbackQuery", botMessageSetup.user(), message,
                null, UserCommand.CHANGE_LANG.getCommand() + " fdas", null, null);
        botMessageSetup.update().setCallbackQuery(callbackQuery);

        Mockito.verifyNoInteractions(telegramLongPollingController);

        Assertions.assertDoesNotThrow(() -> telegramLongPollingController.onUpdateReceived(botMessageSetup.update()));

        User actualUser = userRepository.findByTelegramUserId(telegramUserId).get();
        Assertions.assertEquals(initialUser, actualUser);
    }

}
