package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.Conversation;
import org.panjy.servicemetricsplatform.entity.Message;
import org.panjy.servicemetricsplatform.mapper.mysql.MessageMapper;
import org.panjy.servicemetricsplatform.mapper.mysql.WechatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Service
public class LLMAnalysisService {

    @Autowired
    private WechatMessageMapper wechatMessageMapper;
    
    @Autowired
    private MessageMapper messageMapper;

    private static final String QWEN_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String QWEN_MODEL = "qwen-max";

    @Value("${dashscope.apiKey:}")
    private String apiKeyProp;



    public String analyzeConversations(Date begin, Date end, String instruction) throws Exception {
        List<Conversation> conversations = wechatMessageMapper.findConversationsByDate(begin, end);
        StringBuilder sb = new StringBuilder();
        for (Conversation c : conversations) {
            sb.append("# ").append(c.getWechatId()).append(" ").append(c.getDate()).append('\n');
            if (c.getMessages() != null) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                c.getMessages().forEach(m -> sb
                        .append('[').append(m.getChatTime() == null ? "" : m.getChatTime().format(dtf)).append("] ")
                        .append(m.getSender()).append(": ")
                        .append(m.getMessage())
                        .append('\n'));
            }
            sb.append('\n');
        }

        String content = (instruction == null || instruction.isBlank())
                ? "请从以下聊天记录中提炼主题、用户诉求、情绪倾向，并给出关键洞察与建议。"
                : instruction;

        String payload = "{\n" +
                "  \"model\": \"" + QWEN_MODEL + "\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"你是资深数据分析师，擅长从聊天中提炼洞察。\"},\n" +
                "    {\"role\": \"user\", \"content\": " + jsonEscape(content + "\n\n" + sb.toString()) + "}\n" +
                "  ]\n" +
                "}";

