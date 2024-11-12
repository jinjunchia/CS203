package com.cs203.cs203system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://3.106.213.182:3000", "http://3.106.213.182:8080", "http://3.106.213.182",
                        "http://localhost:3000", "http://localhost:8080",
                        "http://13.211.135.58:8080", "http://13.211.135.58:3000", "http://13.211.135.58",
                        "http://ec2-13-211-135-58.ap-southeast-2.compute.amazonaws.com",
                        "http://ec2-3-106-213-182.ap-southeast-2.compute.amazonaws.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
