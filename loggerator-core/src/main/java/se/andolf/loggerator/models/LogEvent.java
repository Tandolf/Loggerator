package se.andolf.loggerator.models;

public interface LogEvent {
    Object proceed() throws Throwable;

    LogData getLogData();

    void start(long currentTimeMillis);

    void end(long currentTimeMillis);

    void push(LogData logData);
}
