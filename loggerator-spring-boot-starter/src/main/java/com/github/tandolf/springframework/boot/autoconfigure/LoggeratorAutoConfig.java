package com.github.tandolf.springframework.boot.autoconfigure;

import brave.Tracer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import com.github.tandolf.loggerator.core.Loggerator;

@Configuration
@ConditionalOnClass(Loggerator.class)
@EnableConfigurationProperties(LoggeratorProperties.class)
@EnableAspectJAutoProxy
public class LoggeratorAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public Loggerator loggerator(@Qualifier("loggeratorObjectMapper") ObjectMapper objectMapper) {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
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
    public FilterRegistrationBean<LoggingFilter> registration(LoggingFilter filter, LoggeratorProperties loggeratorProperties) {
        final FilterRegistrationBean<LoggingFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(loggeratorProperties.isFilter());
        return registration;
    }

//    @Bean
//    public TransactionFilter transActionfilter(Loggerator loggerator, Tracer tracer) {
//        return new TransactionFilter(loggerator, tracer);
//    }

    @Bean
    public LoggingFilter loggingFilter(Loggerator loggerator, LoggeratorProperties properties) {
        final LoggingFilter loggingFilter = new LoggingFilter(loggerator, properties);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setMaxPayloadLength(4096);
        return loggingFilter;
    }
//
//    @Bean
//    public TestLoggingFilter testLoggingFilter() {
//        final TestLoggingFilter testLoggingFilter = new TestLoggingFilter();
//        testLoggingFilter.setIncludeQueryString(true);
//        testLoggingFilter.setIncludePayload(true);
//        testLoggingFilter.setIncludeClientInfo(true);
//        testLoggingFilter.setMaxPayloadLength(4096);
//        return testLoggingFilter;
//    }

    @Bean
    public LogAspect logAspect(Loggerator loggerator) {
        return new LogAspect(loggerator);
    }
}
