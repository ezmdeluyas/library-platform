package com.zmd.auth_service.config;

import com.zmd.auth_service.logging.HttpAccessLogFilter;
import com.zmd.auth_service.logging.RequestIdFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<RequestIdFilter> requestIdFilterRegistration(RequestIdFilter filter) {
        FilterRegistrationBean<RequestIdFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE); // run first
        return reg;
    }

    @Bean
    public FilterRegistrationBean<HttpAccessLogFilter> httpAccessLogFilterRegistration(HttpAccessLogFilter filter) {
        FilterRegistrationBean<HttpAccessLogFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // run right after requestId
        return reg;
    }
}