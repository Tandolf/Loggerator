package com.github.tandolf.loggerator.core;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tandolf.loggerator.models.LogData;
import com.github.tandolf.loggerator.models.LogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

public class LogTransaction {

    private final static Logger logger = LoggerFactory.getLogger("Transactions");

    private static ThreadLocal<Deque<LogEvent>> threadLocal = new ThreadLocal<>();
    private final ObjectMapper objectMapper;
    private final Deque<LogEvent> logStack;

    public LogTransaction(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        logStack = Optional.ofNullable(threadLocal.get())
            .orElseGet(() -> {
                threadLocal.set(new ArrayDeque<>());
                return threadLocal.get();
            });
    }

    public Object execute(LogEvent logEvent) throws Throwable {

        logStack.push(logEvent);

        logEvent.start(System.currentTimeMillis());
        try {
            return logEvent.proceed();
        } finally {
            logEvent.end(System.currentTimeMillis());
            final LogEvent current = logStack.pop();
            Optional.ofNullable(logStack.peekFirst()).ifPresentOrElse(first -> first.push(current.getLogData()), () -> logger.info(asString(logEvent.getLogData())));
        }
    }

    private String asString(LogData logData) {
        try {
            return objectMapper.writeValueAsString(logData);
        } catch (JsonProcessingException e) {
            throw new InternalError(e);
        }
    }
}
