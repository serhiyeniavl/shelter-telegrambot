package com.wgc.shelter.action;

import com.wgc.shelter.action.annotation.Handler;
import com.wgc.shelter.action.response.UserResponseResolver;
import com.wgc.shelter.action.utils.TelegramApiExecutorWrapper;
import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import com.wgc.shelter.service.UserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Handler
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserRawInputHandler {

    Map<UserActionState, UserResponseResolver> responseResolvers;
    UserService userService;

    @Transactional
    public void handleInput(TelegramLongPollingBot executor, Update update) {
        User user = userService.retrieveExistingUser(UpdateObjectWrapperUtils.getUserTelegramId(update));
        responseResolvers.get(user.getState()).doResolve(user, update)
                .ifPresent(message -> TelegramApiExecutorWrapper.execute(executor, message));
    }
}
