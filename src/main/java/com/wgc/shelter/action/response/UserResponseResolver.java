package com.wgc.shelter.action.response;

import com.wgc.shelter.model.User;
import com.wgc.shelter.model.UserActionState;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.Serializable;
import java.util.Optional;

public interface UserResponseResolver {

    @Transactional(propagation = Propagation.SUPPORTS)
    <T extends Serializable, Method extends BotApiMethod<T>> Optional<Method> doResolve(User user, Update update);

    UserActionState userState();
}
