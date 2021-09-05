package com.wgc.shelter.action.factory;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class KeyboardFactory {

    public static InlineKeyboardButton createInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(text);
        inlineKeyboardButton.setCallbackData(callbackData);
        return inlineKeyboardButton;
    }
}

