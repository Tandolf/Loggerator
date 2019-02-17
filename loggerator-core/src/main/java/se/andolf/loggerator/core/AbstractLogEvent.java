package se.andolf.loggerator.core;

import se.andolf.loggerator.models.LogData;
import se.andolf.loggerator.models.LogEvent;

public abstract class AbstractLogEvent implements LogEvent {

    protected LogData.LogDataBuilder logDataBuilder = LogData.builder();

    public abstract Object proceed() throws Throwable;

    @Override
    public LogData getLogData() {
        return logDataBuilder.build();
    }

    @Override
    public void start(long start) {
        logDataBuilder.start(start);
    }

    @Override
    public void end(long end) {
        logDataBuilder.end(end);
    }

    @Override
    public void push(LogData logData) {
        logDataBuilder.push(logData);
    }
}
