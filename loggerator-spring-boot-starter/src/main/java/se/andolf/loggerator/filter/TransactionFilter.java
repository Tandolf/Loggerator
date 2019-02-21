package se.andolf.loggerator.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.filter.OncePerRequestFilter;
import se.andolf.loggerator.core.LogTransaction;
import se.andolf.loggerator.core.Loggerator;
import se.andolf.loggerator.models.LogEvent;
import se.andolf.loggerator.models.SpringRequestLogEvent;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;

@Component
public class TransactionFilter extends OncePerRequestFilter {

    private final Loggerator loggerator;

    @Autowired
    public TransactionFilter(Loggerator loggerator) {
        this.loggerator = loggerator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) {
        final LogTransaction transaction = loggerator.createTransaction();
        final LogEvent logEvent = new SpringRequestLogEvent(httpServletRequest, httpServletResponse, filterChain);
        try {
            transaction.execute(logEvent);
        } catch (Throwable throwable) {
            throw HttpServerErrorException.InternalServerError
                    .create(HttpStatus.INTERNAL_SERVER_ERROR, throwable.getMessage(), null, null, Charset.defaultCharset());
        }
    }
}
