package org.panjy.servicemetricsplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("org.panjy.servicemetricsplatform.mapper")
@EnableScheduling
public class ServiceMetricsPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceMetricsPlatformApplication.class, args);
    }
}