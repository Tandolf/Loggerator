package se.andolf.loggerator;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import se.andolf.loggerator.model.LogEvent;
import se.andolf.loggerator.model.LogTransaction;
import se.andolf.loggerator.model.Loggerator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoggeratorApplicationTests {

    private Signature signature;
    private ProceedingJoinPoint joinPoint;

    @Before
    public void init() {
        signature = mock(Signature.class);
        joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);
    }

	@Test
    public void shouldExecuteEvent() throws Throwable {

        final Loggerator loggerator = Loggerator.builder()
                .build();

        final LogTransaction logTransaction = loggerator.createTransaction();

        final String expected = "someReturnValue";

        final Signature signature = mock(Signature.class);
        when(signature.getName()).thenReturn(expected);

        final ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getSignature().getName()).thenReturn("someMethodName");
        when(joinPoint.proceed()).thenReturn(expected);


        final LogEvent logEvent = new LogEvent(joinPoint);

        assertEquals(expected, logTransaction.execute(logEvent));
    }

    @Test
    public void shouldLogJsonWhenExecuting() throws Throwable {

        final TestConsoleAppender appender = getTestAppender();
        final Loggerator loggerator = Loggerator.builder()
                .setAppender(appender)
                .build();

        final LogTransaction logTransaction = loggerator.createTransaction();

        final String expected = "someReturnValue";

        when(signature.getName()).thenReturn(expected);
        when(joinPoint.proceed()).thenReturn(expected);

        final LogEvent logEvent = new LogEvent(joinPoint);

        logTransaction.execute(logEvent);

        assertEquals("{\"name\":\"someReturnValue\",\"args\":null,\"methods\":[]}", appender.logs.get(0));
    }

    private TestConsoleAppender getTestAppender() {

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

    private class TestConsoleAppender extends ConsoleAppender<ILoggingEvent> {

        private List<String> logs = new ArrayList<>();

	    @Override
        protected void append(ILoggingEvent eventObject) {
	        logs.add(eventObject.getFormattedMessage());
            super.append(eventObject);
        }
    }

}

