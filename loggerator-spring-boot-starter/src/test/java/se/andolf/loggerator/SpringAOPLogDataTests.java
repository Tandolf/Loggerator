package se.andolf.loggerator;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
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
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SpringAOPLogDataTests {

    private static ObjectMapper objectMapper;
    private Signature signature;
    private ProceedingJoinPoint joinPoint;

    @BeforeClass
    public static void beforeClass() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
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
                .setObjectMapper(objectMapper)
                .build();

        final String methodName = "first";
        final String otherMethodName = "second";
        final String someOtherMethodName = "third";
        final Object[] args = {"someArg1", "someArg2"};
        final Object[] otherArgs = {"someOtherArg1", "someOtherArg2"};

        when(signature.getName())
                .thenReturn(methodName)
                .thenReturn(otherMethodName)
                .thenReturn(someOtherMethodName);
        when(joinPoint.getArgs())
                .thenReturn(args)
                .thenReturn(otherArgs);

        final LogEvent firstMethod = new SpringAopLogEvent(joinPoint);
        final LogEvent secondMethod = new SpringAopLogEvent(joinPoint);
        final LogEvent thirdMethod = new SpringAopLogEvent(joinPoint);

        final LogTransaction logTransaction = loggerator.createTransaction();

        when(joinPoint.proceed())
                .then(invocationOnMock -> logTransaction.execute(secondMethod))
                .then(invocationOnMock -> logTransaction.execute(thirdMethod))
                .thenReturn("someReturnValue");

        final LogData method3 = LogData.builder()
                .name(someOtherMethodName)
                .args(otherArgs)
                .build();

        final LogData method2 = LogData.builder()
                .name(otherMethodName)
                .args(otherArgs)
                .build();

        final LogData method1 = LogData.builder()
                .name(methodName)
                .args(args)
                .push(method3).push(method2)
                .build();

        logTransaction.execute(firstMethod);

        assertEquals(objectMapper.writeValueAsString(method1), appender.logs.get(0));
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

