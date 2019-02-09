package se.andolf.loggerator.aspects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.andolf.loggerator.model.LogData;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;

@Aspect
@Component
@Slf4j
public class LogAspect {

    private static ThreadLocal<Deque<LogData.LogDataBuilder>> logStack = new ThreadLocal<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Around("@annotation(se.andolf.loggerator.model.LogThis)")
    public Object logSomething(ProceedingJoinPoint joinPoint) throws Throwable {

        /*
            Get the stack from current thread local, if no stack is available we create and set one.
        */
        final Deque<LogData.LogDataBuilder> stack = Optional.ofNullable(logStack.get())
                .orElseGet(() -> {
            logStack.set(new ArrayDeque<>());
            return logStack.get();
        });

        /*
            We get some information before we are about to execute the method we have annotated for logging.
            Info is name of the executing method, and the input arguments.
            With this info we start building a logging object and push this to the stack.
        */
        final String methodName = joinPoint.getSignature().getName();
        final Object[] args = joinPoint.getArgs();

        final LogData.LogDataBuilder logBuilder = LogData.builder()
                .name(methodName)
                .args(args);

        stack.push(logBuilder);

        /*
            We execute the anotated method.
        */
        final Object methodReturn = joinPoint.proceed();


        /*
            We pop the top of the stack and poll the first (as in removing the first if is present otherwise we get null from the stack).
            If pollFirst returns a LogData object we know we are not the first called method. So we attach this LogData to the first and push back the first.
            On the other hand, if we pop the latest, and then poll the first and see null in the poll, we know we are the first in the call stack so we log everything.
        */
        final LogData.LogDataBuilder current = stack.pop();

        Optional.ofNullable(stack.pollFirst()).ifPresentOrElse(first -> {
            first.method(current.build());
            stack.push(first);
        }, () -> log.info(asString(logBuilder.build())));

        return methodReturn;
    }

    private String asString(LogData logData) {
        try {
            return objectMapper.writeValueAsString(logData);
        } catch (JsonProcessingException e) {
            throw new InternalError();
        }

    }
}
