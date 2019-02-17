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

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class SpringAOPLogDataTests {

    private static ObjectMapper objectMapper;
    private static Loggerator loggerator;
    private static TestConsoleAppender appender;
    private Signature signature;
    private ProceedingJoinPoint joinPoint;

    @BeforeClass
    public static void beforeClass() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(NON_NULL);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        appender = getTestAppender();

        loggerator = Loggerator.builder()
                .setAppender(appender)
                .setObjectMapper(objectMapper)
                .build();
    }

    @Before
    public void before() {
        signature = Mockito.mock(Signature.class);
        joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        appender.logs.clear();
    }

    @Test
    public void shouldLogJsonWhenExecuting() throws Throwable {

        final String firstMethodName = "first";
        final String secondMethodName = "second";
        final String thirdMethodName = "third";
        final Object[] firstArgs = {"firstArg1", "firstArg2"};
        final Object[] secondArgs = {"secondArg1", "secondArg2"};
        final Object[] thirdArgs = {"thirdArg1", "thirdArg2"};
        final String packageName = this.getClass().getPackageName();

        when(signature.getName())
                .thenReturn(firstMethodName)
                .thenReturn(secondMethodName)
                .thenReturn(thirdMethodName);
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

        logTransaction.execute(firstMethod);

        final LogData actual = objectMapper.readValue(appender.logs.get(0), LogData.class);

        assertEquals(2, actual.getMethods().size());
        assertEquals(packageName + "." + firstMethodName, actual.getName());
        assertArrayEquals(firstArgs, actual.getArgs());
        assertEquals(packageName + "." + secondMethodName, actual.getMethods().peekFirst().getName());
        assertArrayEquals(secondArgs, actual.getMethods().pollFirst().getArgs());
        assertEquals(packageName + "." + thirdMethodName, actual.getMethods().peekFirst().getName());
        assertArrayEquals(thirdArgs, actual.getMethods().pollFirst().getArgs());

    }

    @Test
    public void shouldLogDataContainingTimeStamps() throws Throwable {
        final LogTransaction transaction = loggerator.createTransaction();
        transaction.execute(new SpringAopLogEvent(joinPoint));

        final LogData actual = objectMapper.readValue(appender.logs.get(0), LogData.class);

        assertNotNull(actual.getStart());
        assertNotNull(actual.getEnd());
        assertNotNull(actual.getDuration());
    }

    @Test
    public void shouldLogReturnValueInLogData() throws Throwable {
        final LogTransaction transaction = loggerator.createTransaction();

        when(joinPoint.proceed()).thenReturn("someReturnValue");

        transaction.execute(new SpringAopLogEvent(joinPoint));

        final LogData actual = objectMapper.readValue(appender.logs.get(0), LogData.class);

        assertEquals("someReturnValue", actual.getReturnValue());
    }

    @Test
    public void shouldLogExceptionIfProceedThrowsException() throws Throwable {
        final LogTransaction transaction = loggerator.createTransaction();

        when(joinPoint.proceed())
                .thenThrow(new AccessDeniedException("Some exception")
                        .initCause(new NullPointerException("There was a random null pointer")));

        try {
            transaction.execute(new SpringAopLogEvent(joinPoint));
        } catch (Throwable throwable) {
            final LogData actual = objectMapper.readValue(appender.logs.get(0), LogData.class);
            assertEquals("NullPointerException: There was a random null pointer", actual.getReturnValue());
            assertFalse(actual.isReturnStatus());
        }
    }

    private static TestConsoleAppender getTestAppender() {

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

    private static class TestConsoleAppender extends ConsoleAppender<ILoggingEvent> {

        private List<String> logs = new ArrayList<>();

	    @Override
        protected void append(ILoggingEvent eventObject) {
	        logs.add(eventObject.getFormattedMessage());
            super.append(eventObject);
        }
    }

}

