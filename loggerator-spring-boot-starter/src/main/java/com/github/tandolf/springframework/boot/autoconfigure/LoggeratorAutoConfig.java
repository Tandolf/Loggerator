package com.github.tandolf.springframework.boot.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tandolf.loggerator.core.LogAspect;
import com.github.tandolf.loggerator.core.Loggerator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ConditionalOnClass(Loggerator.class)
@EnableConfigurationProperties(LoggeratorProperties.class)
@EnableAspectJAutoProxy
public class LoggeratorAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public Loggerator loggerator(@Qualifier("loggeratorObjectMapper") ObjectMapper objectMapper) {
        return Loggerator.builder()
                .setObjectMapper(objectMapper)
                .build();
    }

    @Bean
    @Qualifier("loggeratorObjectMapper")
    public ObjectMapper objectMapper(LoggeratorProperties properties) {
        final ObjectMapper objectMapper = new ObjectMapper();

        if(properties.isPrettyPrint()) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return objectMapper;
    }

    @Bean
    @ConditionalOnProperty(prefix = "loggerator", name = "filter", havingValue = "true", matchIfMissing = true)
    public LoggingFilter loggingFilter(Loggerator loggerator, LoggeratorProperties properties) {
        final LoggingFilter loggingFilter = new LoggingFilter(loggerator);
        loggingFilter.setIncludeQueryString(properties.getFilter().isIncludeQueryString());
        loggingFilter.setIncludePayload(properties.getFilter().isIncludePayload());
        loggingFilter.setMaxPayloadLength(properties.getFilter().getMaxPayloadLength());
        loggingFilter.setIncludeClientInfo(true);
        return loggingFilter;
    }

    @Bean
    public LogAspect logAspect(Loggerator loggerator) {
        return new LogAspect(loggerator);
    }
}
