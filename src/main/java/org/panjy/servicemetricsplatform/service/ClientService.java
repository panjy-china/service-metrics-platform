package org.panjy.servicemetricsplatform.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.panjy.servicemetricsplatform.entity.Client;
import org.panjy.servicemetricsplatform.mapper.ClientMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 客户信息业务逻辑服务类
 * 
 * @author System Generated
 */
@Service
public class ClientService {

    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientMapper clientMapper;
    
    private static final String QWEN_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String QWEN_MODEL = "qwen-max";
    
    @Value("${dashscope.apiKey:}")
    private String apiKeyProp;
    
    // 构造函数
    public ClientService(ClientMapper clientMapper) {
        this.clientMapper = clientMapper;
    }

    /**
     * 查询备注信息有效的客户列表
     * 筛选条件：
     * 1. 备注不为空
     * 2. 备注长度>=20字符
     * 3. 备注不包含"成单"关键词
     * 4. 备注不包含"下单"关键词
     * 
     * @return 符合条件的客户列表
     */
    public List<Client> getClientsWithValidDemo() {
        try {
            log.info("开始查询备注信息有效的客户列表");
            
            List<Client> clients = clientMapper.selectClientsWithValidDemo();
            
            log.info("查询备注信息有效的客户列表完成，共找到{}条记录", 
                    clients != null ? clients.size() : 0);
            
            return clients;
            
        } catch (Exception e) {
            log.error("查询备注信息有效的客户列表失败", e);
            throw new RuntimeException("查询客户列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 使用大模型对客户备注进行批量分析
     * 分析内容包括：性别、年龄、身高、体重
     * 注意：备注中年龄、身高、体重如果出现了一定是按序的
     * 身高单位可能为米或厘米，体重可能为千克或斤
     * 若体重没有单位默认为斤，身高由大模型判断
     * 若性别不存在则返回"未知"
     * 
     * @return 分析结果对象，包含分析后的客户列表和更新统计
     */
    public AnalysisResult analyzeClientDemoWithLLM() {
        try {
            log.info("开始使用大模型批量分析客户备注信息");
            
            // 1. 获取备注信息有效的客户列表
            List<Client> clients = getClientsWithValidDemo();
            
            if (clients == null || clients.isEmpty()) {
                log.warn("没有找到符合条件的客户备注信息");
                return new AnalysisResult(new ArrayList<>(), 0, 0, 0);
            }
            
            log.info("找到{}条符合条件的客户备注，开始大模型分析", clients.size());
            
            // 2. 分批处理客户备注
            int batchSize = 5; // 每批处理5个客户，避免API超时
            List<Client> analyzedClients = new ArrayList<>();
            int totalUpdatedCount = 0;
            int successfulBatches = 0;
            
            for (int i = 0; i < clients.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, clients.size());
                List<Client> batch = clients.subList(i, endIndex);
                
                try {
                    List<Client> batchResult = processBatchClientDemo(batch, i / batchSize + 1);
                    
                    // 立即更新数据库，采用重试机制
                    int updatedCount = updateBatchToDatabase(batchResult, i / batchSize + 1);
                    totalUpdatedCount += updatedCount;
                    successfulBatches++;
                    
                    analyzedClients.addAll(batchResult);
                    
                    log.info("已处理批次 {}, 客户数: {}, 分析结果数: {}, 数据库更新: {}", 
                            i / batchSize + 1, batch.size(), batchResult.size(), updatedCount);
                    
                    // 批次间添加延迟，避免API限流
                    if (i + batchSize < clients.size()) {
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    log.error("处理批次 {} 时发生错误: {}", i / batchSize + 1, e.getMessage(), e);
                    // 继续处理下一批次
                }
            }
            
            log.info("客户备注大模型分析完成，总客户数: {}, 成功分析: {}, 数据库更新: {}, 成功批次: {}/{}", 
                    clients.size(), analyzedClients.size(), totalUpdatedCount, successfulBatches, (clients.size() + batchSize - 1) / batchSize);
            
            return new AnalysisResult(analyzedClients, clients.size(), totalUpdatedCount, successfulBatches);
            
        } catch (Exception e) {
            log.error("客户备注大模型分析失败", e);
            throw new RuntimeException("客户备注分析失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 分析结果对象
     */
    public static class AnalysisResult {
        private final List<Client> analyzedClients;
        private final int totalClients;
        private final int updatedCount;
        private final int successfulBatches;
        
        public AnalysisResult(List<Client> analyzedClients, int totalClients, int updatedCount, int successfulBatches) {
            this.analyzedClients = analyzedClients;
            this.totalClients = totalClients;
            this.updatedCount = updatedCount;
            this.successfulBatches = successfulBatches;
        }
        
        public List<Client> getAnalyzedClients() {
            return analyzedClients;
        }
        
        public int getTotalClients() {
            return totalClients;
        }
        
        public int getUpdatedCount() {
            return updatedCount;
        }
        
        public int getSuccessfulBatches() {
            return successfulBatches;
        }
    }
    
    /**
     * 将批次分析结果更新到数据库，采用重试机制
     * 
     * @param analyzedClients 分析后的客户列表
     * @param batchNumber 批次号
     * @return 更新的记录数
     */
    private int updateBatchToDatabase(List<Client> analyzedClients, int batchNumber) {
        log.info("准备更新批次 {} 到数据库，客户数: {}", batchNumber, 
                analyzedClients != null ? analyzedClients.size() : 0);
        
        if (analyzedClients == null || analyzedClients.isEmpty()) {
            log.warn("批次 {} 没有数据需要更新", batchNumber);
            return 0;
        }
        
        int maxRetries = 3;
        int baseDelay = 2000; // 2秒基础延迟
        
        for (int retry = 1; retry <= maxRetries; retry++) {
            try {
                log.info("尝试第 {} 次更新批次 {} 到数据库，客户数: {}", 
                        retry, batchNumber, analyzedClients.size());
                
                // 执行批量更新
                int updatedCount = 0;
                
                // 逐个更新，避免ClickHouse批量更新的限制
                for (Client client : analyzedClients) {
                    try {
                        log.debug("更新客户 {}: 性别={}, 年龄={}, 身高={}, 体重={}", 
                                client.getColCltID(), client.getColGender(), 
                                client.getColAge(), client.getColHeight(), client.getColWeight());
                        
                        int result = clientMapper.updateClientAnalysisResult(client);
                        if (result > 0) {
                            updatedCount++;
                            log.debug("客户 {} 更新成功", client.getColCltID());
                        } else {
                            log.warn("客户 {} 更新无影响行数", client.getColCltID());
                        }
                    } catch (Exception e) {
                        log.warn("更新客户 {} 失败: {}", client.getColCltID(), e.getMessage());
                    }
                }
                
                log.info("批次 {} 数据库更新完成，尝试更新: {} 条，实际更新: {} 条", 
                        batchNumber, analyzedClients.size(), updatedCount);
                
                return updatedCount;
                
            } catch (Exception e) {
                log.error("批次 {} 第 {} 次数据库更新失败: {}", batchNumber, retry, e.getMessage(), e);
                
                if (retry < maxRetries) {
                    // 递增延迟：2秒、4秒、6秒
                    int delay = baseDelay * retry;
                    try {
                        log.info("等待 {} 毫秒后重试...", delay);
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("重试等待被中断", ie);
                    }
                } else {
                    // 最后一次重试失败
                    log.error("批次 {} 数据库更新失败，已经重试 {} 次", batchNumber, maxRetries);
                    throw new RuntimeException("批次数据库更新失败: " + e.getMessage(), e);
                }
            }
        }
        
        return 0;
    }

    /**
     * 处理单批次客户的备注分析
     * 
     * @param batch 客户批次
     * @param batchNumber 批次号
     * @return 分析后的客户列表
     */
    private List<Client> processBatchClientDemo(List<Client> batch, int batchNumber) throws Exception {
        log.info("开始处理批次 {}，客户数: {}", batchNumber, batch.size());
        
        // 记录批次中的所有客户ID
        log.debug("批次 {} 中的客户ID列表:", batchNumber);
        for (int i = 0; i < batch.size(); i++) {
            Client client = batch.get(i);
            log.debug("  客户 {}: ID='{}', 姓名='{}'", i+1, client.getColCltID(), client.getColName());
        }
        
        // 1. 构建分析请求
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("请分析以下客户备注信息，提取出性别、年龄、身高、体重信息。\n\n");
        promptBuilder.append("要求：\n");
        promptBuilder.append("1. 备注中年龄、身高、体重如果出现了一定是按序的\n");
        promptBuilder.append("2. 身高单位可能为米或厘米，请判断并转换为厘米\n");
        promptBuilder.append("3. 体重可能为千克或斤，若没有单位默认为斤，请转换为千克\n");
        promptBuilder.append("4. 若性别不存在则返回\"未知\"\n");
        promptBuilder.append("5. 必须以JSON格式返回，格式如下：\n");
        promptBuilder.append("{\"clients\": [{\"client_id\": \"客户ID\", \"gender\": \"性别或null\", \"age\": 年龄数字或null, \"height_cm\": 身高厘米数字或null, \"weight_kg\": 体重千克数字或null}]}\n\n");
        promptBuilder.append("客户备注信息：\n");
        
        for (int i = 0; i < batch.size(); i++) {
            Client client = batch.get(i);
            promptBuilder.append("客户 ").append(i + 1).append(":\n");
            promptBuilder.append("客户ID: ").append(client.getColCltID()).append("\n");
            promptBuilder.append("客户姓名: ").append(client.getColName() != null ? client.getColName() : "未知").append("\n");
            promptBuilder.append("备注内容: ").append(client.getColDemo()).append("\n\n");
        }
        
        // 2. 调用大模型API
        String analysisResult = callLLMForAnalysis(promptBuilder.toString());
        log.debug("大模型API返回结果: {}", analysisResult.length() > 200 ? analysisResult.substring(0, 200) + "..." : analysisResult);
        
        // 3. 解析结果并更新客户信息
        List<Client> result = parseAnalysisResult(analysisResult, batch);
        log.info("批次 {} 处理完成，返回客户数: {}", batchNumber, result.size());
        
        return result;
    }
    
    /**
     * 调用大模型API进行分析
     * 
     * @param prompt 分析提示
     * @return 分析结果
     */
    private String callLLMForAnalysis(String prompt) throws Exception {
        String apiKey = apiKeyProp.trim();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("未配置百炼 API Key：请在 application.yml 设置 dashscope.apiKey");
        }
        
        String payload = "{\n" +
                "  \"model\": \"" + QWEN_MODEL + "\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"你是专业的客户信息分析专家，擅长从文本中提取人物的基本信息。\"},\n" +
                "    {\"role\": \"user\", \"content\": " + jsonEscape(prompt) + "}\n" +
                "  ]\n" +
                "}";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QWEN_API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json; charset=utf-8")
                .timeout(Duration.ofSeconds(90))
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();
        
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            String body = resp.body();
            return extractContentFromResponse(body);
        } else {
            throw new RuntimeException("Qwen API 调用失败: status=" + resp.statusCode() + ", body=" + resp.body());
        }
    }
    
    /**
     * 解析大模型分析结果并更新客户信息
     * 
     * @param analysisResult 大模型分析结果
     * @param originalBatch 原始客户批次
     * @return 更新后的客户列表
     */
    private List<Client> parseAnalysisResult(String analysisResult, List<Client> originalBatch) {
        List<Client> updatedClients = new ArrayList<>();
        
        try {
            log.info("开始解析大模型分析结果，原始批次客户数: {}", originalBatch.size());
            log.debug("原始分析结果内容: {}", analysisResult);
            
            // 预处理JSON字符串
            String cleanedResult = preprocessJsonString(analysisResult);
            log.debug("清理后的JSON内容: {}", cleanedResult);
            
            // 配置ObjectMapper
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            
            JsonNode rootNode = objectMapper.readTree(cleanedResult);
            JsonNode clientsNode = rootNode.get("clients");
            
            if (clientsNode != null && clientsNode.isArray()) {
                log.info("解析到的客户数: {}", clientsNode.size());
                
                for (JsonNode clientNode : clientsNode) {
                    String clientId = clientNode.get("client_id") != null ? 
                                    clientNode.get("client_id").asText() : null;
                    
                    log.debug("处理客户ID: {}", clientId);
                    
                    if (clientId != null) {
                        // 在原始批次中查找对应的客户
                        Client originalClient = findClientById(clientId.trim(), originalBatch);
                        if (originalClient != null) {
                            log.debug("找到匹配的原始客户: {}", clientId);
                            // 创建新的客户对象，包含分析结果
                            Client analyzedClient = createAnalyzedClient(originalClient, clientNode);
                            updatedClients.add(analyzedClient);
                            log.debug("添加分析后的客户: {}", clientId);
                        } else {
                            log.warn("未在原始批次中找到客户ID: {}", clientId);
                        }
                    } else {
                        log.warn("客户节点中未找到client_id字段");
                    }
                }
            } else {
                log.warn("未找到clients数组或clients不是数组");
            }
            
            log.info("解析完成，生成分析后客户数: {}", updatedClients.size());
            
        } catch (Exception e) {
            log.error("解析大模型分析结果失败", e);
            log.error("原始分析结果内容: {}", analysisResult);
            // 如果解析失败，返回原始客户列表，但添加错误标记
            for (Client client : originalBatch) {
                Client errorClient = copyClient(client);
                errorClient.setColDemo(client.getColDemo() + " [分析失败: " + e.getMessage() + "]");
                updatedClients.add(errorClient);
            }
        }
        
        return updatedClients;
    }
    
    /**
     * 查询客户性别分布
     * 
     * @return 性别分布数据列表，包含性别和对应人数
     */
    public List<Map<String, Object>> getGenderDistribution() {
        try {
            log.info("开始查询客户性别分布");
            
            List<Map<String, Object>> genderDistribution = clientMapper.selectGenderDistribution();
            
            log.info("查询客户性别分布完成，共找到{}条记录", 
                    genderDistribution != null ? genderDistribution.size() : 0);
            
            return genderDistribution;
            
        } catch (Exception e) {
            log.error("查询客户性别分布失败", e);
            throw new RuntimeException("查询性别分布失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 查询客户年龄分布
     * 
     * @return 年龄分布数据列表，包含年龄段和对应人数
     */
    public List<Map<String, Object>> getAgeDistribution() {
        try {
            log.info("开始查询客户年龄分布");
            
            List<Map<String, Object>> ageDistribution = clientMapper.selectAgeDistribution();
            if (ageDistribution != null && ageDistribution.size() > 0) {
                ageDistribution = ageDistribution.subList(1, ageDistribution.size());
            }
            log.info("查询客户年龄分布完成，共找到{}条记录", 
                    ageDistribution != null ? ageDistribution.size() : 0);
            
            return ageDistribution;
            
        } catch (Exception e) {
            log.error("查询客户年龄分布失败", e);
            throw new RuntimeException("查询年龄分布失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 查询客户体重分布
     * 
     * @return 体重分布数据列表，包含体重范围和对应人数
     */
    public List<Map<String, Object>> getWeightDistribution() {
        try {
            log.info("开始查询客户体重分布");
            
            List<Map<String, Object>> weightDistribution = clientMapper.selectWeightDistribution();
            if(weightDistribution != null && weightDistribution.size() > 0){
                weightDistribution = weightDistribution.subList(1, weightDistribution.size());
            }
            log.info("查询客户体重分布完成，共找到{}条记录", 
                    weightDistribution != null ? weightDistribution.size() : 0);
            
            return weightDistribution;
            
        } catch (Exception e) {
            log.error("查询客户体重分布失败", e);
            throw new RuntimeException("查询体重分布失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 查询客户身高分布
     * 
     * @return 身高分布数据列表，包含身高范围和对应人数
     */
    public List<Map<String, Object>> getHeightDistribution() {
        try {
            log.info("开始查询客户身高分布");
            
            List<Map<String, Object>> heightDistribution = clientMapper.selectHeightDistribution();
            if(heightDistribution != null && heightDistribution.size() > 0){
                heightDistribution = heightDistribution.subList(1, heightDistribution.size());
            }
            log.info("查询客户身高分布完成，共找到{}条记录", 
                    heightDistribution != null ? heightDistribution.size() : 0);
            
            return heightDistribution;
            
        } catch (Exception e) {
            log.error("查询客户身高分布失败", e);
            throw new RuntimeException("查询身高分布失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 根据客户ID在批次中查找客户
     * 
     * @param clientId 客户ID
     * @param batch 客户批次
     * @return 找到的客户或null
     */
    private Client findClientById(String clientId, List<Client> batch) {
        log.info("在批次中查找客户ID: {}", clientId);
        for (Client client : batch) {
            log.info("比较客户ID: {} vs {}", clientId, client.getColCltID());
            if (clientId.equals(client.getColCltID().trim())) {
                log.info("找到匹配客户: {}", clientId);
                return client;
            }
        }
        log.info("未找到客户ID: {}", clientId);
        return null;
    }
    
    /**
     * 创建包含分析结果的客户对象
     * 
     * @param originalClient 原始客户对象
     * @param analysisNode 分析结果节点
     * @return 包含分析结果的客户对象
     */
    private Client createAnalyzedClient(Client originalClient, JsonNode analysisNode) {
        Client analyzedClient = copyClient(originalClient);
        
        // 提取分析结果
        String gender = analysisNode.get("gender") != null ? 
                       analysisNode.get("gender").asText() : "未知";
        Integer age = analysisNode.get("age") != null && !analysisNode.get("age").isNull() ? 
                     analysisNode.get("age").asInt() : null;
        Integer heightCm = analysisNode.get("height_cm") != null && !analysisNode.get("height_cm").isNull() ? 
                          analysisNode.get("height_cm").asInt() : null;
        Double weightKg = analysisNode.get("weight_kg") != null && !analysisNode.get("weight_kg").isNull() ? 
                         analysisNode.get("weight_kg").asDouble() : null;
        String analysisNotes = analysisNode.get("analysis_notes") != null ? 
                              analysisNode.get("analysis_notes").asText() : "";
        
        log.info("解析客户 {} 信息 - 性别: {}, 年龄: {}, 身高: {}, 体重: {}", 
                originalClient.getColCltID(), gender, age, heightCm, weightKg);
        
        // 更新客户信息
        analyzedClient.setColGender(gender);
        if (age != null) {
            analyzedClient.setColAge(age);
        }
        if (heightCm != null) {
            analyzedClient.setColHeight(heightCm);
        }
        if (weightKg != null) {
            // 转换为整数（千克）
            analyzedClient.setColWeight(weightKg.intValue());
        }
        
        // 在备注中添加分析结果
        String originalDemo = originalClient.getColDemo();
        String enhancedDemo = originalDemo + " [AI分析: 性别=" + gender;
        if (age != null) enhancedDemo += ", 年龄=" + age;
        if (heightCm != null) enhancedDemo += ", 身高=" + heightCm + "cm";
        if (weightKg != null) enhancedDemo += ", 体重=" + String.format("%.1f", weightKg) + "kg";
        if (!analysisNotes.isEmpty()) enhancedDemo += ", 说明=" + analysisNotes;
        enhancedDemo += "]";
        
        analyzedClient.setColDemo(enhancedDemo);
        
        log.info("创建分析后的客户对象: {} - 性别={}, 年龄={}, 身高={}, 体重={}", 
                analyzedClient.getColCltID(), gender, age, heightCm, weightKg);
        
        return analyzedClient;
    }
    
    /**
     * 复制客户对象
     * 
     * @param original 原始客户对象
     * @return 复制的客户对象
     */
    private Client copyClient(Client original) {
        Client client = new Client();
        client.setColCltID(original.getColCltID());
        client.setColEmpID(original.getColEmpID());
        client.setColDptID(original.getColDptID());
        client.setColPhs(original.getColPhs());
        client.setColName(original.getColName());
        client.setColGender(original.getColGender());
        client.setColAddress(original.getColAddress());
        client.setColProvince(original.getColProvince());
        client.setColCity(original.getColCity());
        client.setColAge(original.getColAge());
        client.setColZip(original.getColZip());
        client.setColQQ(original.getColQQ());
        client.setColMSN(original.getColMSN());
        client.setColDemo(original.getColDemo());
        client.setColCrtTim(original.getColCrtTim());
        client.setColEnable(original.getColEnable());
        client.setColLevel(original.getColLevel());
        client.setColMediaID(original.getColMediaID());
        client.setCollastlogintime(original.getCollastlogintime());
        client.setColProfession(original.getColProfession());
        client.setColActivity(original.getColActivity());
        client.setColRecommend(original.getColRecommend());
        client.setColHeight(original.getColHeight());
        client.setColWeight(original.getColWeight());
        client.setColWaist(original.getColWaist());
        client.setColBust(original.getColBust());
        client.setColCataLog(original.getColCataLog());
        client.setColArea(original.getColArea());
        client.setColBloodType(original.getColBloodType());
        client.setColHealth(original.getColHealth());
        client.setColIncome(original.getColIncome());
        client.setColAttrClass(original.getColAttrClass());
        return client;
    }
    
    /**
     * JSON字符串转义
     * 
     * @param s 原始字符串
     * @return 转义后的JSON字符串
     */
    private static String jsonEscape(String s) {
        StringBuilder out = new StringBuilder("\"");
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': out.append("\\\""); break;
                case '\\': out.append("\\\\"); break;
                case '\n': out.append("\\n"); break;
                case '\r': out.append("\\r"); break;
                case '\t': out.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        out.append(String.format("\\u%04x", (int) c));
                    } else {
                        out.append(c);
                    }
            }
        }
        out.append('"');
        return out.toString();
    }
    
    /**
     * 从API响应中提取content内容
     * 
     * @param response API响应字符串
     * @return 提取的content内容
     */
    private String extractContentFromResponse(String response) {
        try {
            int idx = response.indexOf("\"content\":");
            if (idx > 0) {
                int start = response.indexOf('"', idx + 10);
                int endIdx = findMatchingQuote(response, start + 1);
                if (start > 0 && endIdx > start) {
                    String content = response.substring(start + 1, endIdx);
                    // 处理转义字符
                    content = unescapeJsonString(content);
                    
                    // 尝试从内容中提取JSON
                    String extractedJson = extractJsonFromContent(content);
                    if (extractedJson != null && !extractedJson.isEmpty()) {
                        return extractedJson;
                    }
                    
                    return content;
                }
            }
            return response;
        } catch (Exception e) {
            log.error("提取响应内容时发生错误", e);
            return response;
        }
    }
    
    /**
     * 预处理JSON字符串
     * 
     * @param jsonString 原始JSON字符串
     * @return 清理后的JSON字符串
     */
    private String preprocessJsonString(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return jsonString;
        }
        
        String cleaned = jsonString;
        
        try {
            // 提取JSON部分
            String extractedJson = extractJsonFromContent(cleaned);
            if (extractedJson != null && !extractedJson.isEmpty()) {
                cleaned = extractedJson;
            }
            
            // 处理转义问题
            if (cleaned.contains("\\\\")) {
                cleaned = cleaned.replace("\\\\", "\\");
            }
            
            // 修复引号问题
            cleaned = cleaned.replace("“", "\"").replace("”", "\"");
            
        } catch (Exception e) {
            log.error("预处理JSON字符串时发生错误", e);
            return jsonString;
        }
        
        return cleaned;
    }
    
    /**
     * 从内容中提取JSON格式数据
     * 
     * @param content 内容字符串
     * @return 提取的JSON字符串
     */
    private String extractJsonFromContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }
        
        String cleanContent = content.trim();
        
        // 移除代码块标记
        if (cleanContent.startsWith("```json") || cleanContent.startsWith("```")) {
            int startIndex = cleanContent.indexOf("\n");
            if (startIndex != -1) {
                cleanContent = cleanContent.substring(startIndex + 1);
            }
        }
        
        if (cleanContent.endsWith("```")) {
            int endIndex = cleanContent.lastIndexOf("```");
            if (endIndex != -1) {
                cleanContent = cleanContent.substring(0, endIndex);
            }
        }
        
        cleanContent = cleanContent.trim();
        
        // 找到JSON对象
        int jsonStart = cleanContent.indexOf("{");
        if (jsonStart != -1) {
            int jsonEnd = cleanContent.lastIndexOf("}");
            if (jsonEnd > jsonStart) {
                return cleanContent.substring(jsonStart, jsonEnd + 1);
            }
        }
        
        return null;
    }
    
    /**
     * 解转义JSON字符串
     * 
     * @param jsonString 包含转义字符的JSON字符串
     * @return 解转后的字符串
     */
    private String unescapeJsonString(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return jsonString;
        }
        
        StringBuilder result = new StringBuilder();
        boolean escaped = false;
        
        for (int i = 0; i < jsonString.length(); i++) {
            char c = jsonString.charAt(i);
            
            if (escaped) {
                switch (c) {
                    case 'n': result.append('\n'); break;
                    case 'r': result.append('\r'); break;
                    case 't': result.append('\t'); break;
                    case 'b': result.append('\b'); break;
                    case 'f': result.append('\f'); break;
                    case '"': result.append('"'); break;
                    case '\\': result.append('\\'); break;
                    case '/': result.append('/'); break;
                    default: result.append('\\').append(c); break;
                }
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else {
                result.append(c);
            }
        }
        
        if (escaped) {
            result.append('\\');
        }
        
        return result.toString();
    }
    
    /**
     * 找到匹配的引号位置
     * 
     * @param str 字符串
     * @param startPos 开始位置
     * @return 匹配引号的位置
     */
    private int findMatchingQuote(String str, int startPos) {
        for (int i = startPos; i < str.length(); i++) {
            if (str.charAt(i) == '"' && (i == 0 || str.charAt(i - 1) != '\\')) {
                return i;
            }
        }
        return -1;
    }

}