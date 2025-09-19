package org.panjy.servicemetricsplatform.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.panjy.servicemetricsplatform.mapper.clickhouse.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * 客户分析服务测试类
 * 
 * @author System Generated
 */
@SpringBootTest
public class ClientAnalysisServiceTest {

    @Autowired
    private ClientAnalysisService clientAnalysisService;

    @MockBean
    private ClientMapper clientMapper;

    @Test
    public void testGetAgeDistribution() {
        // 准备测试数据
        List<Map<String, Object>> mockData = new ArrayList<>();
        Map<String, Object> data1 = new HashMap<>();
        data1.put("gender", "男");
        data1.put("ageGroup", "18-29岁");
        data1.put("count", 100);
        mockData.add(data1);

        Map<String, Object> data2 = new HashMap<>();
        data2.put("gender", "女");
        data2.put("ageGroup", "30-39岁");
        data2.put("count", 150);
        mockData.add(data2);

        // 模拟Mapper行为
        when(clientMapper.selectAgeDistribution()).thenReturn(mockData);

        // 调用服务方法
        List<Map<String, Object>> result = clientAnalysisService.getAgeDistribution();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("男", result.get(0).get("gender"));
        assertEquals("18-29岁", result.get(0).get("ageGroup"));
        assertEquals(100, result.get(0).get("count"));
        assertEquals("女", result.get(1).get("gender"));
        assertEquals("30-39岁", result.get(1).get("ageGroup"));
        assertEquals(150, result.get(1).get("count"));

        // 验证Mapper方法被调用
        verify(clientMapper, times(1)).selectAgeDistribution();
    }
}