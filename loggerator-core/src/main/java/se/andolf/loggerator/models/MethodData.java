package se.andolf.loggerator.models;

import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;

@Getter
public class MethodData extends BaseData {

    private MethodData(Builder builder) {
        super(builder.start, builder.end, builder.duration, builder.methods);
        this.name = builder.name;
        this.args = builder.args;
        this.returnValue = builder.returnValue;
        this.returnStatus = builder.returnStatus;
        this.thread = builder.thread;
    }

    private String name;
    private Object[] args;
    private Object returnValue;
    private boolean returnStatus;
    private String thread;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BaseData.Builder {

        private Deque<LogData> methods = new ArrayDeque<>();
        private String name;
        private Object[] args;
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
            return new MethodData(this);
        }
    }
}
