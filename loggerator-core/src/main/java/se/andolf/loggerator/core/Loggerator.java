package se.andolf.loggerator.core;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import config.LoggerConfiguration;

public class Loggerator {

    final static String LOGBACK = "ch.qos.logback.classic.LoggerContext[default]";
    private static Logger logger;
    private static ObjectMapper objectMapper;

    private Loggerator() {}


    public static LoggeratorBuilder builder() {
        return new Loggerator.LoggeratorBuilder();
    }

    public LogTransaction createTransaction() {
        return new LogTransaction(objectMapper);
    }

    public static class LoggeratorBuilder {

        private Appender<ILoggingEvent> appender;

        public Loggerator build() {

            final StaticLoggerBinder binder = StaticLoggerBinder.getSingleton();

            if(LOGBACK.equals(binder.getLoggerFactory().toString())) {
                createLogbackLogger();
            }

            objectMapper = new ObjectMapper();

            return new Loggerator();
        }

        private void createLogbackLogger() {
            final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

            final PatternLayoutEncoder pattern = new PatternLayoutEncoder();
            pattern.setPattern("%msg");
            pattern.setContext(lc);
            pattern.start();

            logger = (Logger) LoggerFactory.getLogger("Transactions");

            if(appender == null) {
                final ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
                appender.setEncoder(pattern);
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

        public LoggerConfiguration.LoggerConfigurationBuilder logger() {
            return LoggerConfiguration.builder();
        }

        public LoggeratorBuilder setAppender(Appender<ILoggingEvent> appender) {
            this.appender = appender;
            return this;
        }
    }
}
