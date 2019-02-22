package com.github.tandolf.loggerator.core.models;

public interface LogEvent {
    Object proceed() throws Throwable;

    LogData getLogData();

    void start(long currentTimeMillis);

    void end(long currentTimeMillis);

    void push(LogData logData);
}
