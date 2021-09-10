package com.wgc.shelter.action;

import com.wgc.shelter.action.factory.KeyboardFactory;
import com.wgc.shelter.action.message.MessageCode;
import com.wgc.shelter.action.model.UserCommand;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import com.wgc.shelter.model.User;
import com.wgc.shelter.service.RoomService;
import com.wgc.shelter.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Locale;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class AbstractCommandAction implements CommandAction {

    UserService userService;
    RoomService roomService;

    MessageSource messageSource;

    protected SendMessage createEmptySendMessageForUserChat(Update update) {
        return SendMessage.builder()
                .chatId(UpdateObjectWrapperUtils.getChaId(update))
                .text("")
                .build();
    }

    protected Long getUserTelegramId(Update update) {
        return UpdateObjectWrapperUtils.getUserTelegramId(update);
    }

    @Transactional
    protected User getExistingUser(Update update) {
        return userService.retrieveExistingUser(getUserTelegramId(update));
    }

    protected InlineKeyboardButton createLeaveOrDestroyButton(User user) {
        String command = roomService.findRoom(user.getTelegramUserId())
                .map(room -> UserCommand.DESTROY.getCommand().concat(" " + user.getTelegramUserId()))
                .orElseGet(UserCommand.LEAVE::getCommand);

        return KeyboardFactory.createInlineKeyboardButton(
                messageSource.getMessage(MessageCode.ANSWER_YES.getCode(), null, new Locale(user.getLocale())), command);
    }

}
