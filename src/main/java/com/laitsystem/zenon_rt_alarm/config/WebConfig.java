package com.laitsystem.zenon_rt_alarm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${docker.address.port01}")
    private String port01;

    @Value("${docker.address.port02}")
    private String port02;

    @Value("${docker.address.port03}")
    private String port03;

    @Value("${docker.address.port04}")
    private String port04;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 경로 지정
                .allowedOrigins(
                        port01,
                        port02,
                        port03,
                        port04
                )
                .allowedMethods("*")  // GET, POST 등 허용할 HTTP 메서드
                .allowedHeaders("*"); // 헤더도 허용
    }
}
