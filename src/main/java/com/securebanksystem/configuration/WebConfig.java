package com.securebanksystem.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ApiTimingInterceptor apiTimingInterceptor;

    WebConfig(ApiTimingInterceptor apiTimingInterceptor) {
        this.apiTimingInterceptor = apiTimingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiTimingInterceptor);
    }
}