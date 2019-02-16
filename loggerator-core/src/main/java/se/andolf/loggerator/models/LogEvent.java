package se.andolf.loggerator.models;

public interface LogEvent {
    Object proceed();

    LogData getLogData();

    LogData.LogDataBuilder mutate();
}
