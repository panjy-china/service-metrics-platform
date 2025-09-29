package org.panjy.servicemetricsplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.panjy.servicemetricsplatform.mapper")
public class ServiceMetricsPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceMetricsPlatformApplication.class, args);
    }
}