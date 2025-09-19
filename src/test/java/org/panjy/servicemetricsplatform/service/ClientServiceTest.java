package org.panjy.servicemetricsplatform.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.panjy.servicemetricsplatform.entity.Client;
import org.panjy.servicemetricsplatform.mapper.clickhouse.ClientMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    private final ClientMapper clientMapper = mock(ClientMapper.class);
    private final ClientService clientService = new ClientService(clientMapper);

    @Test
    void testParseAnalysisResult() throws Exception {
        // 准备测试数据
        String jsonResult = "{\n" +
                "  \"clients\": [\n" +
                "    {\n" +
                "      \"client_id\": \"AF2640000406\",\n" +
                "      \"gender\": \"男\",\n" +
                "      \"age\": 70,\n" +
                "      \"height_cm\": 165,\n" +
                "      \"weight_kg\": 66\n" +
                "    },\n" +
                "    {\n" +
                "      \"client_id\": \"AF4100000064\",\n" +
                "      \"gender\": \"女\",\n" +
                "      \"age\": 64,\n" +
                "      \"height_cm\": 155,\n" +
                "      \"weight_kg\": 49\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // 创建原始批次客户
        List<Client> originalBatch = new ArrayList<>();
        Client client1 = new Client();
        client1.setColCltID("AF2640000406");
        client1.setColDemo("测试备注1");
        originalBatch.add(client1);
        
        Client client2 = new Client();
        client2.setColCltID("AF4100000064");
        client2.setColDemo("测试备注2");
        originalBatch.add(client2);

        // 测试解析方法
        // 使用反射调用私有方法
        /*
        java.lang.reflect.Method method = ClientService.class.getDeclaredMethod(
                "parseAnalysisResult", String.class, List.class);
        method.setAccessible(true);
        List<Client> result = (List<Client>) method.invoke(clientService, jsonResult, originalBatch);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        
        Client analyzedClient1 = result.get(0);
        assertEquals("男", analyzedClient1.getColGender());
        assertEquals(Integer.valueOf(70), analyzedClient1.getColAge());
        assertEquals(Integer.valueOf(165), analyzedClient1.getColHeight());
        assertEquals(Integer.valueOf(66), analyzedClient1.getColWeight());
        assertTrue(analyzedClient1.getColDemo().contains("AI分析"));
        */
    }
    
    @Test
    void testGetGenderDistribution() {
        // 准备测试数据
        List<Map<String, Object>> mockData = new ArrayList<>();
        Map<String, Object> maleData = new HashMap<>();
        maleData.put("gender", "男");
        maleData.put("count", 150);
        mockData.add(maleData);
        
        Map<String, Object> femaleData = new HashMap<>();
        femaleData.put("gender", "女");
        femaleData.put("count", 200);
        mockData.add(femaleData);
        
        // 设置mock行为
        when(clientMapper.selectGenderDistribution()).thenReturn(mockData);
        
        // 调用方法
        List<Map<String, Object>> result = clientService.getGenderDistribution();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("男", result.get(0).get("gender"));
        assertEquals(150, result.get(0).get("count"));
        assertEquals("女", result.get(1).get("gender"));
        assertEquals(200, result.get(1).get("count"));
        
        // 验证mapper方法被调用
        verify(clientMapper, times(1)).selectGenderDistribution();
    }
    
    @Test
    void testGetAgeDistribution() {
        // 准备测试数据
        List<Map<String, Object>> mockData = new ArrayList<>();
        Map<String, Object> ageGroup1 = new HashMap<>();
        ageGroup1.put("ageGroup", "18-24岁");
        ageGroup1.put("count", 50);
        mockData.add(ageGroup1);
        
        Map<String, Object> ageGroup2 = new HashMap<>();
        ageGroup2.put("ageGroup", "25-34岁");
        ageGroup2.put("count", 120);
        mockData.add(ageGroup2);
        
        Map<String, Object> ageGroup3 = new HashMap<>();
        ageGroup3.put("ageGroup", "35-44岁");
        ageGroup3.put("count", 80);
        mockData.add(ageGroup3);
        
        // 设置mock行为
        when(clientMapper.selectAgeDistribution()).thenReturn(mockData);
        
        // 调用方法
        List<Map<String, Object>> result = clientService.getAgeDistribution();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("18-24岁", result.get(0).get("ageGroup"));
        assertEquals(50, result.get(0).get("count"));
        assertEquals("25-34岁", result.get(1).get("ageGroup"));
        assertEquals(120, result.get(1).get("count"));
        assertEquals("35-44岁", result.get(2).get("ageGroup"));
        assertEquals(80, result.get(2).get("count"));
        
        // 验证mapper方法被调用
        verify(clientMapper, times(1)).selectAgeDistribution();
    }
    
    @Test
    void testGetWeightDistribution() {
        // 准备测试数据
        List<Map<String, Object>> mockData = new ArrayList<>();
        Map<String, Object> weightGroup1 = new HashMap<>();
        weightGroup1.put("weightGroup", "40-50kg");
        weightGroup1.put("count", 30);
        mockData.add(weightGroup1);
        
        Map<String, Object> weightGroup2 = new HashMap<>();
        weightGroup2.put("weightGroup", "50-60kg");
        weightGroup2.put("count", 80);
        mockData.add(weightGroup2);
        
        Map<String, Object> weightGroup3 = new HashMap<>();
        weightGroup3.put("weightGroup", "60-70kg");
        weightGroup3.put("count", 120);
        mockData.add(weightGroup3);
        
        Map<String, Object> weightGroup4 = new HashMap<>();
        weightGroup4.put("weightGroup", "70-80kg");
        weightGroup4.put("count", 60);
        mockData.add(weightGroup4);
        
        Map<String, Object> weightGroup5 = new HashMap<>();
        weightGroup5.put("weightGroup", "80kg以上");
        weightGroup5.put("count", 20);
        mockData.add(weightGroup5);
        
        // 设置mock行为
        when(clientMapper.selectWeightDistribution()).thenReturn(mockData);
        
        // 调用方法
        List<Map<String, Object>> result = clientService.getWeightDistribution();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("40-50kg", result.get(0).get("weightGroup"));
        assertEquals(30, result.get(0).get("count"));
        assertEquals("50-60kg", result.get(1).get("weightGroup"));
        assertEquals(80, result.get(1).get("count"));
        assertEquals("60-70kg", result.get(2).get("weightGroup"));
        assertEquals(120, result.get(2).get("count"));
        assertEquals("70-80kg", result.get(3).get("weightGroup"));
        assertEquals(60, result.get(3).get("count"));
        assertEquals("80kg以上", result.get(4).get("weightGroup"));
        assertEquals(20, result.get(4).get("count"));
        
        // 验证mapper方法被调用
        verify(clientMapper, times(1)).selectWeightDistribution();
    }
    
    @Test
    void testGetHeightDistribution() {
        // 准备测试数据
        List<Map<String, Object>> mockData = new ArrayList<>();
        
        Map<String, Object> heightGroup1 = new HashMap<>();
        heightGroup1.put("heightGroup", "150cm以下");
        heightGroup1.put("count", 10);
        mockData.add(heightGroup1);
        
        Map<String, Object> heightGroup2 = new HashMap<>();
        heightGroup2.put("heightGroup", "150-159cm");
        heightGroup2.put("count", 45);
        mockData.add(heightGroup2);
        
        Map<String, Object> heightGroup3 = new HashMap<>();
        heightGroup3.put("heightGroup", "160-169cm");
        heightGroup3.put("count", 120);
        mockData.add(heightGroup3);
        
        Map<String, Object> heightGroup4 = new HashMap<>();
        heightGroup4.put("heightGroup", "170-179cm");
        heightGroup4.put("count", 95);
        mockData.add(heightGroup4);
        
        Map<String, Object> heightGroup5 = new HashMap<>();
        heightGroup5.put("heightGroup", "180-189cm");
        heightGroup5.put("count", 35);
        mockData.add(heightGroup5);
        
        Map<String, Object> heightGroup6 = new HashMap<>();
        heightGroup6.put("heightGroup", "190cm以上");
        heightGroup6.put("count", 5);
        mockData.add(heightGroup6);
        
        // 设置mock行为
        when(clientMapper.selectHeightDistribution()).thenReturn(mockData);
        
        // 调用方法
        List<Map<String, Object>> result = clientService.getHeightDistribution();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(6, result.size());
        assertEquals("150cm以下", result.get(0).get("heightGroup"));
        assertEquals(10, result.get(0).get("count"));
        assertEquals("150-159cm", result.get(1).get("heightGroup"));
        assertEquals(45, result.get(1).get("count"));
        assertEquals("160-169cm", result.get(2).get("heightGroup"));
        assertEquals(120, result.get(2).get("count"));
        assertEquals("170-179cm", result.get(3).get("heightGroup"));
        assertEquals(95, result.get(3).get("count"));
        assertEquals("180-189cm", result.get(4).get("heightGroup"));
        assertEquals(35, result.get(4).get("count"));
        assertEquals("190cm以上", result.get(5).get("heightGroup"));
        assertEquals(5, result.get(5).get("count"));
        
        // 验证mapper方法被调用
        verify(clientMapper, times(1)).selectHeightDistribution();
    }
}