package se.andolf.loggerator.core;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.andolf.loggerator.models.LogData;
import se.andolf.loggerator.models.LogEvent;

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

    public Object execute(LogEvent logEvent) {

        logStack.push(logEvent);

        final Object proceed = logEvent.proceed();
        final LogEvent current = logStack.pop();

        Optional.ofNullable(logStack.peekLast()).ifPresentOrElse(first -> first.mutate().push(current.getLogData()), () -> logger.info(asString(logEvent.getLogData())));

        return proceed;
    }

    private String asString(LogData logData) {
        try {
            return objectMapper.writeValueAsString(logData);
        } catch (JsonProcessingException e) {
            throw new InternalError();
        }
    }
}
