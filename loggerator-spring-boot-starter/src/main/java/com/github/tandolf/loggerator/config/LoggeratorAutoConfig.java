package com.github.tandolf.loggerator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.tandolf.loggerator.filter.TransactionFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import com.github.tandolf.loggerator.config.properties.LoggeratorProperties;
import com.github.tandolf.loggerator.core.Loggerator;

@Configuration
@ConditionalOnClass(Loggerator.class)
@EnableConfigurationProperties(LoggeratorProperties.class)
@EnableAspectJAutoProxy
public class LoggeratorAutoConfig {

    @Bean
    @ConditionalOnMissingBean
    public Loggerator loggerator(@Qualifier("loggeratorObjectMapper") ObjectMapper objectMapper) {
        CommonsRequestLoggingFilter filter
                = new CommonsRequestLoggingFilter();
        return Loggerator.builder()
                .setObjectMapper(objectMapper)
                .build();
    }

    @Bean
    @Qualifier("loggeratorObjectMapper")
    public ObjectMapper objectMapper(LoggeratorProperties properties) {
        final ObjectMapper objectMapper = new ObjectMapper();

        if(properties.prettyPrint) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        return objectMapper;
    }

    @Bean
    public FilterRegistrationBean<TransactionFilter> registration(TransactionFilter filter, LoggeratorProperties loggeratorProperties) {
        final FilterRegistrationBean<TransactionFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(loggeratorProperties.filter);
        return registration;
    }
}
