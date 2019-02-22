package com.github.tandolf.springframework.boot.autoconfigure;


import com.github.tandolf.loggerator.core.LogTransaction;
import com.github.tandolf.loggerator.core.Loggerator;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
public class LogAspect {

    private final Loggerator loggerator;

    @Autowired
    public LogAspect(Loggerator loggerator) {
        this.loggerator = loggerator;
    }

    @Around("@annotation(com.github.tandolf.loggerator.core.models.annotations.LogThis)")
    public Object logSomething(ProceedingJoinPoint joinPoint) throws Throwable {
        final LogTransaction transaction = loggerator.createTransaction();
        final SpringMethodLogEvent logEvent = new SpringMethodLogEvent(joinPoint);
        return transaction.execute(logEvent);
    }
}
