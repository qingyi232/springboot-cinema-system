package com.project.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    // 定义一个常量，表示CORS缓存的最大时间，以秒为单位。这里设置为24小时（24小时 * 60分钟/小时 * 60秒/分钟）
    private static final long MAX_AGE = 24 * 60 * 60;

    @Value("${cors.allowed-origins:http://localhost:*,http://127.0.0.1:*}")
    private String allowedOrigins;

    // 使用Spring的@Bean注解，表示该方法会返回一个Bean对象，供Spring容器管理
    @Bean
    public CorsFilter corsFilter() {
        // 创建一个基于URL的CORS配置源对象
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 创建一个CORS配置对象
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
        for (String origin : origins) {
            corsConfiguration.addAllowedOriginPattern(origin);
        }

        // 允许所有的HTTP请求头
        corsConfiguration.addAllowedHeader("*");

        // 允许所有的HTTP请求方法（如GET, POST等）
        corsConfiguration.addAllowedMethod("*");

        corsConfiguration.setAllowCredentials(true);

        // 设置CORS缓存的最大时间
        corsConfiguration.setMaxAge(MAX_AGE);

        // 为所有的URL路径（/**表示所有路径）注册这个CORS配置
        source.registerCorsConfiguration("/**", corsConfiguration);

        // 创建一个CORS过滤器，并传入配置源，然后返回这个过滤器
        return new CorsFilter(source);
    }

}
