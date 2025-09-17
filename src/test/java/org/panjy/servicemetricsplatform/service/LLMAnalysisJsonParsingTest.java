package org.panjy.servicemetricsplatform.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JSON解析错误修复测试
 */
@SpringBootTest
public class LLMAnalysisJsonParsingTest {
    
    @Autowired
    private LLMAnalysisService llmAnalysisService;
    
    /**
     * 测试JSON解析错误修复
     */
    @Test
    public void testJsonParsingErrorFix() {
        System.out.println("=== 测试JSON解析错误修复 ===");
        
        // 模拟有问题的JSON字符串（包含未正确转义的换行符）
        String problematicJson = "{\n" +
                "  \"addresses\": [\n" +
                "    {\n" +
                "      \"sender\": \"wxid_zny9r7neph6p22\",\n" +
                "      \"original_content\": \"冯学传，山东省淄博市博山区城东街道青龙山颜山花园东苑1号楼3单元401室。\",\n" +
                "      \"is_real_address\": true,\n" +
                "      \"standardized_address\": \"山东省-淄博市-博山区-城东街道青龙山颜山花园东苑1号楼3单元401室\",\n" +
                "      \"confidence\": 0.95\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        
        // 测试预处理功能（使用反射调用私有方法进行测试）
        try {
            java.lang.reflect.Method preprocessMethod = LLMAnalysisService.class
                    .getDeclaredMethod("preprocessJsonString", String.class);
            preprocessMethod.setAccessible(true);
            
            String processedJson = (String) preprocessMethod.invoke(llmAnalysisService, problematicJson);
            
            assertNotNull(processedJson);
            System.out.println("原始JSON长度: " + problematicJson.length());
            System.out.println("处理后JSON长度: " + processedJson.length());
            System.out.println("预处理成功完成");
            
            // 测试是否能够正常解析
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            
            com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(processedJson);
            
            assertNotNull(rootNode);
            assertTrue(rootNode.has("addresses"));
            
            com.fasterxml.jackson.databind.JsonNode addressesNode = rootNode.get("addresses");
            assertTrue(addressesNode.isArray());
            assertTrue(addressesNode.size() > 0);
            
            System.out.println("JSON解析成功，包含 " + addressesNode.size() + " 个地址记录");
            
        } catch (Exception e) {
            System.err.println("测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            fail("JSON解析测试失败: " + e.getMessage());
        }
        
        System.out.println("=== JSON解析错误修复测试完成 ===");
    }
    
    /**
     * 测试转义字符处理
     */
    @Test
    public void testEscapeCharacterHandling() {
        System.out.println("=== 测试转义字符处理 ===");
        
        // 测试包含各种转义字符的字符串
        String testString = "测试字符串\\n包含换行\\t包含制表符\\\"包含引号";
        
        try {
            java.lang.reflect.Method unescapeMethod = LLMAnalysisService.class
                    .getDeclaredMethod("unescapeJsonString", String.class);
            unescapeMethod.setAccessible(true);
            
            String unescaped = (String) unescapeMethod.invoke(llmAnalysisService, testString);
            
            assertNotNull(unescaped);
            assertTrue(unescaped.contains("\n"));
            assertTrue(unescaped.contains("\t"));
            assertTrue(unescaped.contains("\""));
            
            System.out.println("原始字符串: " + testString);
            System.out.println("解转义后: " + unescaped.replace("\n", "\\n").replace("\t", "\\t"));
            System.out.println("转义字符处理测试成功");
            
        } catch (Exception e) {
            System.err.println("转义字符处理测试失败: " + e.getMessage());
            e.printStackTrace();
            fail("转义字符处理测试失败: " + e.getMessage());
        }
        
        System.out.println("=== 转义字符处理测试完成 ===");
    }
}