package com.wgc.shelter.aop;

import com.wgc.shelter.action.utils.UpdateObjectWrapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Aspect
@Slf4j
public class LoggerAdvice {

    @Before("@annotation(com.wgc.shelter.aop.annotation.Loggable) && execution(* *..*(org.telegram.telegrambots.meta.api.objects.Update,..))")
    public void logTelegramUpdate(JoinPoint point) {
        log.info("Update received \n{}", UpdateObjectWrapperUtils.toString((Update) point.getArgs()[0]));
    }
}
