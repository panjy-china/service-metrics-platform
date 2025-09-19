package org.panjy.servicemetricsplatform;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.panjy.servicemetricsplatform.entity.Client;

import java.util.ArrayList;
import java.util.List;

public class TestJsonParsing {
    public static void main(String[] args) {
        try {
            // 模拟的大模型返回结果
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
                    "    },\n" +
                    "    {\n" +
                    "      \"client_id\": \"AF4110000080\",\n" +
                    "      \"gender\": \"女\",\n" +
                    "      \"age\": 74,\n" +
                    "      \"height_cm\": 160,\n" +
                    "      \"weight_kg\": 50\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"client_id\": \"AF2070000801\",\n" +
                    "      \"gender\": \"女\",\n" +
                    "      \"age\": 60,\n" +
                    "      \"height_cm\": 153,\n" +
                    "      \"weight_kg\": 52.5\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"client_id\": \"AF0600000133\",\n" +
                    "      \"gender\": \"女\",\n" +
                    "      \"age\": 40,\n" +
                    "      \"height_cm\": 145,\n" +
                    "      \"weight_kg\": 50\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            System.out.println("原始JSON结果:");
            System.out.println(jsonResult);
            
            // 创建原始批次客户（模拟）
            List<Client> originalBatch = new ArrayList<>();
            String[] clientIds = {"AF2640000406", "AF4100000064", "AF4110000080", "AF2070000801", "AF0600000133"};
            for (String clientId : clientIds) {
                Client client = new Client();
                client.setColCltID(clientId);
                client.setColDemo("测试备注 for " + clientId);
                originalBatch.add(client);
            }
            
            System.out.println("\n原始批次客户数: " + originalBatch.size());
            
            // 解析JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            
            JsonNode rootNode = objectMapper.readTree(jsonResult);
            JsonNode clientsNode = rootNode.get("clients");
            
            System.out.println("\n解析到的clients节点: " + (clientsNode != null ? "存在" : "不存在"));
            if (clientsNode != null) {
                System.out.println("clients数组大小: " + clientsNode.size());
                
                List<Client> updatedClients = new ArrayList<>();
                for (JsonNode clientNode : clientsNode) {
                    String clientId = clientNode.get("client_id") != null ? 
                                    clientNode.get("client_id").asText() : null;
                    
                    System.out.println("处理客户ID: " + clientId);
                    
                    if (clientId != null) {
                        // 在原始批次中查找对应的客户
                        Client originalClient = findClientById(clientId, originalBatch);
                        if (originalClient != null) {
                            System.out.println("  找到匹配的原始客户: " + clientId);
                            // 创建新的客户对象，包含分析结果（简化版）
                            updatedClients.add(originalClient);
                            System.out.println("  添加到更新列表");
                        } else {
                            System.out.println("  未在原始批次中找到客户ID: " + clientId);
                        }
                    } else {
                        System.out.println("  客户节点中未找到client_id字段");
                    }
                }
                
                System.out.println("\n最终更新客户数: " + updatedClients.size());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static Client findClientById(String clientId, List<Client> batch) {
        for (Client client : batch) {
            if (clientId.equals(client.getColCltID())) {
                return client;
            }
        }
        return null;
    }
}