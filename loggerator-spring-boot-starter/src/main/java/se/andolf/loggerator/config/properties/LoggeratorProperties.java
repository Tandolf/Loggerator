package se.andolf.loggerator.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "loggerator")
public class LoggeratorProperties {

    public boolean prettyPrint;
    public boolean filter = true;
}


