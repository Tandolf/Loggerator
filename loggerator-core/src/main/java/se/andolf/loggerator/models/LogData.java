package se.andolf.loggerator.models;

import lombok.Value;

import java.util.ArrayDeque;
import java.util.Deque;

@Value
public class LogData {

    private String name;
    private Object[] args;
    private Deque<LogData> methods;

    public static LogData.LogDataBuilder builder() {
        return new LogData.LogDataBuilder();
    }

    public static class LogDataBuilder {

        private Deque<LogData> methods = new ArrayDeque<>();
        private String name;
        private Object[] args;

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

        public LogData build() {
            return new LogData(name, args, methods);
        }
    }
}
