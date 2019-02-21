package se.andolf.loggerator.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Deque;


@Getter
@AllArgsConstructor
public class BaseData implements LogData {

    private Long start;
    private Long end;
    private Long duration;
    private Deque<LogData> methods;

    public static class Builder {

        Long start;
        Long end;
        Long duration;
        Deque<LogData> methods;

        public BaseData.Builder push(LogData logData) {
            methods.push(logData);
            return this;
        }

        public BaseData.Builder start(long currentTimeMillis) {
            this.start = currentTimeMillis;
            return this;
        }

        public BaseData.Builder end(long currentTimeMillis) {
            this.end = currentTimeMillis;
            return this;
        }

    }
}
