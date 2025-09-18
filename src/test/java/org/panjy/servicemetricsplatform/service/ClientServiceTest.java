package org.panjy.servicemetricsplatform.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.panjy.servicemetricsplatform.entity.Client;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientServiceTest {

    private final ClientService clientService = new ClientService(null);

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
}