package com.github.tandolf.loggerator.core;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tandolf.loggerator.core.models.LogData;
import com.github.tandolf.loggerator.core.models.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class LogTransaction {

    private static final Logger logger = LoggerFactory.getLogger("Transactions");

    private static ThreadLocal<Deque<LogEvent>> threadLocal = new ThreadLocal<>();
    private final ObjectMapper objectMapper;
    private final Deque<LogEvent> logStack;
    private final boolean timeTransactions;

    public LogTransaction(ObjectMapper objectMapper, boolean timeTransactions) {
        this.objectMapper = objectMapper;

        logStack = Optional.ofNullable(threadLocal.get())
            .orElseGet(() -> {
                threadLocal.set(new ArrayDeque<>());
                return threadLocal.get();
            });
        this.timeTransactions = timeTransactions;
    }

    public Object execute(LogEvent logEvent) throws Throwable {

        logStack.push(logEvent);

        try {
            if(logEvent.isTimed() && timeTransactions)
                return timedInvoke(logEvent);
            else
                return invoke(logEvent);
        } finally {
            final LogEvent current = logStack.pop();
            Optional.ofNullable(logStack.peekFirst()).ifPresentOrElse(first -> first.push(current.getLogData()), () -> logger.info(asString(logEvent.getLogData())));
        }
    }

    private Object timedInvoke(LogEvent logEvent) throws Throwable {
        logEvent.start(System.currentTimeMillis());
        try {
            return logEvent.proceed();
        } finally {
            logEvent.end(System.currentTimeMillis());
        }
    }

    private Object invoke(LogEvent logEvent) throws Throwable {
        return logEvent.proceed();
    }

    private String asString(LogData logData) {
        try {
            return objectMapper.writeValueAsString(logData);
        } catch (JsonProcessingException e) {
            throw new InternalError(e);
        }
    }
}
