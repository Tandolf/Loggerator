package se.andolf.loggerator.core;

import se.andolf.loggerator.models.LogBuilder;
import se.andolf.loggerator.models.MethodData;
import se.andolf.loggerator.models.LogEvent;

public abstract class AbstractLogEvent implements LogEvent {

    protected final LogBuilder builder;

    public AbstractLogEvent(LogBuilder builder) {
        this.builder = builder;
    }

    public abstract Object proceed() throws Throwable;


}
