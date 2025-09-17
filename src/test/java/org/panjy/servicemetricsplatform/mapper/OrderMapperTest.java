package org.panjy.servicemetricsplatform.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * OrderMapper配置测试类
 * 验证多数据源配置是否正确
 */
@SpringBootTest
public class OrderMapperTest {

    @Test
    public void testApplicationContextLoads() {
        // 这个测试只是验证Spring上下文能否正常加载
        // 如果能运行成功，说明多数据源配置没有问题
        System.out.println("应用上下文加载成功！");
    }
}