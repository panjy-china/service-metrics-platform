package org.panjy.servicemetricsplatform.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@MapperScan(basePackages = "org.panjy.servicemetricsplatform.mapper.clickhouse", sqlSessionFactoryRef = "clickhouseSqlSessionFactory")
public class ClickHouseDataSourceConfig {

    @Bean(name = "clickhouseDataSource")
    @ConfigurationProperties("spring.datasource.clickhouse")
    public DataSource clickhouseDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "clickhouseSqlSessionFactory")
    public SqlSessionFactory clickhouseSqlSessionFactory(@Qualifier("clickhouseDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // 设置MyBatis配置文件位置
        bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));
        
        // ClickHouse加载OrderMapper.xml、ClientMapper.xml和ServerTimeMapper.xml - 分别获取资源并合并
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> mapperResources = new ArrayList<>();
        mapperResources.addAll(Arrays.asList(resolver.getResources("classpath:mappers/OrderMapper.xml")));
        mapperResources.addAll(Arrays.asList(resolver.getResources("classpath:mappers/ClientMapper.xml")));
        mapperResources.addAll(Arrays.asList(resolver.getResources("classpath:mappers/ServerTimeMapper.xml")));
        bean.setMapperLocations(mapperResources.toArray(new Resource[0]));
        // 设置类型别名包
        bean.setTypeAliasesPackage("org.panjy.servicemetricsplatform.entity");
        return bean.getObject();
    }

    @Bean(name = "clickhouseSqlSessionTemplate")
    public SqlSessionTemplate clickhouseSqlSessionTemplate(@Qualifier("clickhouseSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
