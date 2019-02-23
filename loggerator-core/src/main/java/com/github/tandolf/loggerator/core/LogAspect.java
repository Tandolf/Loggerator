package com.github.tandolf.loggerator.core;


import com.github.tandolf.loggerator.core.models.SpringMethodLogEvent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;

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
