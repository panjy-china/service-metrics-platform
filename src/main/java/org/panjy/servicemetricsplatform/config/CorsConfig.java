package org.panjy.servicemetricsplatform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置跨域路径
        registry.addMapping("/**")
                // 允许的域名，可以改为具体地址，比如 http://localhost:3000
                .allowedOrigins("*")  // 如果是开发环境的前端地址
                .allowCredentials(false)  // 允许前端携带凭证信息
                // 允许的方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 允许的头部信息
                .allowedHeaders("*")
                // 预检请求的有效期，单位：秒
                .maxAge(3600);
    }
}
