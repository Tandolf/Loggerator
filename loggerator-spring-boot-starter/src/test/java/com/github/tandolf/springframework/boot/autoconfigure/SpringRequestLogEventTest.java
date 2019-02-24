package com.github.tandolf.springframework.boot.autoconfigure;

import com.github.tandolf.loggerator.core.models.RequestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Tests for SpringRequestLogEvent.class")
class SpringRequestLogEventTest {

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void beforeEach() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @Test
    @DisplayName("Log given http method")
    void shouldLogRequestMethod() throws Throwable {
        request.setMethod(HttpMethod.GET.name());
        final SpringRequestLogEvent logEvent = new SpringRequestLogEvent(request, response, filterChain);
        logEvent.proceed();
        final RequestData logData = (RequestData) logEvent.getLogData();
        assertEquals(HttpMethod.GET.name(), logData.getHttpMethods());
    }

    @Test
    @DisplayName("Log given headers")
    void shouldLogHeaders() throws Throwable {
        request.addHeader("myHeader", "myData");
        final SpringRequestLogEvent logEvent = new SpringRequestLogEvent(request, response, filterChain);
        logEvent.proceed();
        final RequestData logData = (RequestData) logEvent.getLogData();
        assertThat(logData.getHeaders(), hasEntry("myHeader", "myData"));
    }

    @Test
    void shouldLogRequestUri() throws Throwable {
        final String uri = "https://myLocalHost:8080/myPath";
        request.setServerName("myLocalHost");
        request.setRequestURI("/myPath");
        request.setScheme("https");
        request.setServerPort(8080);
        final SpringRequestLogEvent logEvent = new SpringRequestLogEvent(request, response, filterChain);
        logEvent.proceed();
        final RequestData logData = (RequestData) logEvent.getLogData();
        assertEquals(uri, logData.getUrl());
    }

    @Test
    void shouldLogRequestUriWithQueryString() throws Throwable {
        final String uri = "https://myLocalHost:8080/myPath?test=hello";
        request.setServerName("myLocalHost");
        request.setRequestURI("/myPath");
        request.setScheme("https");
        request.setQueryString("test=hello");
        request.setServerPort(8080);
        final SpringRequestLogEvent logEvent = new SpringRequestLogEvent(request, response, filterChain);
        logEvent.includeQueryString(true);
        logEvent.proceed();
        final RequestData logData = (RequestData) logEvent.getLogData();
        assertEquals(uri, logData.getUrl());
    }

    @Test
    void shouldLogPayload() throws Throwable {
        final String payload = "1";
        request.setContent(payload.getBytes());
        final ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        final int byteRead = requestWrapper.getInputStream().read();
        final SpringRequestLogEvent logEvent = new SpringRequestLogEvent(requestWrapper, response, filterChain);
        logEvent.includePayload(true);
        logEvent.proceed();
        final RequestData logData = (RequestData) logEvent.getLogData();
        assertEquals(payload, logData.getBody());
        assertEquals(payload.getBytes()[0], byteRead);
    }

    @Test
    void shouldLogPayloadWIthALimit() throws Throwable {
        final String payload = "10";
        final int payloadLimit = 1;
        request.setContent(payload.getBytes());
        final ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        final int firstByte = requestWrapper.getInputStream().read();
        final int secondByte = requestWrapper.getInputStream().read();
        final SpringRequestLogEvent logEvent = new SpringRequestLogEvent(requestWrapper, response, filterChain);
        logEvent.includePayload(true);
        logEvent.setMaxPayloadLength(payloadLimit);
        logEvent.proceed();
        final RequestData logData = (RequestData) logEvent.getLogData();
        assertEquals("1", logData.getBody());
        assertEquals(payload.getBytes()[0], firstByte);
        assertEquals(payload.getBytes()[1], secondByte);
    }
}