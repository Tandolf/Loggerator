package com.github.tandolf.loggerator.core.models;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;

public class SpringMethodLogEvent implements LogEvent {

    private ProceedingJoinPoint joinPoint;
    private final MethodData.Builder builder;

    public SpringMethodLogEvent(ProceedingJoinPoint joinPoint) {
        this.joinPoint = joinPoint;
        builder = MethodData.builder().name(getName(joinPoint.getSignature()))
                .args(joinPoint.getArgs())
                .thread(Thread.currentThread().getName());
    }

    @Override
    public Object proceed() throws Throwable {

        try {
            final Object returnValue = joinPoint.proceed();
            builder.returnValue(returnValue);
            builder.returnStatus(true);
            return returnValue;
        } catch (Throwable e) {
            builder.returnValue(ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    private String getName(Signature signature) {
        final String name = signature.getName();
        final String declaringTypeName = signature.getDeclaringTypeName();
        if(declaringTypeName == null || name == null)
            return null;
        return declaringTypeName + "." + name;
    }

    @Override
    public LogData getLogData() {
        return builder.build();
    }

    @Override
    public void start(long start) {
        builder.start(start);
    }

    @Override
    public void end(long end) {
        builder.end(end);
    }

    @Override
    public void push(LogData logData) {
        builder.push(logData);
    }
}
