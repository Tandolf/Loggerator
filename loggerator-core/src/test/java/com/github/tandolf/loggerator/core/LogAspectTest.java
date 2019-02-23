package com.github.tandolf.loggerator.core;

import com.github.tandolf.loggerator.core.models.MethodData;
import com.github.tandolf.loggerator.core.models.annotations.LogThis;
import com.github.tandolf.utils.LoggingUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.jupiter.api.Assertions.*;

class LogAspectTest {

    private static Loggerator loggerator;
    private static LoggingUtils.TestConsoleAppender appender;
    private static Stub stubProxy;

    @BeforeAll
    static void beforeClass() {
        appender = LoggingUtils.getAppender();
        loggerator = Loggerator.builder()
                .setAppender(appender)
                .build();
    }

    @BeforeEach
    void each() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new Stub());
        factory.addAspect(new LogAspect(loggerator));
        stubProxy = factory.getProxy();
        stubProxy.setSelf(stubProxy);

        appender.clearLogs();
    }

    @Test
    @DisplayName("Log the fully qualified name of the invoked method")
    void shouldLogCorrectInvokedMethod() {
        stubProxy.divide(2, 1);

        final MethodData log = appender.getLatest();
        assertEquals("com.github.tandolf.loggerator.core.LogAspectTest$Stub.divide", log.getName());
    }

    @Test
    @DisplayName("Log correct method arguments")
    void shouldLogCorrectArgs() {
        final int arg1 = 2;
        final int arg2 = 1;
        stubProxy.divide(arg1, arg2);

        final MethodData log = appender.getLatest();
        assertEquals(arg1, log.getArgs()[0]);
        assertEquals(arg2, log.getArgs()[1]);
    }

    @Test
    @DisplayName("Log timestamps, start, end and duration")
    void shouldLogTimestamps() {
        stubProxy.divide(2,1);

        final MethodData log = appender.getLatest();
        assertNotNull(log.getStart());
        assertNotNull(log.getEnd());
        assertNotNull(log.getDuration());
    }

    @Test
    @DisplayName("Log the invoked methods return value")
    void shouldLogReturnValue() {
        final int result = stubProxy.divide(1, 2);
        final MethodData latest = appender.getLatest();
        assertEquals(result, latest.getReturnValue());
    }

    @Test
    @DisplayName("Log the return status boolean to true if method returns successfully")
    void shouldLogReturnStatusTrue() {
        stubProxy.divide(2, 1);
        final MethodData latest = appender.getLatest();
        assertTrue(latest.isReturnStatus());
    }

    @Test
    @DisplayName("Log an exception as a return value, if method ends bad")
    void shouldLogExceptionAsReturnValue() {
        final ArithmeticException exception = assertThrows(ArithmeticException.class, () -> stubProxy.divide(0, 0));
        final MethodData latest = appender.getLatest();
        final String s = latest.getReturnValue().toString();
        final String message = exception.getMessage();
        assertTrue(s.contains(message));
        assertFalse(latest.isReturnStatus());
    }

    @Test
    @DisplayName("Log the running threads name")
    void shouldLogThreadName() {
        stubProxy.divide(2, 1);
        final MethodData latest = appender.getLatest();
        assertEquals(Thread.currentThread().getName(), latest.getThread());
    }

    @Test
    @DisplayName("Should produce a log with a nested log inside")
    void shouldLogNested() {
        stubProxy.nested(2, 1);
        final MethodData latest = appender.getLatest();
        assertEquals(1, latest.getMethods().size());
    }

    public class Stub {

        private Stub self;

        void setSelf(Stub self) {
            this.self = self;
        }

        @LogThis
        int divide(int x, int y) {
            return x / y;
        }

        @LogThis
        int nested(int x, int y) {
            return self.divide(x, y);
        }
    }
}
