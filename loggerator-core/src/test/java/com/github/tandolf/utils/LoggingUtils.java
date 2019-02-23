package com.github.tandolf.utils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tandolf.loggerator.core.models.MethodData;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoggingUtils {

    public static TestConsoleAppender getAppender() {

        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        final PatternLayoutEncoder pattern = new PatternLayoutEncoder();
        pattern.setPattern("%msg");
        pattern.setContext(lc);
        pattern.start();

        final TestConsoleAppender testConsoleAppender = new TestConsoleAppender();
        testConsoleAppender.setEncoder(pattern);
        testConsoleAppender.setContext(lc);
        testConsoleAppender.setName("Transactions");
        testConsoleAppender.start();

        return testConsoleAppender;
    }

    public static class TestConsoleAppender extends ConsoleAppender<ILoggingEvent> {

        private final ObjectMapper objectMapper;
        private List<String> logs;

        public TestConsoleAppender() {
            this.logs = new ArrayList<>();
            objectMapper = new ObjectMapper();

        }

        @Override
        protected void append(ILoggingEvent eventObject) {
            logs.add(eventObject.getFormattedMessage());
            super.append(eventObject);
        }

        public MethodData getLatest() {
            try {
                return objectMapper.readValue(logs.get(0), MethodData.class);
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }

        public void clearLogs() {
            logs.clear();
        }
    }
}
