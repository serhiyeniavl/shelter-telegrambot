package com.wgc.shelter.action.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@Slf4j
public class TelegramApiExecutorWrapper {

    public static <T extends Serializable, Method extends BotApiMethod<T>> void execute(TelegramLongPollingBot executor,
                                                                                        Method messageToSend) {
        try {
            executor.execute(messageToSend);
        } catch (TelegramApiException e) {
            log.error("Error occurred during sent message to telegram bot, message = {}, ex = {}", messageToSend, e);
            throw new RuntimeException(e);
        }
    }
}
