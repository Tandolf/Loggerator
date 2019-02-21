package se.andolf.loggerator.models;

import lombok.Value;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

@Value
public class RequestData implements LogData {

    private final String url;
    private final Long start;
    private final Long end;
    private final Long duration;
    private final boolean returnStatus;
    private final Deque<LogData> methods;
    private final String httpMethods;
    private final Map<String, String> headers;

    public static RequestData.Builder builder() {
        return new RequestData.Builder();
    }

    public static class Builder {

        private String url;
        private Long start;
        private Long end;
        private Deque<LogData> methods = new ArrayDeque<>();
        private boolean returnStatus;
        private Long duration;
        private String httpMethod;
        private Map<String, String> headers;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public RequestData.Builder push(LogData logData) {
            methods.push(logData);
            return this;
        }

        public RequestData.Builder start(long currentTimeMillis) {
            this.start = currentTimeMillis;
            return this;
        }

        public RequestData.Builder end(long currentTimeMillis) {
            this.end = currentTimeMillis;
            return this;
        }

        public RequestData.Builder returnStatus(boolean returnStatus) {
            this.returnStatus = returnStatus;
            return this;
        }

        public Builder httpMethod(String httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public LogData build() {
            if(end != null && start != null) {
                duration = end - start;
            }
            return new RequestData(url, start, end, duration, returnStatus, methods, httpMethod, headers);
        }
    }
}
