package org.panjy.servicemetricsplatform.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class ClickHouseDataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource clickHouseDataSource() {
        return DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .url("jdbc:clickhouse://106.14.221.209:8123/aikang?compress=0&http_connection_provider=HTTP_URL_CONNECTION")
                .username("default")
                .password("123456")
                .driverClassName("com.clickhouse.jdbc.ClickHouseDriver")
                .build();
    }
}