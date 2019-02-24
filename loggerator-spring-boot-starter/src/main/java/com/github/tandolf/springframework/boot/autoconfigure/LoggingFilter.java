package com.github.tandolf.springframework.boot.autoconfigure;

import com.github.tandolf.loggerator.core.LogTransaction;
import com.github.tandolf.loggerator.core.Loggerator;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;

public class LoggingFilter extends CommonsRequestLoggingFilter {

    private final Loggerator loggerator;
    private final PathMatcher pathMatcher;
    private String[] excludedUrls;

    public LoggingFilter(Loggerator loggerator) {
        this.loggerator = loggerator;
        pathMatcher = new AntPathMatcher();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        boolean isFirstRequest = !isAsyncDispatch(request);
        HttpServletRequest requestToUse = request;

        if (isIncludePayload() && isFirstRequest && !(request instanceof ContentCachingRequestWrapper)) {
            requestToUse = new ContentCachingRequestWrapper(request, getMaxPayloadLength());
        }
        final SpringRequestLogEvent logEvent = new SpringRequestLogEvent(requestToUse, response, filterChain);
        logEvent.setMaxPayloadLength(getMaxPayloadLength());
        logEvent.includePayload(isIncludePayload());
        logEvent.includeQueryString(isIncludeQueryString());

        final LogTransaction transaction = loggerator.createTransaction();
        try {
            transaction.execute(logEvent);
        } catch (Throwable throwable) {
            throw new ServletException(throwable);
        }
    }

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return true;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return Stream.of(excludedUrls).anyMatch(p -> pathMatcher.match(p, request.getServletPath()));
    }


    public void addExcludedUrlPatterns(String[] excludedUrls) {
        this.excludedUrls = excludedUrls;
    }
}
