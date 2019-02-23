package com.github.tandolf.springframework.boot.autoconfigure;

import javax.servlet.http.HttpServletRequest;

public interface HttpRequestLogEvent {

    String getBody(HttpServletRequest request);

    void includePayload(boolean logBody);

    void setMaxPayloadLength(int maxPayloadLength);

    String getUrl(HttpServletRequest request);

    void includeQueryString(boolean includeQueryString);
}
