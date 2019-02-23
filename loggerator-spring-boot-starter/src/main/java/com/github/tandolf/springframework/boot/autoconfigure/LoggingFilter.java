package com.github.tandolf.springframework.boot.autoconfigure;

import com.github.tandolf.loggerator.core.LogTransaction;
import com.github.tandolf.loggerator.core.Loggerator;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoggingFilter extends CommonsRequestLoggingFilter {

    private final Loggerator loggerator;

    public LoggingFilter(Loggerator loggerator) {
        this.loggerator = loggerator;
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

//    @Override
//    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
//        StringBuilder msg = new StringBuilder();
//        msg.append(prefix);
//        msg.append("uri=").append(request.getRequestURI());
//
//        if (isIncludeQueryString()) {
//            String queryString = request.getQueryString();
//            if (queryString != null) {
//                msg.append('?').append(queryString);
//            }
//        }
//
//        if (isIncludeClientInfo()) {
//            String client = request.getRemoteAddr();
//            if (StringUtils.hasLength(client)) {
//                msg.append(";client=").append(client);
//            }
//            HttpSession session = request.getSession(false);
//            if (session != null) {
//                msg.append(";session=").append(session.getId());
//            }
//            String user = request.getRemoteUser();
//            if (user != null) {
//                msg.append(";user=").append(user);
//            }
//        }
//
//        if (isIncludeHeaders()) {
//            msg.append(";headers=").append(new ServletServerHttpRequest(request).getHeaders());
//        }
//
//        if (isIncludePayload()) {
//            String payload = getMessagePayload(request);
//            if (payload != null) {
//                msg.append(";payload=").append(payload);
//            }
//        }
//
//        msg.append(suffix);
//        return msg.toString();
//    }
}
