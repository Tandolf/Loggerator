package com.github.tandolf.loggerator.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class Loggerator {

    private ObjectMapper objectMapper;

    private Loggerator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public static LoggeratorBuilder builder() {
        return new Loggerator.LoggeratorBuilder();
    }

    public LogTransaction createTransaction() {
        return new LogTransaction(objectMapper);
    }

    public static class LoggeratorBuilder {

        private final static String LOGBACK = "ch.qos.logback.classic.LoggerContext[default]";
        private Appender<ILoggingEvent> appender;
        private ObjectMapper objectMapper;

        public Loggerator build() {

            final StaticLoggerBinder binder = StaticLoggerBinder.getSingleton();

            if(LOGBACK.equals(binder.getLoggerFactory().toString())) {
                createLogbackLogger();
            }

            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }

            objectMapper.setSerializationInclusion(NON_NULL);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            return new Loggerator(objectMapper);
        }

        private void createLogbackLogger() {
            final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            final Logger logger = (Logger) LoggerFactory.getLogger("Transactions");

            if(appender == null) {
                final ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
                appender.setEncoder(getPatternLayoutEncoder(lc));
                appender.setContext(lc);
                appender.setName("Transactions");
                appender.start();
                logger.addAppender(appender);
            } else {
                logger.addAppender(appender);
            }

            logger.setLevel(Level.INFO);
            logger.setAdditive(false);
        }

        private PatternLayoutEncoder getPatternLayoutEncoder(LoggerContext lc) {
            final PatternLayoutEncoder pattern = new PatternLayoutEncoder();
            pattern.setPattern("%msg%n");
            pattern.setContext(lc);
            pattern.start();
            return pattern;
        }

        public LoggeratorBuilder setAppender(Appender<ILoggingEvent> appender) {
            this.appender = appender;
            return this;
        }

        public LoggeratorBuilder setObjectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }
    }
}
