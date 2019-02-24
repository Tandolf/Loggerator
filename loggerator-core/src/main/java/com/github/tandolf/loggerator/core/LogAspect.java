package com.github.tandolf.loggerator.core;


import com.github.tandolf.loggerator.core.models.SpringMethodLogEvent;
import com.github.tandolf.loggerator.core.models.annotations.LogThis;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Method;

@Aspect
public class LogAspect {

    private final Loggerator loggerator;

    @Autowired
    public LogAspect(Loggerator loggerator) {
        this.loggerator = loggerator;
    }

    @Around("@annotation(com.github.tandolf.loggerator.core.models.annotations.LogThis)")
    public Object logSomething(ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();

        final LogThis logThis = method.getAnnotation(LogThis.class);
        final boolean isTimed = logThis.timed();

        final LogTransaction transaction = loggerator.createTransaction();
        final SpringMethodLogEvent logEvent = new SpringMethodLogEvent(joinPoint, isTimed);
        return transaction.execute(logEvent);
    }
}
