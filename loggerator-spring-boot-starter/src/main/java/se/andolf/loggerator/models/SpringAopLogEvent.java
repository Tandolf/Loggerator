package se.andolf.loggerator.models;

import org.aspectj.lang.ProceedingJoinPoint;

import javax.validation.Valid;

@Valid
public class SpringAopLogEvent implements LogEvent {

    private ProceedingJoinPoint joinPoint;
    private LogData logData;

    public SpringAopLogEvent(ProceedingJoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }

    @Override
    public Object proceed() {
        final LogData.LogDataBuilder dataBuilder = LogData.builder().name(joinPoint.getSignature().getName());
        try {
            final Object returnValues = joinPoint.proceed();
            logData = dataBuilder.build();
            return returnValues;
        } catch (Throwable throwable) {
            //TODO: handle exception
            throw new InternalError("Could not proceed correctly");
        }
    }

    @Override
    public LogData getLogData() {
        return logData;
    }
}
