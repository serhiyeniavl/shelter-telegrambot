package com.wgc.shelter.action.factory;

import com.google.common.collect.Lists;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;

public class KeyboardFactory {

    public static InlineKeyboardButton createInlineKeyboardButton(String text) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(text);
        inlineKeyboardButton.setCallbackData(text);
        return inlineKeyboardButton;
    }

    public static List<List<InlineKeyboardButton>> createInlineKeyboardButtonRow(InlineKeyboardButton button, InlineKeyboardButton... buttons) {
        return Collections.singletonList(Lists.asList(button, buttons));
    }
}
