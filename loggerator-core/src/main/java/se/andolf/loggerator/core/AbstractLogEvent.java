package se.andolf.loggerator.core;

import se.andolf.loggerator.models.LogData;
import se.andolf.loggerator.models.LogEvent;

public abstract class AbstractLogEvent implements LogEvent {

    protected LogData.LogDataBuilder logDataBuilder = LogData.builder();

    public abstract Object proceed();

    @Override
    public LogData getLogData() {
        return logDataBuilder.build();
    }

    @Override
    public LogData.LogDataBuilder mutate() {
        return logDataBuilder;
    }
}
