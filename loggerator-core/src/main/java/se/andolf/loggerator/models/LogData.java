package se.andolf.loggerator.models;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.ArrayDeque;
import java.util.Deque;

@Value
@AllArgsConstructor
public class LogData {

    private String name;
    private Object[] args;
    private Deque<LogData> methods;
    private Long start;
    private Long end;
    private Long duration;
    private Object returnValue;
    private boolean returnStatus;

    public static LogData.LogDataBuilder builder() {
        return new LogData.LogDataBuilder();
    }

    public static class LogDataBuilder {

        private Deque<LogData> methods = new ArrayDeque<>();
        private String name;
        private Object[] args;
        private Long start;
        private Long end;
        private Long duration;
        private Object returnValue;
        private boolean returnStatus;

        public LogData.LogDataBuilder name(String name) {
            this.name = name;
            return this;
        }

        public LogData.LogDataBuilder args(Object[] args) {
            this.args = args;
            return this;
        }

        public LogData.LogDataBuilder push(LogData logData) {
            methods.push(logData);
            return this;
        }

        public LogData.LogDataBuilder start(long currentTimeMillis) {
            this.start = currentTimeMillis;
            return this;
        }

        public LogData.LogDataBuilder end(long currentTimeMillis) {
            this.end = currentTimeMillis;
            return this;
        }

        public LogData.LogDataBuilder returnValue(Object returnValue) {
            this.returnValue = returnValue;
            return this;
        }

        public LogData.LogDataBuilder returnStatus(boolean returnStatus) {
            this.returnStatus = returnStatus;
            return this;
        }

        public LogData build() {
            if(end != null && start != null) {
                 duration = end - start;
            }

            return new LogData(name, args, methods, start, end, duration, returnValue, returnStatus);
        }
    }
}
