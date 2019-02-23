package com.github.tandolf.springframework.boot.autoconfigure;

import com.github.tandolf.loggerator.core.models.LogData;
import com.github.tandolf.loggerator.core.models.LogEvent;
import com.github.tandolf.loggerator.core.models.RequestData;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public class SpringRequestLogEvent implements LogEvent {

    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final FilterChain filterChain;
    private final RequestData.Builder builder;

    public SpringRequestLogEvent(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        this.request = request;
        this.response = response;
        this.filterChain = filterChain;

        builder = RequestData.builder()
                .url(request.getRequestURL().toString())
                .headers(RequestUtils.getHeaders(request))
                .httpMethod(request.getMethod());
    }

    @Override
    public Object proceed() throws Throwable {
        filterChain.doFilter(request, response);
        builder.body(getMessagePayload(request));
        builder.returnStatus(true);
        return null;
    }

    @Override
    public LogData getLogData() {
        return builder.build();
    }

    @Override
    public void start(long currentTimeMillis) {
        builder.start(System.currentTimeMillis());
    }

    @Override
    public void end(long currentTimeMillis) {
        builder.end(System.currentTimeMillis());
    }

    @Override
    public void push(LogData logData) {
        builder.push(logData);
    }

    protected String getMessagePayload(HttpServletRequest request) {
        ContentCachingRequestWrapper wrapper = WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);
        if (wrapper != null) {
            byte[] buf = wrapper.getContentAsByteArray();
            if (buf.length > 0) {
                int length = Math.min(buf.length, 4096);
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
