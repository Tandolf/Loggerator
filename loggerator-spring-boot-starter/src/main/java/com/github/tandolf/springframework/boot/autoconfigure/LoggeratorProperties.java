package com.github.tandolf.springframework.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "loggerator")
public class LoggeratorProperties {

    private boolean prettyPrint;
    private Filter filter;
    private boolean timeTransactions = true;

    @Data
    public static class Filter {

        private boolean active = true;
        private boolean includePayload;
        private boolean includeQueryString;
        private int maxPayloadLength = 4096;
        private UrlPatterns urlPatterns = new UrlPatterns();

        @Data
        public static class UrlPatterns {

            private String[] included = {};
            private String[] excluded = {};
        }
    }
}


