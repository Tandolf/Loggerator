package se.andolf.loggerator.models;

import org.aspectj.lang.ProceedingJoinPoint;
import se.andolf.loggerator.core.AbstractLogEvent;

public class SpringAopLogEvent extends AbstractLogEvent implements LogEvent {

    private ProceedingJoinPoint joinPoint;

    public SpringAopLogEvent(ProceedingJoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }

    @Override
    public Object proceed() {
        logDataBuilder.name(joinPoint.getSignature().getName())
                .args(joinPoint.getArgs());
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throw new InternalError("Could not proceed correctly");
        }
    }
}
