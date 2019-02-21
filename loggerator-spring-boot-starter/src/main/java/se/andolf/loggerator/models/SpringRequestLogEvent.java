package se.andolf.loggerator.models;

import lombok.Value;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static se.andolf.loggerator.Utils.RequestUtils.getHeaders;

@Value
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
                .headers(getHeaders(request))
                .httpMethod(request.getMethod());
    }

    @Override
    public Object proceed() throws Throwable {
        filterChain.doFilter(request, response);
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
}
