package com.wgc.shelter.action.utils;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;

@UtilityClass
public class UpdateObjectWrapperUtils {


    public @NonNull Long getUserTelegramId(Update update) {
        return update.getMessage().getFrom().getId();
    }

    public static String getChaId(Update update) {
        return String.valueOf(update.getMessage().getChatId());
    }
}
