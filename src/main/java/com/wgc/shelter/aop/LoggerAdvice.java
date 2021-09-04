package com.wgc.shelter.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggerAdvice {

    @Pointcut("@annotation(com.wgc.shelter.aop.annotation.Loggable)")
    public void loggableMethods() {}

    @Before("loggableMethods()")
    public void logTelegramUpdate(JoinPoint point) {
        log.info("Update received {}", point.getArgs()[0]);
    }
}
