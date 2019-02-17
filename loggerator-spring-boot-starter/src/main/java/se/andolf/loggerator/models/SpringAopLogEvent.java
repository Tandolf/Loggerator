package se.andolf.loggerator.models;

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
    public Object proceed() {
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throw new InternalError("Could not proceed correctly");
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
