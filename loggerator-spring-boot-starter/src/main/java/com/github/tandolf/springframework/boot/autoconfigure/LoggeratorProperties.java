package com.github.tandolf.springframework.boot.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "loggerator")
public class LoggeratorProperties {

    private boolean prettyPrint;
    private boolean filter = true;
    private boolean includePayload;
    private boolean includeQueryString;
    private int maxPayloadLength = 4096;
}