        String apiKey = apiKeyProp.trim();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("未配置百炼 API Key：请在 application.yml 设置 dashscope.apiKey，或设置环境变量 DASHSCOPE_API_KEY，或填写 DEFAULT_API_KEY 常量（不安全）");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QWEN_API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json; charset=utf-8")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            // 简单提取返回文本（兼容 OpenAI 格式）
            String body = resp.body();
            int idx = body.indexOf("\"content\":");
            if (idx > 0) {
                int start = body.indexOf('"', idx + 10);
                int endIdx = body.indexOf('"', start + 1);
                if (start > 0 && endIdx > start) {
                    return body.substring(start + 1, endIdx).replace("\\n", "\n");
                }
            }
            return body;
        }
        throw new RuntimeException("Qwen API 调用失败: status=" + resp.statusCode() + ", body=" + resp.body());
    }

    public String analyzeConversation(Conversation conversation, String instruction) throws Exception {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (conversation != null) {
            sb.append("# ").append(conversation.getWechatId()).append(" ").append(conversation.getDate()).append('\n');
            if (conversation.getMessages() != null) {
                conversation.getMessages().forEach(m -> sb
                        .append('[')
                        .append(m.getChatTime() == null ? "" : m.getChatTime().format(dtf))
                        .append("] ")
                        .append(m.getSender()).append(": ")
                        .append(m.getMessage())
                        .append('\n'));
            }
        }

        String content = (instruction == null || instruction.isBlank())
                ? "请从以下聊天记录中提炼主题、用户诉求、情绪倾向，并给出关键洞察与建议。"
                : instruction;

        String payload = "{\n" +
                "  \"model\": \"" + QWEN_MODEL + "\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"你是资深数据分析师，擅长从聊天中提炼洞察。\"},\n" +
                "    {\"role\": \"user\", \"content\": " + jsonEscape(content + "\n\n" + sb.toString()) + "}\n" +
                "  ]\n" +
                "}";

        System.out.println(payload);

        String apiKey = apiKeyProp.trim();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("未配置百炼 API Key：请在 application.yml 设置 dashscope.apiKey，或设置环境变量 DASHSCOPE_API_KEY，或填写 DEFAULT_API_KEY 常量（不安全）");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QWEN_API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json; charset=utf-8")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            String body = resp.body();
            int idx = body.indexOf("\"content\":");
            if (idx > 0) {
                int start = body.indexOf('"', idx + 10);
                int endIdx = body.indexOf('"', start + 1);
                if (start > 0 && endIdx > start) {
                    return body.substring(start + 1, endIdx).replace("\\n", "\n");
                }
            }
            return body;
        }
        throw new RuntimeException("Qwen API 调用失败: status=" + resp.statusCode() + ", body=" + resp.body());
    }

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
     * 使用大模型分析消息中的地址信息
     * 默认批量大小为10
     * @param messages 消息列表
     * @return 分析结果的JSON格式字符串
     */
    public String analyzeAddressWithLLM(List<Message> messages) throws Exception {
        return analyzeAddressWithLLM(messages, 10);
    }

    /**
     * 使用大模型分析消息中的地址信息
     * @param messages 消息列表
     * @param batchSize 批量处理大小
     * @return 分析结果的JSON格式字符串
     */
    public String analyzeAddressWithLLM(List<Message> messages, int batchSize) throws Exception {
        if (messages == null || messages.isEmpty()) {
            return "{\"results\": [], \"total\": 0, \"message\": \"没有输入消息\"}";
        }

        List<String> allResults = new ArrayList<>();
        int total = messages.size();
        int processedCount = 0;

        // 分批处理消息
        for (int i = 0; i < messages.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, messages.size());
            List<Message> batch = messages.subList(i, endIndex);
            
            try {
                String batchResult = processBatchAddress(batch, i / batchSize + 1);
                allResults.add(batchResult);
                processedCount += batch.size();
                
                System.out.println("已处理批次 " + (i / batchSize + 1) + ", 消息数: " + batch.size());
                
                // 批次间添加短暂延迟，避免API限流
                if (i + batchSize < messages.size()) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.err.println("处理批次 " + (i / batchSize + 1) + " 时发生错误: " + e.getMessage());
                // 继续处理下一批次
                allResults.add("{\"batch_error\": \"处理失败: " + e.getMessage() + "\"}");
            }
        }

        // 合并所有批次结果
        return mergeAddressResults(allResults, total, processedCount);
    }

    /**
     * 处理单批次消息的地址分析
     * @param batch 消息批次
     * @param batchNumber 批次号
     * @return 该批次的分析结果
     */
    private String processBatchAddress(List<Message> batch, int batchNumber) throws Exception {
        StringBuilder messageContent = new StringBuilder();
        messageContent.append("请分析以下消息中的地址信息，判断是否为真实地址，并标准化格式。\n\n");
        messageContent.append("要求：\n");
        messageContent.append("1. 只提取真实存在的地理位置信息\n");
        messageContent.append("2. 将地址标准化为：省/直辖市-市/区-县/区-具体位置的格式\n");
        messageContent.append("3. 如果信息不完整，在返回能确定部分的前提下，尽量匹配上级\n");
        messageContent.append("4. 必须以JSON格式返回，格式如下：\n");
        messageContent.append("{\"addresses\": [{\"sender\": \"发送者\", \"original_content\": \"原始内容\", \"is_real_address\": true/false, \"standardized_address\": \"标准地址\", \"confidence\": 0.0-1.0}]}\n\n");
        messageContent.append("消息列表：\n");
        
        for (int i = 0; i < batch.size(); i++) {
            Message msg = batch.get(i);
            messageContent.append("消息 ").append(i + 1).append(":\n");
            messageContent.append("发送者: ").append(msg.getSender() != null ? msg.getSender() : "未知").append("\n");
            messageContent.append("内容: ").append(msg.getMessage() != null ? msg.getMessage() : "无内容").append("\n");
            messageContent.append("\n");
        }

        String instruction = messageContent.toString();

        String payload = "{\n" +
                "  \"model\": \"" + QWEN_MODEL + "\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"你是专业的地址识别和标准化专家，擅长从文本中提取和标准化地理位置信息。\"},\n" +
                "    {\"role\": \"user\", \"content\": " + jsonEscape(instruction) + "}\n" +
                "  ]\n" +
                "}";

        String apiKey = apiKeyProp.trim();
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("未配置百炼 API Key：请在 application.yml 设置 dashscope.apiKey");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(QWEN_API_URL))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json; charset=utf-8")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(payload, StandardCharsets.UTF_8))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            String body = resp.body();
            
            // 尝试提取JSON响应中的content部分
            String extractedContent = extractContentFromResponse(body);
            
            // 验证返回的是否为有效JSON
            if (isValidJson(extractedContent)) {
                // 如果包含markdown代码块，进一步提取纯正JSON
                String cleanJson = extractJsonFromContent(extractedContent);
                if (cleanJson != null && !cleanJson.isEmpty()) {
                    return cleanJson;
                } else {
                    return extractedContent;
                }
            } else {
                // 如果不是有效JSON，包装成错误格式
                return "{\"batch_number\": " + batchNumber + ", \"error\": \"AI返回格式错误\", \"raw_response\": " + jsonEscape(extractedContent) + "}";
            }
        } else {
            throw new RuntimeException("Qwen API 调用失败: status=" + resp.statusCode() + ", body=" + resp.body());
        }
    }

    /**
     * 从API响应中提取content内容
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
                    content = content.replace("\\n", "\n")
                                    .replace("\\\\", "\\")
                                    .replace("\\\"", "\"");
                    
                    // 尝试从内容中提取JSON（处理markdown代码块格式）
                    String extractedJson = extractJsonFromContent(content);
                    if (extractedJson != null && !extractedJson.isEmpty()) {
                        return extractedJson;
                    }
                    
                    return content;
                }
            }
            return response;
        } catch (Exception e) {
            System.err.println("提取响应内容时发生错误: " + e.getMessage());
            return response;
        }
    }

    /**
     * 从内容中提取JSON格式数据（处理markdown代码块等格式）
     * @param content 内容字符串
     * @return 提取的JSON字符串
     */
    private String extractJsonFromContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }
        
        // 处理markdown代码块格式
        String cleanContent = content.trim();
        
        // 移除markdown代码块标记
        if (cleanContent.startsWith("```json")) {
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
        
        // 移除其他可能的代码块标记
        if (cleanContent.startsWith("```")) {
            int startIndex = cleanContent.indexOf("\n");
            if (startIndex != -1) {
                cleanContent = cleanContent.substring(startIndex + 1);
            }
        }
        
        cleanContent = cleanContent.trim();
        
        // 尝试找到JSON对象的开始和结束
        int jsonStart = cleanContent.indexOf("{");
        if (jsonStart != -1) {
            // 从后往前找到最后一个}
            int jsonEnd = cleanContent.lastIndexOf("}");
            if (jsonEnd > jsonStart) {
                String jsonPart = cleanContent.substring(jsonStart, jsonEnd + 1);
                
                // 验证是否为有效JSON格式
                if (isValidJsonStructure(jsonPart)) {
                    return jsonPart;
                }
            }
        }
        
        return null;
    }

    /**
     * 验证JSON结构的有效性（简单验证）
     * @param jsonString JSON字符串
     * @return 是否为有效的JSON结构
     */
    private boolean isValidJsonStructure(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = jsonString.trim();
        
        // 检查是否以{开始和}结束
        if (!trimmed.startsWith("{") || !trimmed.endsWith("}")) {
            return false;
        }
        
        // 简单检查括号匹配
        int braceCount = 0;
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < trimmed.length(); i++) {
            char c = trimmed.charAt(i);
            
            if (escaped) {
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                escaped = true;
                continue;
            }
            
            if (c == '"') {
                inString = !inString;
                continue;
            }
            
            if (!inString) {
                if (c == '{') {
                    braceCount++;
                } else if (c == '}') {
                    braceCount--;
                }
            }
        }
        
        return braceCount == 0;
    }

    /**
     * 找到匹配的引号位置（处理转义字符）
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

    /**
     * 验证字符串是否为有效的JSON格式
     * @param jsonString 待验证的字符串
     * @return 是否为有效JSON
     */
    private boolean isValidJson(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return false;
        }
        
        // 先尝试从内容中提取JSON
        String extractedJson = extractJsonFromContent(jsonString);
        if (extractedJson != null && !extractedJson.isEmpty()) {
            return isValidJsonStructure(extractedJson);
        }
        
        // 如果不能提取，直接验证原字符串
        return isValidJsonStructure(jsonString);
    }

    /**
     * 合并所有批次的分析结果
     * @param batchResults 各批次结果列表
     * @param total 总消息数
     * @param processed 已处理消息数
     * @return 合并后的JSON结果
     */
    private String mergeAddressResults(List<String> batchResults, int total, int processed) {
        StringBuilder finalResult = new StringBuilder();
        finalResult.append("{\n");
        finalResult.append("  \"summary\": {\n");
        finalResult.append("    \"total_messages\": ").append(total).append(",\n");
        finalResult.append("    \"processed_messages\": ").append(processed).append(",\n");
        finalResult.append("    \"batch_count\": ").append(batchResults.size()).append(",\n");
        finalResult.append("    \"analysis_time\": \"").append(java.time.LocalDateTime.now()).append("\"\n");
        finalResult.append("  },\n");
        finalResult.append("  \"batch_results\": [\n");
        
        for (int i = 0; i < batchResults.size(); i++) {
            finalResult.append("    ").append(batchResults.get(i));
            if (i < batchResults.size() - 1) {
                finalResult.append(",");
            }
            finalResult.append("\n");
        }
        
        finalResult.append("  ]\n");
        finalResult.append("}");
        
        return finalResult.toString();
    }
}


