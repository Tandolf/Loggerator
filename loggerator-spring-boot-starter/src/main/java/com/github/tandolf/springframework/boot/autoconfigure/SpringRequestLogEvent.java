package com.github.tandolf.springframework.boot.autoconfigure;

import com.github.tandolf.loggerator.core.models.LogData;
import com.github.tandolf.loggerator.core.models.LogEvent;
import com.github.tandolf.loggerator.core.models.RequestData;
import org.springframework.util.Assert;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public class SpringRequestLogEvent implements HttpRequestLogEvent, LogEvent {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final FilterChain filterChain;
    private final RequestData.Builder builder;
    private boolean includePayload;
    private boolean includeQueryString;
    private int maxPayloadLength = 4096;

    public SpringRequestLogEvent(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        this.request = request;
        this.response = response;
        this.filterChain = filterChain;

        builder = RequestData.builder()
                .headers(RequestUtils.getHeaders(request))
                .remoteAddr(request.getRemoteAddr())
                .httpMethod(request.getMethod());
    }

    @Override
    public Object proceed() throws Throwable {
        builder.url(getUrl(request));

        filterChain.doFilter(request, response);

        builder.body(getBody(request));
        builder.returnStatus(true);
        return null;
    }

    @Override
    public LogData getLogData() {
        return builder.build();
    }

    @Override
    public void start(long start) {
        builder.start(start);
    }

    @Override
    public void end(long end) {
        builder.end(end);
    }

    @Override
    public void push(LogData logData) {
        builder.push(logData);
    }

    @Override
    public boolean isTimed() {
        return true;
    }

    @Override
    public String getBody(HttpServletRequest request){
        if(includePayload) {
            return getMessagePayload(request);
        }
        return null;
    }

    @Override
    public void includePayload(boolean includePayload) {
        this.includePayload = includePayload;
    }

    @Override
    public void setMaxPayloadLength(int maxPayloadLength) {
        Assert.isTrue(maxPayloadLength >= 0, "'maxPayloadLength' should be larger than or equal to 0");
        this.maxPayloadLength = maxPayloadLength;
    }

    @Override
    public String getUrl(HttpServletRequest request) {
        final StringBuffer requestURL = request.getRequestURL();
        if(includeQueryString && request.getQueryString() != null) {
            requestURL.append("?").append(request.getQueryString());
        }
        return requestURL.toString();
    }

    @Override
    public void includeQueryString(boolean includeQueryString) {
        this.includeQueryString = includeQueryString;
    }

    private String getMessagePayload(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, maxPayloadLength);
                try {
                    return new String(buf, 0, length, wrapper.getCharacterEncoding())
                            .replace("\n", " ").replace("\t", "");
                }
                catch (UnsupportedEncodingException ex) {
                    return "[unknown]";
                }
            }
        }
        return null;
    }
}
