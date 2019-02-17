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
        final Object[] firstArgs = {"firstArg1", "firstArg2"};
        final Object[] secondArgs = {"secondArg1", "secondArg2"};
        final Object[] thirdArgs = {"thirdArg1", "thirdArg2"};

        final String packageName = this.getClass().getPackageName();

        when(signature.getName())
                .thenReturn(methodName)
                .thenReturn(otherMethodName)
                .thenReturn(someOtherMethodName);
        when(signature.getDeclaringTypeName())
                .thenReturn(packageName);
        when(joinPoint.getArgs())
                .thenReturn(firstArgs)
                .thenReturn(secondArgs)
                .thenReturn(thirdArgs);

        final LogEvent firstMethod = new SpringAopLogEvent(joinPoint);
        final LogEvent secondMethod = new SpringAopLogEvent(joinPoint);
        final LogEvent thirdMethod = new SpringAopLogEvent(joinPoint);

        final LogTransaction logTransaction = loggerator.createTransaction();

        when(joinPoint.proceed())
                .then(invocationOnMock -> logTransaction.execute(secondMethod))
                .then(invocationOnMock -> logTransaction.execute(thirdMethod))
                .thenReturn("someReturnValue");

        final LogData method3 = LogData.builder()
                .name(packageName + "." + someOtherMethodName)
                .args(thirdArgs)
                .build();

        final LogData method2 = LogData.builder()
                .name(packageName + "." + otherMethodName)
                .args(secondArgs)
                .build();

        final LogData method1 = LogData.builder()
                .name(packageName + "." + methodName)
                .args(firstArgs)
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

