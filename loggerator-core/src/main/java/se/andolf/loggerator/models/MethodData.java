package se.andolf.loggerator.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

@Getter
@AllArgsConstructor
public class MethodData implements LogData {

    private String name;
    private Object[] args;
    private Deque<LogData> methods;
    private Long start;
    private Long end;
    private Long duration;
    private Object returnValue;
    private boolean returnStatus;
    private String thread;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Deque<LogData> methods = new ArrayDeque<>();
        private String name;
        private Object[] args;
        private Long start;
        private Long end;
        private Long duration;
        private Object returnValue;
        private boolean returnStatus;
        private String thread;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder args(Object[] args) {
            this.args = args;
            return this;
        }

        public Builder push(LogData logData) {
            methods.push(logData);
            return this;
        }

        public Builder start(long currentTimeMillis) {
            this.start = currentTimeMillis;
            return this;
        }

        public Builder end(long currentTimeMillis) {
            this.end = currentTimeMillis;
            return this;
        }

        public Builder returnValue(Object returnValue) {
            this.returnValue = returnValue;
            return this;
        }

        public Builder returnStatus(boolean returnStatus) {
            this.returnStatus = returnStatus;
            return this;
        }

        public Builder thread(String name) {
            this.thread = name;
            return this;
        }

        public MethodData build() {
            if(end != null && start != null) {
                 duration = end - start;
            }
            return new MethodData(name, args, methods, start, end, duration, returnValue, returnStatus, thread);
        }
    }
}
