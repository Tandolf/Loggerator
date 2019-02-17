package se.andolf.loggerator.models;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import se.andolf.loggerator.core.AbstractLogEvent;

public class SpringAopLogEvent extends AbstractLogEvent implements LogEvent {

    private ProceedingJoinPoint joinPoint;

    public SpringAopLogEvent(ProceedingJoinPoint joinPoint) {
        this.joinPoint = joinPoint;
        logDataBuilder.name(getName(joinPoint.getSignature()))
                .args(joinPoint.getArgs());
    }

    @Override
    public Object proceed() throws Throwable {

        try {
            final Object returnValue = joinPoint.proceed();
            logDataBuilder.returnValue(returnValue);
            logDataBuilder.returnStatus(true);
            return returnValue;
        } catch (Throwable e) {
            final String rootCauseStackTrace = ExceptionUtils.getRootCauseMessage(e);
            logDataBuilder.returnValue(rootCauseStackTrace);
            throw e;
        }
    }

    private String getName(Signature signature) {
        final String name = signature.getName();
        final String declaringTypeName = signature.getDeclaringTypeName();
        if(declaringTypeName == null || name == null)
            return null;
        return declaringTypeName + "." + name;
    }
}
