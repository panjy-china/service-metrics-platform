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
import org.springframework.context.annotation.Primary;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "org.panjy.servicemetricsplatform.mapper.mysql", sqlSessionFactoryRef = "mysqlSqlSessionFactory")
public class MysqlDataSourceConfig {

    @Bean(name = "mysqlDataSource")
    @Primary
    @ConfigurationProperties("spring.datasource.mysql")
    public DataSource mysqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "mysqlSqlSessionFactory")
    public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier("mysqlDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        // 设置MyBatis配置文件位置
        bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:mybatis-config.xml"));
        // 设置Mapper XML文件位置 - 排除ClickHouse相关的Mapper文件
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        org.springframework.core.io.Resource[] allMappers = resolver.getResources("classpath:mappers/*.xml");
        java.util.List<org.springframework.core.io.Resource> mysqlMappers = new java.util.ArrayList<>();
        for (org.springframework.core.io.Resource resource : allMappers) {
            String filename = resource.getFilename();
            // 排除OrderMapper.xml，因为它是ClickHouse专用的
            if (filename != null && !filename.equals("OrderMapper.xml")) {
                mysqlMappers.add(resource);
            }
        }
        bean.setMapperLocations(mysqlMappers.toArray(new org.springframework.core.io.Resource[0]));
        // 设置类型别名包
        bean.setTypeAliasesPackage("org.panjy.servicemetricsplatform.entity");
        return bean.getObject();
    }

    @Bean(name = "mysqlSqlSessionTemplate")
    public SqlSessionTemplate mysqlSqlSessionTemplate(@Qualifier("mysqlSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
