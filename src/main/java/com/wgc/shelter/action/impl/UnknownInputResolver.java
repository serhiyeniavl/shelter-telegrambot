package com.wgc.shelter.action.impl;

import com.wgc.shelter.action.response.UserResponseResolver;
import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Optional;

public class UnknownInputResolver implements UserResponseResolver {
    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> Optional<Method> doResolve(User user, Update update) {
        return Optional.empty();
    }

    @Override
    public UserActionState userState() {
        return null;
    }
}
