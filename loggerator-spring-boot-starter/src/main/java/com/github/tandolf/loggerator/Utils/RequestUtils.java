package com.github.tandolf.loggerator.Utils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class RequestUtils {

    private RequestUtils() {
    }

    public static Map<String, String> getHeaders(HttpServletRequest request) {
        final Stream<String> headerNames = StreamSupport.stream(Spliterators.spliteratorUnknownSize(request.getHeaderNames().asIterator(), Spliterator.ORDERED), true);
        return headerNames.collect(Collectors.toMap(headerName -> headerName, request::getHeader));
    }
}
