package com.github.tandolf.loggerator.core.models;

import lombok.Getter;

import java.util.Map;

@Getter
public class RequestData extends BaseData {

    private final String url;
    private final boolean returnStatus;
    private final String httpMethods;
    private final Map<String, String> headers;
    private final String body;
    private final String remoteAddr;

    private RequestData(Builder builder) {
        super(builder.start, builder.end, builder.duration, builder.methods);
        this.url = builder.url;
        this.returnStatus = builder.returnStatus;
        this.httpMethods = builder.httpMethod;
        this.headers = builder.headers;
        this.body = builder.body;
        this.remoteAddr = builder.remoteAddr;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder extends BaseData.Builder {

        private String url;
        private boolean returnStatus;
        private String httpMethod;
        private Map<String, String> headers;
        private String body;
        private String remoteAddr;

        public Builder url(String url) {
            this.url = url;
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

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder remoteAddr(String remoteAddr) {
            this.remoteAddr = remoteAddr;
            return this;
        }

        public LogData build() {
            if(end != null && start != null) {
                duration = end - start;
            }
            return new RequestData(this);
        }
    }
}
