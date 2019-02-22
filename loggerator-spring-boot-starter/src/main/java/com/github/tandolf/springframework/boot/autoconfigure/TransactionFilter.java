package com.github.tandolf.springframework.boot.autoconfigure;

import brave.Tracer;
import com.github.tandolf.loggerator.core.LogTransaction;
import com.github.tandolf.loggerator.core.Loggerator;
import com.github.tandolf.loggerator.core.models.LogEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;

public class TransactionFilter extends OncePerRequestFilter {

    private final Loggerator loggerator;
    private final Tracer tracer;

    @Autowired
    public TransactionFilter(Loggerator loggerator, Tracer tracer) {
        this.loggerator = loggerator;
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) {
        final LogTransaction transaction = loggerator.createTransaction();
        final LogEvent logEvent = new SpringRequestLogEvent(httpServletRequest, httpServletResponse, filterChain);
        System.out.println("SpanId: " + tracer.currentSpan().context().spanIdString());
        System.out.println("TraceId: " + tracer.currentSpan().context().traceIdString());
        try {
            transaction.execute(logEvent);
        } catch (Throwable throwable) {
            throw HttpServerErrorException.InternalServerError
                    .create(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage(), null, null, Charset.defaultCharset());
        }
    }
}
