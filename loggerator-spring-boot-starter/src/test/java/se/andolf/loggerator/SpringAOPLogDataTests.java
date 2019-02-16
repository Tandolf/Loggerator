package se.andolf.loggerator;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import se.andolf.loggerator.core.LogTransaction;
import se.andolf.loggerator.core.Loggerator;
import se.andolf.loggerator.models.LogData;
import se.andolf.loggerator.models.LogEvent;
import se.andolf.loggerator.models.SpringAopLogEvent;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.mockito.Mockito.when;

public class SpringAOPLogDataTests {

    private static ObjectMapper objectMapper;
    private Signature signature;
    private ProceedingJoinPoint joinPoint;

    @BeforeClass
    public static void beforeClass() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(NON_NULL);
    }

    @Before
    public void before() {
        signature = Mockito.mock(Signature.class);
        joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);
    }

    @Test
    public void shouldLogJsonWhenExecuting() throws Throwable {

        final TestConsoleAppender appender = getTestAppender();
        final Loggerator loggerator = Loggerator.builder()
                .setAppender(appender)
                .build();

        final String methodName = "someMethodName";
        final Object[] args = {"someArg1", "someArg2"};

        final LogTransaction logTransaction = loggerator.createTransaction();

        when(signature.getName()).thenReturn(methodName);
        when(joinPoint.getArgs()).thenReturn(args);

        final LogEvent logEvent = new SpringAopLogEvent(joinPoint);

        final LogData logData = LogData.builder()
                .name(methodName)
                .args(args)
                .build();

        logTransaction.execute(logEvent);

        Assert.assertEquals(objectMapper.writeValueAsString(logData), appender.logs.get(0));
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

