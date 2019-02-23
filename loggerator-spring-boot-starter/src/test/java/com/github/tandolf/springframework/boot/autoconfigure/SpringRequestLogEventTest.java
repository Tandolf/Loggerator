package com.github.tandolf.springframework.boot.autoconfigure;

import com.github.tandolf.loggerator.core.models.RequestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

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
}