package com.github.tandolf.loggerator.core.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseData implements LogData {

    private Long start;
    private Long end;
    private Long duration;
    private Deque<LogData> methods;

    public static class Builder {

        Long start;
        Long end;
        Long duration;
        Deque<LogData> methods = new ArrayDeque<>();

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
