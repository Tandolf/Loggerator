package se.andolf.loggerator.core;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.andolf.loggerator.models.LogEvent;


@Value
@AllArgsConstructor
public class LogTransaction {

    final static Logger logger = LoggerFactory.getLogger("Transactions");
    private final ObjectMapper objectMapper;

    public Object execute(LogEvent logEvent) {
        final Object proceed = logEvent.proceed();
        try {
            logger.info(objectMapper.writeValueAsString(logEvent.getLogData()));
        } catch (JsonProcessingException e) {
            //TODO: handle exception
            e.printStackTrace();
        }
        return proceed;
    }
}
