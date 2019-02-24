package com.github.tandolf.springframework.boot.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tandolf.loggerator.core.LogAspect;
import com.github.tandolf.loggerator.core.Loggerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
    public Loggerator loggerator(LoggeratorProperties properties) {
        return Loggerator.builder()
                .setObjectMapper(objectMapper(properties))
                .timeTransactions(properties.isTimeTransactions())
                .build();
    }

    private ObjectMapper objectMapper(LoggeratorProperties properties) {
        final ObjectMapper objectMapper = new ObjectMapper();

        if(properties.isPrettyPrint()) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return objectMapper;
    }

    private LoggingFilter loggingFilter(Loggerator loggerator, LoggeratorProperties properties) {
        final LoggingFilter loggingFilter = new LoggingFilter(loggerator);
        loggingFilter.setIncludeQueryString(properties.getFilter().isIncludeQueryString());
        loggingFilter.setIncludePayload(properties.getFilter().isIncludePayload());
        loggingFilter.setMaxPayloadLength(properties.getFilter().getMaxPayloadLength());
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.addExcludedUrlPatterns(properties.getFilter().getUrlPatterns().getExcluded());
        return loggingFilter;
    }

    @Bean
    @ConditionalOnProperty(prefix = "loggerator", name = "filter", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean someFilterRegistration(Loggerator loggerator, LoggeratorProperties properties) {
        final FilterRegistrationBean<LoggingFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(loggingFilter(loggerator, properties));
        registration.addUrlPatterns(properties.getFilter().getUrlPatterns().getIncluded());
        registration.addInitParameter("paramName", "paramValue");
        registration.setName("transactionFilter");
        return registration;
    }

    @Bean
    public LogAspect logAspect(Loggerator loggerator) {
        return new LogAspect(loggerator);
    }
}
