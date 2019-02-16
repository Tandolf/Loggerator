package se.andolf.loggerator;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import se.andolf.loggerator.core.LogTransaction;
import se.andolf.loggerator.core.Loggerator;
import se.andolf.loggerator.models.SpringAopLogEvent;

import java.util.ArrayList;
import java.util.List;

public class LoggeratorApplicationTests {

    private Signature signature;
    private ProceedingJoinPoint joinPoint;

    @Before
    public void init() {
        signature = Mockito.mock(Signature.class);
        joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
    }

	@Test
    public void shouldExecuteEvent() throws Throwable {

        final Loggerator loggerator = Loggerator.builder()
                .build();

        final LogTransaction logTransaction = loggerator.createTransaction();

        final String expected = "someReturnValue";

        final Signature signature = Mockito.mock(Signature.class);
        Mockito.when(signature.getName()).thenReturn(expected);

        final ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        Mockito.when(joinPoint.getSignature()).thenReturn(signature);
        Mockito.when(joinPoint.getSignature().getName()).thenReturn("someMethodName");
        Mockito.when(joinPoint.proceed()).thenReturn(expected);


        final SpringAopLogEvent logEvent = new SpringAopLogEvent(joinPoint);

        Assert.assertEquals(expected, logTransaction.execute(logEvent));
    }

    @Test
    public void shouldLogJsonWhenExecuting() throws Throwable {

        final TestConsoleAppender appender = getTestAppender();
        final Loggerator loggerator = Loggerator.builder()
                .setAppender(appender)
                .build();

        final LogTransaction logTransaction = loggerator.createTransaction();

        final String expected = "someReturnValue";

        Mockito.when(signature.getName()).thenReturn(expected);
        Mockito.when(joinPoint.proceed()).thenReturn(expected);

        final SpringAopLogEvent logEvent = new SpringAopLogEvent(joinPoint);

        logTransaction.execute(logEvent);

        Assert.assertEquals("{\"name\":\"someReturnValue\",\"args\":null,\"methods\":[]}", appender.logs.get(0));
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

