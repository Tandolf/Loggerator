package se.andolf.loggerator.model;

import lombok.AllArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;

import javax.validation.Valid;

@Valid
public class LogEvent {

    private ProceedingJoinPoint joinPoint;
    private LogData logData;

    public LogEvent(ProceedingJoinPoint joinPoint) {
        this.joinPoint = joinPoint;
    }

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

    public LogData getLogData() {
        return logData;
    }
}
