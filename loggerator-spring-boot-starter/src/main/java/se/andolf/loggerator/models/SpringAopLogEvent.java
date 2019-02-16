package se.andolf.loggerator.models;

import org.aspectj.lang.ProceedingJoinPoint;

public class SpringAopLogEvent implements LogEvent {

    private ProceedingJoinPoint joinPoint;
    private LogData.LogDataBuilder logDataBuilder;
    private LogData logData;

    public SpringAopLogEvent(ProceedingJoinPoint joinPoint) {
        this.joinPoint = joinPoint;
        this.logDataBuilder = LogData.builder();
    }

    @Override
    public Object proceed() {
        logDataBuilder.name(joinPoint.getSignature().getName())
                .args(joinPoint.getArgs());
        try {
            final Object returnValues = joinPoint.proceed();
            logData = logDataBuilder.build();
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
