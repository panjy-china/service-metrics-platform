package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.*;
import org.panjy.servicemetricsplatform.mapper.MessageMapper;
import org.panjy.servicemetricsplatform.mapper.WechatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser;

@Service
public class LLMAnalysisService {

    @Autowired
    private WechatMessageMapper wechatMessageMapper;
    
    @Autowired
    private MessageMapper messageMapper;
    
    @Autowired
    private WechatMessageAnalyzeAddressService addressService;
    
    @Autowired
    private UserFirstFeedbackService userFirstFeedbackService;

    private static final String QWEN_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String QWEN_MODEL = "qwen-max";

    @Value("${dashscope.apiKey:}")
    private String apiKeyProp;

    private static final Logger logger = LoggerFactory.getLogger(LLMAnalysisService.class);

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

    /**
     * 分析指定日期之后用户首次聊天两天内的对话记录
     * @param date 限定日期
     * @return 分析结果
     */
    public String analyzeConversationsAfterDate(Date date) throws Exception {
        logger.info("开始分析指定日期之后用户首次聊天两天内的对话记录，限定日期: {}", date);

        try {
            // 1. 查询指定日期之后用户首次聊天两天内的对话记录
            logger.info("正在查询指定日期之后用户首次聊天两天内的对话记录，限定日期: {}", date);
            List<Conversation> conversations = wechatMessageMapper.findConversationsWithinTwoDaysOfFirstChatAfterDate(date);
            logger.info("查询完成，总对话记录数: {}，限定日期: {}", conversations != null ? conversations.size() : 0, date);

            if (conversations == null || conversations.isEmpty()) {
                logger.warn("未找到任何对话记录，限定日期: {}", date);
                return "{\"result\": \"未找到任何对话记录\"}";
            }

            logger.info("找到{}个用户的对话记录，开始逐一分析，限定日期: {}", conversations.size(), date);

            // 2. 逐一分析每个用户的对话记录
            List<String> analysisResults = new ArrayList<>();
            for (Conversation conversation : conversations) {
                if (conversation != null && conversation.getWechatId() != null) {
                    try {
                        logger.info("开始分析用户{}的对话记录", conversation.getWechatId());

                        // 构建分析提示
                        StringBuilder analysisPrompt = new StringBuilder();
                        analysisPrompt.append("请分析以下用户对话记录，判断用户是否发送过舌苔和体型照片：\n\n");
                        analysisPrompt.append("分析要求：\n");
                        analysisPrompt.append("1. 如果用户只发送了一张照片，认为是舌苔照片\n");
                        analysisPrompt.append("2. 如果用户发送了两张照片，认为既有舌苔照片也有体型照片\n");
                        analysisPrompt.append("3. 如果用户在对话中明确提到客服要求发送舌苔和体型照片，请特别标注\n");
                        analysisPrompt.append("4. 请以JSON格式返回分析结果，格式如下：\n");
                        analysisPrompt.append("{\n");
                        analysisPrompt.append("  \"wechatId\": \"用户微信ID\",\n");
                        analysisPrompt.append("  \"photoCount\": 照片数量,\n");
                        analysisPrompt.append("  \"hasTonguePhoto\": true/false,\n");
                        analysisPrompt.append("  \"hasBodyTypePhoto\": true/false,\n");
                        analysisPrompt.append("  \"customerServiceRequested\": true/false,\n");
                        analysisPrompt.append("  \"analysis\": \"详细分析说明\"\n");
                        analysisPrompt.append("}\n\n");
                        analysisPrompt.append("用户对话记录：\n");

                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        analysisPrompt.append("# ").append(conversation.getWechatId()).append(" ").append(conversation.getDate()).append('\n');
                        conversation.getMessages().forEach(m -> analysisPrompt
                                .append('[').append(m.getChatTime() == null ? "" : m.getChatTime().format(dtf)).append("] ")
                                .append(m.getSender()).append(": ")
                                .append(m.getMessage())
                                .append('\n'));

                        // 调用大模型进行分析
                        String payload = "{\n" +
                                "  \"model\": \"" + QWEN_MODEL + "\",\n" +
                                "  \"messages\": [\n" +
                                "    {\"role\": \"system\", \"content\": \"你是专业的客服对话分析师，擅长分析用户发送的照片类型和客服要求。\"},\n" +
                                "    {\"role\": \"user\", \"content\": " + jsonEscape(analysisPrompt.toString()) + "}\n" +
                                "  ]\n" +
                                "}";

                        String apiKey = apiKeyProp.trim();
                        if (apiKey == null || apiKey.isBlank()) {
                            logger.error("未配置百炼 API Key，用户微信ID: {}", conversation.getWechatId());
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
                            // 提取响应中的content部分
                            String extractedContent = extractContentFromResponse(body);

                            // 验证返回的是否为有效JSON
                            if (isValidJson(extractedContent)) {
                                // 如果包含代码块，进一步提取纯正JSON
                                String cleanJson = extractJsonFromContent(extractedContent);
                                String finalResult = (cleanJson != null && !cleanJson.isEmpty()) ? cleanJson : extractedContent;
                                analysisResults.add(finalResult);

                                // 保存分析结果到用户首次反馈表
                                try {
                                    saveAnalysisResultToFeedbackTable(finalResult);
                                } catch (Exception e) {
                                    logger.error("保存分析结果到用户首次反馈表时发生异常，用户微信ID: " + conversation.getWechatId(), e);
                                }
                            } else {
                                logger.warn("AI返回格式错误，用户微信ID: {}，原始响应长度: {}", conversation.getWechatId(), extractedContent.length());
                                analysisResults.add("{\"error\": \"AI返回格式错误\", \"raw_response_length\": " + extractedContent.length() + "}");
                            }
                        } else {
                            logger.error("Qwen API 调用失败，用户微信ID: {}，状态码: {}，响应体长度: {}", conversation.getWechatId(), resp.statusCode(), resp.body().length());
                            analysisResults.add("{\"error\": \"Qwen API 调用失败\", \"status_code\": " + resp.statusCode() + ", \"raw_response_length\": " + resp.body().length() + "}");
                        }
                    } catch (Exception e) {
                        logger.error("分析用户{}的对话记录时发生异常", conversation.getWechatId(), e);
                        analysisResults.add("{\"error\": \"分析用户对话记录时发生异常\", \"user_wechat_id\": \"" + conversation.getWechatId() + "\"}");
                    }
                }
            }

            logger.info("所有对话记录分析完成，开始构建最终结果");
            StringBuilder finalResult = new StringBuilder();
            finalResult.append("{\n");
            finalResult.append("  \"result\": [\n");

            for (int i = 0; i < analysisResults.size(); i++) {
                finalResult.append("    ").append(analysisResults.get(i));
                if (i < analysisResults.size() - 1) {
                    finalResult.append(",");
                }
                finalResult.append("\n");
            }

            finalResult.append("  ]\n");
            finalResult.append("}");

            return finalResult.toString();
        } catch (Exception e) {
            logger.error("分析指定日期之后用户首次聊天两天内的对话记录时发生异常，限定日期: " + date, e);
            throw e;
        }
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
     * 使用大模型分析消息中的地址信息并返回Message列表
     * 默认批量大小为10
     * @param messages 消息列表
     * @return 包含地址分析结果的Message列表
     */
    public List<Message> analyzeAddressWithLLMAsMessages(List<Message> messages) throws Exception {
        return analyzeAddressWithLLMAsMessages(messages, 10);
    }

    /**
     * 使用大模型分析消息中的地址信息并返回Message列表
     * @param messages 消息列表
     * @param batchSize 批量处理大小
     * @return 包含地址分析结果的Message列表
     */
    public List<Message> analyzeAddressWithLLMAsMessages(List<Message> messages, int batchSize) throws Exception {
        if (messages == null || messages.isEmpty()) {
            return new ArrayList<>();
        }

        List<Message> resultMessages = new ArrayList<>();
        int total = messages.size();
        int processedCount = 0;

        // 分批处理消息
        for (int i = 0; i < messages.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, messages.size());
            List<Message> batch = messages.subList(i, endIndex);
            
            try {
                List<Message> batchResult = processBatchAddressAsMessages(batch, i / batchSize + 1);
                resultMessages.addAll(batchResult);
                processedCount += batch.size();
                
                System.out.println("已处理批次 " + (i / batchSize + 1) + ", 消息数: " + batch.size() + ", 提取地址数: " + batchResult.size());
                
                // 批次间添加短暂延迟，避免API限流
                if (i + batchSize < messages.size()) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.err.println("处理批次 " + (i / batchSize + 1) + " 时发生错误: " + e.getMessage());
                // 继续处理下一批次，但记录错误信息
                e.printStackTrace();
            }
        }

        System.out.println("地址分析完成 - 总消息数: " + total + ", 已处理: " + processedCount + ", 提取地址数: " + resultMessages.size());
        return resultMessages;
    }

    /**
     * 处理单批次消息的地址分析并返回Message列表
     * @param batch 消息批次
     * @param batchNumber 批次号
     * @return 该批次提取的地址Message列表
     */
    private List<Message> processBatchAddressAsMessages(List<Message> batch, int batchNumber) throws Exception {
        String jsonResult = processBatchAddress(batch, batchNumber);
        return parseAddressJsonToMessages(jsonResult, batch);
    }

    /**
     * 将地址分析的JSON结果解析为Message列表
     * @param jsonResult LLM返回的JSON分析结果
     * @param originalBatch 原始消息批次（用于补充信息）
     * @return 解析后的Message列表
     */
    private List<Message> parseAddressJsonToMessages(String jsonResult, List<Message> originalBatch) {
        List<Message> messages = new ArrayList<>();
        
        try {
            // 预处理JSON字符串，处理控制字符和格式问题
            String cleanedJsonResult = preprocessJsonString(jsonResult);
            
            ObjectMapper objectMapper = new ObjectMapper();
            // 配置ObjectMapper以更宽松地处理JSON
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            
            JsonNode rootNode = objectMapper.readTree(cleanedJsonResult);
            
            // 处理addresses数组
            JsonNode addressesNode = rootNode.get("addresses");
            if (addressesNode != null && addressesNode.isArray()) {
                for (JsonNode addressNode : addressesNode) {
                    if (addressNode.get("is_real_address") != null && 
                        addressNode.get("is_real_address").asBoolean()) {
                        
                        String sender = addressNode.get("sender") != null ? 
                                      addressNode.get("sender").asText() : "未知";
                        String originalContent = addressNode.get("original_content") != null ? 
                                                addressNode.get("original_content").asText() : "";
                        String standardizedAddress = addressNode.get("standardized_address") != null ? 
                                                    addressNode.get("standardized_address").asText() : "";
                        double confidence = addressNode.get("confidence") != null ? 
                                          addressNode.get("confidence").asDouble() : 0.0;
                        
                        // 查找对应的原始消息获取时间信息
                        LocalDateTime chatTime = null;
                        for (Message originalMsg : originalBatch) {
                            if (sender.equals(originalMsg.getSender()) && 
                                originalContent.equals(originalMsg.getMessage())) {
                                chatTime = originalMsg.getChatTime();
                                break;
                            }
                        }
                        
                        if (chatTime == null) {
                            chatTime = LocalDateTime.now();
                        }
                        
                        // 创建新的Message对象，包含地址信息
                        Message addressMessage = new Message(sender, 
                                                "地址分析结果: " + standardizedAddress + 
                                                " (原文: " + originalContent + 
                                                ", 置信度: " + String.format("%.2f", confidence) + ")",
                                                "AddressAnalysis", 
                                                chatTime);
                        
                        messages.add(addressMessage);
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("解析地址分析JSON结果时发生错误: " + e.getMessage());
            e.printStackTrace();
            
            // 如果解析失败，创建一个错误信息的Message
            Message errorMessage = new Message("系统", 
                                              "地址分析结果解析失败: " + e.getMessage(),
                                              "Error", 
                                              LocalDateTime.now());
            messages.add(errorMessage);
        }
        
        return messages;
    }
    
    /**
     * 预处理JSON字符串，处理控制字符和格式问题
     * @param jsonString 原始JSON字符串
     * @return 清理后的JSON字符串
     */
    private String preprocessJsonString(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return jsonString;
        }
        
        String cleaned = jsonString;
        
        try {
            // 1. 首先尝试从内容中提取纯JSON部分
            String extractedJson = extractJsonFromContent(cleaned);
            if (extractedJson != null && !extractedJson.isEmpty()) {
                cleaned = extractedJson;
            }
            
            // 2. 处理过度转义的问题
            // 检查是否存在双重转义（如 \\n 应该变成 \n）
            if (cleaned.contains("\\\\")) {
                // 处理双重反斜杠转义
                cleaned = cleaned.replace("\\\\", "\\");
            }
            
            // 3. 修复可能的引号问题
            cleaned = fixQuotationMarks(cleaned);
            
            // 4. 验证和修复JSON结构
            cleaned = validateAndFixJsonStructure(cleaned);
            
            // 5. 最后尝试简单验证JSON是否可解析
            try {
                ObjectMapper testMapper = new ObjectMapper();
                testMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
                testMapper.readTree(cleaned);
                // 如果能成功解析，直接返回
                return cleaned;
            } catch (Exception parseTest) {
                // 如果解析失败，进行更深层的清理
                cleaned = deepCleanJsonString(cleaned);
            }
            
        } catch (Exception e) {
            System.err.println("预处理JSON字符串时发生错误: " + e.getMessage());
            // 如果预处理失败，返回原始字符串
            return jsonString;
        }
        
        return cleaned;
    }
    
    /**
     * 深度清理JSON字符串
     * @param jsonString JSON字符串
     * @return 清理后的JSON字符串
     */
    private String deepCleanJsonString(String jsonString) {
        String cleaned = jsonString;
        
        // 处理控制字符，但保留JSON中合法的转义序列
        StringBuilder result = new StringBuilder();
        boolean inString = false;
        boolean escaped = false;
        
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            
            if (escaped) {
                // 前一个字符是反斜杠，这个字符是转义字符
                result.append(c);
                escaped = false;
                continue;
            }
            
            if (c == '\\') {
                // 遇到反斜杠，检查下一个字符
                if (i + 1 < cleaned.length()) {
                    char nextChar = cleaned.charAt(i + 1);
                    // 如果下一个字符是合法的转义字符，保留反斜杠
                    if (nextChar == '"' || nextChar == '\\' || nextChar == 'n' || 
                        nextChar == 'r' || nextChar == 't' || nextChar == 'b' || 
                        nextChar == 'f' || nextChar == 'u' || nextChar == '/') {
                        result.append(c);
                        escaped = true;
                    }
                    // 否则跳过这个反斜杠
                } else {
                    // 如果反斜杠是最后一个字符，跳过
                }
                continue;
            }
            
            if (c == '"' && !escaped) {
                inString = !inString;
            }
            
            // 如果是控制字符且不在字符串内，跳过
            if (!inString && c < 32 && c != '\n' && c != '\r' && c != '\t') {
                continue;
            }
            
            result.append(c);
        }
        
        return result.toString();
    }
    
    /**
     * 修复引号标记问题
     * @param jsonString JSON字符串
     * @return 修复后的JSON字符串
     */
    private String fixQuotationMarks(String jsonString) {
        // 处理中文引号问题
        return jsonString
                .replace("“", "\"")
                .replace("”", "\"")
                .replace("‘", "'")
                .replace("’", "'");
    }
    
    /**
     * 验证和修复JSON结构
     * @param jsonString JSON字符串
     * @return 修复后的JSON字符串
     */
    private String validateAndFixJsonStructure(String jsonString) {
        String trimmed = jsonString.trim();
        
        // 确保JSON以{开始和}结束
        if (!trimmed.startsWith("{")) {
            int startIndex = trimmed.indexOf("{");
            if (startIndex != -1) {
                trimmed = trimmed.substring(startIndex);
            }
        }
        
        if (!trimmed.endsWith("}")) {
            int endIndex = trimmed.lastIndexOf("}");
            if (endIndex != -1) {
                trimmed = trimmed.substring(0, endIndex + 1);
            }
        }
        
        return trimmed;
    }
    
    /**
     * 分析消息中的地址信息并存储到数据库
     * @param messages 消息列表
     * @return 存储的地址分析结果数量
     */
    public int analyzeAndStoreAddresses(List<Message> messages) throws Exception {
        return analyzeAndStoreAddresses(messages, 10);
    }
    
    /**
     * 简化版本：分析消息中的地址信息并存储到数据库
     * 使用新的 executeSimple 方法进行批量存储
     * @param messages 消息列表
     * @param batchSize 批量处理大小
     * @return 存储的地址分析结果数量
     */
    public int analyzeAndStoreAddressesSimple(List<Message> messages, int batchSize) throws Exception {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }
        
        List<WechatMessageAnalyzeAddress> addressRecords = new ArrayList<>();
        int total = messages.size();
        int processedCount = 0;
        
        System.out.println("开始分析并存储地址信息（简化模式），总消息数: " + total);
        
        // 分批处理消息
        for (int i = 0; i < messages.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, messages.size());
            List<Message> batch = messages.subList(i, endIndex);
            
            try {
                List<WechatMessageAnalyzeAddress> batchAddresses = 
                    processBatchAddressForStorage(batch, i / batchSize + 1);
                addressRecords.addAll(batchAddresses);
                processedCount += batch.size();
                
                System.out.println("已处理批次 " + (i / batchSize + 1) + 
                                 ", 消息数: " + batch.size() + 
                                 ", 提取地址数: " + batchAddresses.size());
                
                // 批次间添加短暂延迟，避免API限流
                if (i + batchSize < messages.size()) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.err.println("处理批次 " + (i / batchSize + 1) + " 时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // 使用简化方法进行批量存储
        int savedCount = 0;
        if (!addressRecords.isEmpty()) {
            try {
                savedCount = addressService.executeBatchSimple(addressRecords);
                System.out.println("地址分析和存储完成（简化模式） - 总消息数: " + total + 
                                 ", 已处理: " + processedCount + 
                                 ", 提取地址数: " + addressRecords.size() + 
                                 ", 成功存储: " + savedCount);
            } catch (Exception e) {
                System.err.println("批量存储地址分析结果失败（简化模式）: " + e.getMessage());
                throw new RuntimeException("存储地址分析结果失败", e);
            }
        }
        
        return savedCount;
    }
    
    /**
     * 完整版本：分析消息中的地址信息并存储到数据库
     * 返回详细的执行结果信息
     * @param messages 消息列表
     * @param batchSize 批量处理大小
     * @return 执行结果对象，包含成功状态和详细信息
     */
    public WechatMessageAnalyzeAddressService.ExecuteResult analyzeAndStoreAddressesWithDetails(List<Message> messages, int batchSize) throws Exception {
        WechatMessageAnalyzeAddressService.ExecuteResult finalResult = new WechatMessageAnalyzeAddressService.ExecuteResult();
        
        if (messages == null || messages.isEmpty()) {
            finalResult.setSuccess(true);
            finalResult.setMessage("输入消息列表为空，无需处理");
            finalResult.setData(0);
            return finalResult;
        }
        
        try {
            List<WechatMessageAnalyzeAddress> addressRecords = new ArrayList<>();
            int total = messages.size();
            int processedCount = 0;
            List<String> processingLogs = new ArrayList<>();
            
            processingLogs.add("开始分析并存储地址信息（完整模式），总消息数: " + total);
            
            // 分批处理消息
            for (int i = 0; i < messages.size(); i += batchSize) {
                int endIndex = Math.min(i + batchSize, messages.size());
                List<Message> batch = messages.subList(i, endIndex);
                
                try {
                    List<WechatMessageAnalyzeAddress> batchAddresses = 
                        processBatchAddressForStorage(batch, i / batchSize + 1);
                    addressRecords.addAll(batchAddresses);
                    processedCount += batch.size();
                    
                    String logMsg = "已处理批次 " + (i / batchSize + 1) + 
                                   ", 消息数: " + batch.size() + 
                                   ", 提取地址数: " + batchAddresses.size();
                    processingLogs.add(logMsg);
                    System.out.println(logMsg);
                    
                    // 批次间添加短暂延迟，避免API限流
                    if (i + batchSize < messages.size()) {
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    String errorMsg = "处理批次 " + (i / batchSize + 1) + " 时发生错误: " + e.getMessage();
                    processingLogs.add("[错误] " + errorMsg);
                    System.err.println(errorMsg);
                    e.printStackTrace();
                }
            }
            
            // 使用完整方法进行批量存储
            if (!addressRecords.isEmpty()) {
                WechatMessageAnalyzeAddressService.ExecuteResult result = addressService.executeBatch(addressRecords);
                
                if (result.isSuccess()) {
                    int savedCount = (Integer) result.getData();
                    String successMsg = "地址分析和存储完成（完整模式） - 总消息数: " + total + 
                                      ", 已处理: " + processedCount + 
                                      ", 提取地址数: " + addressRecords.size() + 
                                      ", 成功存储: " + savedCount;
                    processingLogs.add(successMsg);
                    System.out.println(successMsg);
                    
                    finalResult.setSuccess(true);
                    finalResult.setMessage("地址分析和存储成功");
                    
                    // 添加详细信息到结果中
                    Map<String, Object> details = new HashMap<>();
                    details.put("totalMessages", total);
                    details.put("processedMessages", processedCount);
                    details.put("extractedAddresses", addressRecords.size());
                    details.put("savedAddresses", savedCount);
                    details.put("processingLogs", processingLogs);
                    details.put("storageResult", result);
                    
                    finalResult.setData(details);
                } else {
                    finalResult.setSuccess(false);
                    finalResult.setMessage("批量存储地址分析结果失败: " + result.getMessage());
                    finalResult.setErrorCode(result.getErrorCode());
                    finalResult.setException(result.getException());
                }
            } else {
                finalResult.setSuccess(true);
                finalResult.setMessage("没有提取到有效的地址信息");
                finalResult.setData(0);
            }
            
        } catch (Exception e) {
            finalResult.setSuccess(false);
            finalResult.setMessage("地址分析和存储过程中发生错误: " + e.getMessage());
            finalResult.setErrorCode("ANALYSIS_ERROR");
            finalResult.setException(e);
            System.err.println("地址分析和存储过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        
        return finalResult;
    }
    
    /**
     * 分析消息中的地址信息并存储到数据库
     * @param messages 消息列表
     * @param batchSize 批量处理大小
     * @return 存储的地址分析结果数量
     */
    public int analyzeAndStoreAddresses(List<Message> messages, int batchSize) throws Exception {
        if (messages == null || messages.isEmpty()) {
            return 0;
        }
        
        List<WechatMessageAnalyzeAddress> addressRecords = new ArrayList<>();
        int total = messages.size();
        int processedCount = 0;
        
        System.out.println("开始分析并存储地址信息，总消息数: " + total);
        
        // 分批处理消息
        for (int i = 0; i < messages.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, messages.size());
            List<Message> batch = messages.subList(i, endIndex);
            
            try {
                List<WechatMessageAnalyzeAddress> batchAddresses = 
                    processBatchAddressForStorage(batch, i / batchSize + 1);
                addressRecords.addAll(batchAddresses);
                processedCount += batch.size();
                
                System.out.println("已处理批次 " + (i / batchSize + 1) + 
                                 ", 消息数: " + batch.size() + 
                                 ", 提取地址数: " + batchAddresses.size());
                
                // 批次间添加短暂延迟，避免API限流
                if (i + batchSize < messages.size()) {
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.err.println("处理批次 " + (i / batchSize + 1) + " 时发生错误: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        // 批量存储到数据库
        int savedCount = 0;
        if (!addressRecords.isEmpty()) {
            try {
                // 使用新的服务方法进行批量存储
                WechatMessageAnalyzeAddressService.ExecuteResult result = addressService.executeBatch(addressRecords);
                if (result.isSuccess()) {
                    savedCount = (Integer) result.getData();
                    System.out.println("地址分析和存储完成 - 总消息数: " + total + 
                                     ", 已处理: " + processedCount + 
                                     ", 提取地址数: " + addressRecords.size() + 
                                     ", 成功存储: " + savedCount);
                } else {
                    System.err.println("批量存储地址分析结果失败: " + result.getMessage());
                    if (result.getErrorCode() != null) {
                        System.err.println("错误代码: " + result.getErrorCode());
                    }
                    throw new RuntimeException("存储地址分析结果失败: " + result.getMessage());
                }
            } catch (Exception e) {
                System.err.println("批量存储地址分析结果失败: " + e.getMessage());
                throw new RuntimeException("存储地址分析结果失败", e);
            }
        }
        
        return savedCount;
    }
    
    /**
     * 处理单批次消息的地址分析并转换为存储实体
     * @param batch 消息批次
     * @param batchNumber 批次号
     * @return 该批次的地址分析结果实体列表
     */
    private List<WechatMessageAnalyzeAddress> processBatchAddressForStorage(List<Message> batch, int batchNumber) throws Exception {
        String jsonResult = processBatchAddress(batch, batchNumber);
        return parseAddressJsonToStorageEntities(jsonResult, batch);
    }
    
    /**
     * 将地址分析的JSON结果解析为存储实体列表
     * @param jsonResult LLM返回的JSON分析结果
     * @param originalBatch 原始消息批次（用于补充信息）
     * @return 解析后的存储实体列表
     */
    private List<WechatMessageAnalyzeAddress> parseAddressJsonToStorageEntities(String jsonResult, List<Message> originalBatch) {
        List<WechatMessageAnalyzeAddress> entities = new ArrayList<>();
        
        try {
            // 预处理JSON字符串，处理控制字符和格式问题
            String cleanedJsonResult = preprocessJsonString(jsonResult);
            
            ObjectMapper objectMapper = new ObjectMapper();
            // 配置ObjectMapper以更宽松地处理JSON
            objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            objectMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
            
            JsonNode rootNode = objectMapper.readTree(cleanedJsonResult);
            
            // 处理addresses数组
            JsonNode addressesNode = rootNode.get("addresses");
            if (addressesNode != null && addressesNode.isArray()) {
                for (JsonNode addressNode : addressesNode) {
                    if (addressNode.get("is_real_address") != null && 
                        addressNode.get("is_real_address").asBoolean()) {
                        
                        String sender = addressNode.get("sender") != null ? 
                                      addressNode.get("sender").asText() : "未知";
                        String originalContent = addressNode.get("original_content") != null ? 
                                                addressNode.get("original_content").asText() : "";
                        String standardizedAddress = addressNode.get("standardized_address") != null ? 
                                                    addressNode.get("standardized_address").asText() : "";
                        double confidence = addressNode.get("confidence") != null ? 
                                          addressNode.get("confidence").asDouble() : 0.0;
                        
                        // 查找对应的原始消息获取完整信息
                        Message originalMessage = findOriginalMessage(sender, originalContent, originalBatch);
                        if (originalMessage != null) {
                            // 生成唯一ID（使用时间戳和发送者的hash）
                            Long id = generateUniqueId(originalMessage);
                            
                            // 检查是否已存在
                            if (!addressService.existsByWechatIdAndTime(sender, convertLocalDateTimeToTimestamp(originalMessage.getChatTime()))) {
                                WechatMessageAnalyzeAddress entity = WechatMessageAnalyzeAddress.builder()
                                        .wechatId(sender)
                                        .msgType(convertMessageTypeToInt(originalMessage.getType()))
                                        .wechatTime(convertLocalDateTimeToTimestamp(originalMessage.getChatTime()))
                                        .content(originalContent)
                                        .address(standardizedAddress + " (置信度: " + String.format("%.2f", confidence) + ")")
                                        .build();
                                
                                entities.add(entity);
                            } else {
                                System.out.println("地址分析记录已存在，跳过: 微信ID=" + sender + ", 时间=" + convertLocalDateTimeToTimestamp(originalMessage.getChatTime()));
                            }
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("解析地址分析JSON结果为存储实体时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
        
        return entities;
    }
    
    /**
     * 查找对应的原始消息
     * @param sender 发送者
     * @param content 消息内容
     * @param batch 消息批次
     * @return 匹配的原始消息
     */
    private Message findOriginalMessage(String sender, String content, List<Message> batch) {
        for (Message msg : batch) {
            if (sender.equals(msg.getSender()) && content.equals(msg.getMessage())) {
                return msg;
            }
        }
        return null;
    }
    
    /**
     * 生成唯一ID
     * @param message 消息对象
     * @return 唯一ID
     */
    private Long generateUniqueId(Message message) {
        // 使用发送者、消息内容和时间的组合生成唯一ID
        String combined = message.getSender() + "_" + message.getMessage() + "_" + 
                         (message.getChatTime() != null ? message.getChatTime().toString() : System.currentTimeMillis());
        return Math.abs((long) combined.hashCode());
    }
    
    /**
     * 将消息类型转换为整数
     * @param messageType 消息类型字符串
     * @return 整数类型
     */
    private Integer convertMessageTypeToInt(String messageType) {
        if (messageType == null) return 1;
        
        switch (messageType.toLowerCase()) {
            case "text":
            case "addressanalysis":
                return 1;
            case "picture":
            case "wxpic":
                return 3;
            case "voice":
            case "wxvoice":
                return 34;
            case "miniprogram":
                return 49;
            default:
                return 1; // 默认为文本类型
        }
    }
    
    /**
     * 将LocalDateTime转换为时间戳（毫秒）
     * @param localDateTime LocalDateTime对象
     * @return 时间戳（毫秒）
     */
    private Long convertLocalDateTimeToTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return System.currentTimeMillis();
        }
        return localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
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
                // 如果包含代码块，进一步提取纯正JSON
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
                    // 改进转义字符处理，避免过度转义
                    content = unescapeJsonString(content);
                    
                    // 尝试从内容中提取JSON（处理代码块格式）
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
     * 解转JSON字符串中的转义字符
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
                    case 'n':
                        result.append('\n');
                        break;
                    case 'r':
                        result.append('\r');
                        break;
                    case 't':
                        result.append('\t');
                        break;
                    case 'b':
                        result.append('\b');
                        break;
                    case 'f':
                        result.append('\f');
                        break;
                    case '"':
                        result.append('"');
                        break;
                    case '\\':
                        result.append('\\');
                        break;
                    case '/':
                        result.append('/');
                        break;
                    default:
                        // 对于不识别的转义字符，保留原样
                        result.append('\\').append(c);
                        break;
                }
                escaped = false;
            } else if (c == '\\') {
                escaped = true;
            } else {
                result.append(c);
            }
        }
        
        // 如果最后还有未处理的转义字符
        if (escaped) {
            result.append('\\');
        }
        
        return result.toString();
    }

    /**
     * 从内容中提取JSON格式数据（处理代码块等格式）
     * @param content 内容字符串
     * @return 提取的JSON字符串
     */
    private String extractJsonFromContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }
        
        // 处理代码块格式
        String cleanContent = content.trim();
        
        // 移除代码块标记
        if (cleanContent.startsWith("``json")) {
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
    
    /**
     * 保存分析结果到用户首次反馈表
     * @param analysisResult 分析结果JSON字符串
     */
    private void saveAnalysisResultToFeedbackTable(String analysisResult) {
        try {
            logger.info("开始解析并保存分析结果到用户首次反馈表");
            
            // 使用ObjectMapper解析JSON结果
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(analysisResult);
            
            // 提取字段
            String wechatId = rootNode.get("wechatId") != null ? rootNode.get("wechatId").asText() : null;
            Integer photoCount = rootNode.get("photoCount") != null ? rootNode.get("photoCount").asInt() : null;
            Boolean hasTonguePhoto = rootNode.get("hasTonguePhoto") != null ? rootNode.get("hasTonguePhoto").asBoolean() : null;
            Boolean hasBodyTypePhoto = rootNode.get("hasBodyTypePhoto") != null ? rootNode.get("hasBodyTypePhoto").asBoolean() : null;
            Boolean customerServiceRequested = rootNode.get("customerServiceRequested") != null ? rootNode.get("customerServiceRequested").asBoolean() : null;
            String analysis = rootNode.get("analysis") != null ? rootNode.get("analysis").asText() : null;
            
            // 创建UserFirstFeedback对象
            UserFirstFeedback feedback = new UserFirstFeedback(
                wechatId, 
                photoCount, 
                hasTonguePhoto, 
                hasBodyTypePhoto, 
                customerServiceRequested, 
                analysis
            );
            
            // 保存到数据库
            int savedCount = userFirstFeedbackService.saveFeedback(feedback);
            logger.info("成功保存分析结果到用户首次反馈表，微信ID: {}, 保存记录数: {}", wechatId, savedCount);
            
        } catch (Exception e) {
            logger.error("解析并保存分析结果到用户首次反馈表时发生异常", e);
            throw new RuntimeException("保存分析结果失败", e);
        }
    }

    /**
     * 分析三餐打卡与体重反馈情况
     * @param conversation 对话记录对象
     * @return UserMealCheckin对象，包含三餐打卡和体重反馈信息
     */
    public UserMealCheckin analyzeMealCheckinAndWeightFeedback(Conversation conversation) {
        logger.info("开始分析三餐打卡与体重反馈情况，用户微信ID: {}", conversation.getWechatId());
        
        try {
            // 初始化UserMealCheckin对象
            UserMealCheckin mealCheckin = new UserMealCheckin();
            mealCheckin.setWechatId(conversation.getWechatId());
            
            // 获取对话日期作为打卡日期
            if (conversation.getDate() != null) {
                mealCheckin.setCheckinDate(conversation.getDate().toLocalDate());
            } else {
                mealCheckin.setCheckinDate(java.time.LocalDate.now());
            }
            
            // 初始化打卡状态
            mealCheckin.setBreakfastChecked(0);
            mealCheckin.setLunchChecked(0);
            mealCheckin.setDinnerChecked(0);
            mealCheckin.setHasWeightFeedback(0);
            
            // 如果没有消息，直接返回默认对象
            if (conversation.getMessages() == null || conversation.getMessages().isEmpty()) {
                logger.warn("用户对话记录为空，用户微信ID: {}", conversation.getWechatId());
                return mealCheckin;
            }
            
            // 使用大模型直接分析三餐打卡和体重反馈情况
            String analysisResult = analyzeMealCheckinAndWeightFeedbackWithLLM(conversation);
            
            if (analysisResult != null && !analysisResult.isEmpty()) {
                try {
                    // 解析大模型返回的JSON结果
                    com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(analysisResult);
                    
                    // 提取三餐打卡和体重反馈信息
                    if (rootNode.has("breakfastChecked")) {
                        mealCheckin.setBreakfastChecked(rootNode.get("breakfastChecked").asInt());
                    }
                    if (rootNode.has("lunchChecked")) {
                        mealCheckin.setLunchChecked(rootNode.get("lunchChecked").asInt());
                    }
                    if (rootNode.has("dinnerChecked")) {
                        mealCheckin.setDinnerChecked(rootNode.get("dinnerChecked").asInt());
                    }
                    if (rootNode.has("hasWeightFeedback")) {
                        mealCheckin.setHasWeightFeedback(rootNode.get("hasWeightFeedback").asInt());
                    }
                    
                    logger.info("三餐打卡与体重反馈分析完成，用户微信ID: {}，早餐: {}，午餐: {}，晚餐: {}，体重反馈: {}", 
                               conversation.getWechatId(), 
                               mealCheckin.getBreakfastChecked(), 
                               mealCheckin.getLunchChecked(), 
                               mealCheckin.getDinnerChecked(), 
                               mealCheckin.getHasWeightFeedback());
                    
                    return mealCheckin;
                } catch (Exception e) {
                    logger.error("解析大模型返回的JSON时发生异常，用户微信ID: " + conversation.getWechatId(), e);
                }
            }
            
            // 如果大模型分析失败，使用原有的规则进行分析
            return analyzeMealCheckinAndWeightFeedbackFallback(conversation);
            
        } catch (Exception e) {
            logger.error("分析三餐打卡与体重反馈情况时发生异常，用户微信ID: " + conversation.getWechatId(), e);
            // 返回默认对象
            UserMealCheckin defaultCheckin = new UserMealCheckin();
            defaultCheckin.setWechatId(conversation.getWechatId());
            if (conversation.getDate() != null) {
                defaultCheckin.setCheckinDate(conversation.getDate().toLocalDate());
            } else {
                defaultCheckin.setCheckinDate(java.time.LocalDate.now());
            }
            defaultCheckin.setBreakfastChecked(0);
            defaultCheckin.setLunchChecked(0);
            defaultCheckin.setDinnerChecked(0);
            defaultCheckin.setHasWeightFeedback(0);
            return defaultCheckin;
        }
    }
    
    /**
     * 使用大模型分析三餐打卡与体重反馈情况
     * @param conversation 对话记录
     * @return 包含三餐打卡和体重反馈信息的JSON字符串
     */
    private String analyzeMealCheckinAndWeightFeedbackWithLLM(Conversation conversation) {
        try {
            logger.info("使用大模型分析三餐打卡与体重反馈，用户微信ID: {}", conversation.getWechatId());
            
            // 构建分析提示
            StringBuilder analysisPrompt = new StringBuilder();
            analysisPrompt.append("请分析以下用户对话记录，判断用户在各个时间段是否进行了三餐打卡以及是否提供了体重反馈信息：\n\n");
            analysisPrompt.append("分析要求：\n");
            analysisPrompt.append("1. 根据消息发送时间判断三餐打卡情况：\n");
            analysisPrompt.append("   - 早餐：6:00-11:00时间段内发送图片或饮食反馈文字\n");
            analysisPrompt.append("   - 午餐：11:00-13:00时间段内发送图片或饮食反馈文字\n");
            analysisPrompt.append("   - 晚餐：17:00-20:00时间段内发送图片或饮食反馈文字\n");
            analysisPrompt.append("2. 体重反馈包括明确提到体重数字、体重变化、称重行为等\n");
            analysisPrompt.append("3. 如果用户在早餐时间段发送了两次照片且与体重相关，请判断是否包含体重反馈\n");
            analysisPrompt.append("4. 请严格按照以下JSON格式返回分析结果：\n");
            analysisPrompt.append("{\n");
            analysisPrompt.append("  \"breakfastChecked\": 0或1,\n");
            analysisPrompt.append("  \"lunchChecked\": 0或1,\n");
            analysisPrompt.append("  \"dinnerChecked\": 0或1,\n");
            analysisPrompt.append("  \"hasWeightFeedback\": 0或1,\n");
            analysisPrompt.append("  \"analysis\": \"详细分析说明\"\n");
            analysisPrompt.append("}\n\n");
            analysisPrompt.append("用户对话记录：\n");

            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            analysisPrompt.append("# ").append(conversation.getWechatId()).append(" ").append(conversation.getDate()).append('\n');
            conversation.getMessages().forEach(m -> analysisPrompt
                    .append('[').append(m.getChatTime() == null ? "" : m.getChatTime().format(dtf)).append("] ")
                    .append(m.getSender()).append(": ")
                    .append(m.getMessage())
                    .append('\n'));
            logger.info("分析提示构建完成，用户微信ID: {}，提示长度: {}", conversation.getWechatId(), analysisPrompt.length());

            // 调用大模型进行分析
            String payload = "{\n" +
                    "  \"model\": \"" + QWEN_MODEL + "\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"你是专业的客服对话分析师，擅长分析用户的三餐打卡情况和体重反馈信息。请严格按照指定的JSON格式返回分析结果。\"},\n" +
                    "    {\"role\": \"user\", \"content\": " + jsonEscape(analysisPrompt.toString()) + "}\n" +
                    "  ]\n" +
                    "}";

            String apiKey = apiKeyProp.trim();
            if (apiKey == null || apiKey.isBlank()) {
                logger.error("未配置百炼 API Key，用户微信ID: {}", conversation.getWechatId());
                return null;
            }

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(QWEN_API_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .timeout(java.time.Duration.ofSeconds(60))
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(payload, java.nio.charset.StandardCharsets.UTF_8))
                    .build();

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            logger.info("正在发送HTTP请求到大模型API，用户微信ID: {}", conversation.getWechatId());
            java.net.http.HttpResponse<String> resp = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));
            logger.info("收到HTTP响应，用户微信ID: {}，状态码: {}", conversation.getWechatId(), resp.statusCode());

            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                String body = resp.body();
                logger.info("大模型API响应体长度: {}，用户微信ID: {}", body.length(), conversation.getWechatId());

                // 提取响应中的content部分
                String extractedContent = extractContentFromResponse(body);
                logger.info("提取的响应内容长度: {}，用户微信ID: {}", extractedContent.length(), conversation.getWechatId());

                // 验证返回的是否为有效JSON
                if (isValidJson(extractedContent)) {
                    // 如果包含代码块，进一步提取纯正JSON
                    String cleanJson = extractJsonFromContent(extractedContent);
                    String finalResult = (cleanJson != null && !cleanJson.isEmpty()) ? cleanJson : extractedContent;
                    
                    logger.info("大模型分析完成，用户微信ID: {}", conversation.getWechatId());
                    return finalResult;
                } else {
                    logger.warn("AI返回格式错误，用户微信ID: {}，原始响应长度: {}", conversation.getWechatId(), extractedContent.length());
                    return null;
                }
            } else {
                logger.error("Qwen API 调用失败，用户微信ID: {}，状态码: {}，响应体长度: {}", conversation.getWechatId(), resp.statusCode(), resp.body().length());
                return null;
            }
        } catch (Exception e) {
            logger.error("使用大模型分析三餐打卡与体重反馈时发生异常，用户微信ID: " + conversation.getWechatId(), e);
            return null;
        }
    }
    
    /**
     * 使用原有规则分析三餐打卡与体重反馈情况（备用方案）
     * @param conversation 对话记录对象
     * @return UserMealCheckin对象
     */
    private UserMealCheckin analyzeMealCheckinAndWeightFeedbackFallback(Conversation conversation) {
        logger.info("使用备用方案分析三餐打卡与体重反馈情况，用户微信ID: {}", conversation.getWechatId());
        
        // 初始化UserMealCheckin对象
        UserMealCheckin mealCheckin = new UserMealCheckin();
        mealCheckin.setWechatId(conversation.getWechatId());
        
        // 获取对话日期作为打卡日期
        if (conversation.getDate() != null) {
            mealCheckin.setCheckinDate(conversation.getDate().toLocalDate());
        } else {
            mealCheckin.setCheckinDate(java.time.LocalDate.now());
        }
        
        // 初始化打卡状态
        mealCheckin.setBreakfastChecked(0);
        mealCheckin.setLunchChecked(0);
        mealCheckin.setDinnerChecked(0);
        mealCheckin.setHasWeightFeedback(0);
        
        // 如果没有消息，直接返回默认对象
        if (conversation.getMessages() == null || conversation.getMessages().isEmpty()) {
            logger.warn("用户对话记录为空，用户微信ID: {}", conversation.getWechatId());
            return mealCheckin;
        }
        
        // 分析各时间段的打卡情况
        int breakfastImageCount = 0;
        boolean hasWeightFeedbackText = false;
        
        for (Message message : conversation.getMessages()) {
            // 获取消息时间
            java.time.LocalDateTime chatTime = message.getChatTime();
            if (chatTime == null) {
                continue;
            }
            
            // 获取小时数
            int hour = chatTime.getHour();
            
            // 判断消息类型和内容
            String messageType = message.getType();
            String messageContent = message.getMessage();
            
            // 检查是否为图片消息
            boolean isImageMessage = "Picture".equals(messageType) || "WxPic".equals(messageType);
            
            // 检查是否包含体重反馈相关关键词
            boolean containsWeightKeywords = messageContent != null && 
                (messageContent.contains("体重") || messageContent.contains("斤") || 
                 messageContent.contains("公斤") || messageContent.contains("kg") ||
                 messageContent.contains("KG") || messageContent.contains("减重") ||
                 messageContent.contains("增重") || messageContent.contains("轻了") ||
                 messageContent.contains("重了") || messageContent.contains("称重"));
            
            // 判断时间段并更新打卡状态
            if (hour >= 6 && hour < 11) {
                // 早餐时间段 (6点～11点)
                if (isImageMessage || (messageContent != null && isDietFeedbackMessage(messageContent))) {
                    mealCheckin.setBreakfastChecked(1);
                    if (isImageMessage) {
                        breakfastImageCount++;
                    }
                }
                
                // 检查是否有体重反馈
                if (containsWeightKeywords) {
                    hasWeightFeedbackText = true;
                }
            } else if (hour >= 11 && hour < 13) {
                // 午餐时间段 (11点～13点)
                if (isImageMessage || (messageContent != null && isDietFeedbackMessage(messageContent))) {
                    mealCheckin.setLunchChecked(1);
                }
                
                // 检查是否有体重反馈
                if (containsWeightKeywords) {
                    hasWeightFeedbackText = true;
                }
            } else if (hour >= 17 && hour < 20) {
                // 晚餐时间段 (17点～20点)
                if (isImageMessage || (messageContent != null && isDietFeedbackMessage(messageContent))) {
                    mealCheckin.setDinnerChecked(1);
                }
                
                // 检查是否有体重反馈
                if (containsWeightKeywords) {
                    hasWeightFeedbackText = true;
                }
            } else {
                // 其他时间段检查是否有体重反馈
                if (containsWeightKeywords) {
                    hasWeightFeedbackText = true;
                }
            }
        }
        
        // 如果早餐时间段出现两次图片，结合上下文判断是否进行了体重反馈
        if (breakfastImageCount >= 2) {
            // 进一步分析是否包含体重反馈相关的内容
            if (hasWeightFeedbackText) {
                mealCheckin.setHasWeightFeedback(1);
            } else {
                // 使用大模型分析是否包含体重反馈
                boolean weightFeedback = analyzeWeightFeedbackWithLLM(conversation);
                if (weightFeedback) {
                    mealCheckin.setHasWeightFeedback(1);
                }
            }
        } else if (hasWeightFeedbackText) {
            // 如果有明确的体重反馈关键词，直接标记为有体重反馈
            mealCheckin.setHasWeightFeedback(1);
        }
        
        logger.info("备用方案分析完成，用户微信ID: {}，早餐: {}，午餐: {}，晚餐: {}，体重反馈: {}", 
                   conversation.getWechatId(), 
                   mealCheckin.getBreakfastChecked(), 
                   mealCheckin.getLunchChecked(), 
                   mealCheckin.getDinnerChecked(), 
                   mealCheckin.getHasWeightFeedback());
        
        return mealCheckin;
    }
    
    /**
     * 判断消息是否为饮食反馈消息
     * @param messageContent 消息内容
     * @return 是否为饮食反馈消息
     */
    private boolean isDietFeedbackMessage(String messageContent) {
        if (messageContent == null || messageContent.isEmpty()) {
            return false;
        }
        
        // 定义饮食反馈关键词
        String[] dietKeywords = {
            "早餐", "午餐", "晚餐", "吃了", "吃的是", "今天吃", "早饭", "午饭", "晚饭",
            "喝的", "喝了", "喝了点", "吃的", "点外卖", "点了个", "点餐", "叫了外卖",
            "面条", "米饭", "粥", "包子", "馒头", "面包", "牛奶", "豆浆", "咖啡",
            "蔬菜", "水果", "肉类", "鱼", "鸡肉", "猪肉", "牛肉", "羊肉",
            "沙拉", "汤", "火锅", "烧烤", "炒菜", "蒸蛋", "煎蛋", "煮蛋"
        };
        
        // 检查是否包含饮食关键词
        for (String keyword : dietKeywords) {
            if (messageContent.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 使用大模型分析是否包含体重反馈
     * @param conversation 对话记录
     * @return 是否包含体重反馈
     */
    private boolean analyzeWeightFeedbackWithLLM(Conversation conversation) {
        try {
            logger.info("使用大模型分析体重反馈，用户微信ID: {}", conversation.getWechatId());
            
            // 构建分析提示
            StringBuilder analysisPrompt = new StringBuilder();
            analysisPrompt.append("请分析以下用户对话记录，判断用户是否提供了体重反馈信息：\n\n");
            analysisPrompt.append("分析要求：\n");
            analysisPrompt.append("1. 体重反馈包括明确提到体重数字、体重变化、称重行为等\n");
            analysisPrompt.append("2. 如果用户发送了两次照片且与体重相关，请判断是否包含体重反馈\n");
            analysisPrompt.append("3. 请以JSON格式返回分析结果，格式如下：\n");
            analysisPrompt.append("{\n");
            analysisPrompt.append("  \"hasWeightFeedback\": true/false,\n");
            analysisPrompt.append("  \"analysis\": \"详细分析说明\"\n");
            analysisPrompt.append("}\n\n");
            analysisPrompt.append("用户对话记录：\n");

            java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            analysisPrompt.append("# ").append(conversation.getWechatId()).append(" ").append(conversation.getDate()).append('\n');
            conversation.getMessages().forEach(m -> analysisPrompt
                    .append('[').append(m.getChatTime() == null ? "" : m.getChatTime().format(dtf)).append("] ")
                    .append(m.getSender()).append(": ")
                    .append(m.getMessage())
                    .append('\n'));
            logger.info("分析提示构建完成，用户微信ID: {}，提示长度: {}", conversation.getWechatId(), analysisPrompt.length());

            // 调用大模型进行分析
            String payload = "{\n" +
                    "  \"model\": \"" + QWEN_MODEL + "\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"你是专业的客服对话分析师，擅长分析用户是否提供了体重反馈信息。\"},\n" +
                    "    {\"role\": \"user\", \"content\": " + jsonEscape(analysisPrompt.toString()) + "}\n" +
                    "  ]\n" +
                    "}";

            String apiKey = apiKeyProp.trim();
            if (apiKey == null || apiKey.isBlank()) {
                logger.error("未配置百炼 API Key，用户微信ID: {}", conversation.getWechatId());
                return false;
            }

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(QWEN_API_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .timeout(java.time.Duration.ofSeconds(60))
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(payload, java.nio.charset.StandardCharsets.UTF_8))
                    .build();

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            logger.info("正在发送HTTP请求到大模型API，用户微信ID: {}", conversation.getWechatId());
            java.net.http.HttpResponse<String> resp = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString(java.nio.charset.StandardCharsets.UTF_8));
            logger.info("收到HTTP响应，用户微信ID: {}，状态码: {}", conversation.getWechatId(), resp.statusCode());

            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                String body = resp.body();
                logger.info("大模型API响应体长度: {}，用户微信ID: {}", body.length(), conversation.getWechatId());

                // 提取响应中的content部分
                String extractedContent = extractContentFromResponse(body);
                logger.info("提取的响应内容长度: {}，用户微信ID: {}", extractedContent.length(), conversation.getWechatId());

                // 验证返回的是否为有效JSON
                if (isValidJson(extractedContent)) {
                    // 如果包含代码块，进一步提取纯正JSON
                    String cleanJson = extractJsonFromContent(extractedContent);
                    String finalResult = (cleanJson != null && !cleanJson.isEmpty()) ? cleanJson : extractedContent;
                    
                    // 解析JSON结果
                    try {
                        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                        com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(finalResult);
                        
                        boolean hasWeightFeedback = rootNode.get("hasWeightFeedback") != null ? 
                                                   rootNode.get("hasWeightFeedback").asBoolean() : false;
                        
                        logger.info("大模型分析完成，用户微信ID: {}，是否有体重反馈: {}", conversation.getWechatId(), hasWeightFeedback);
                        return hasWeightFeedback;
                    } catch (Exception e) {
                        logger.error("解析大模型返回的JSON时发生异常，用户微信ID: " + conversation.getWechatId(), e);
                        return false;
                    }
                } else {
                    logger.warn("AI返回格式错误，用户微信ID: {}，原始响应长度: {}", conversation.getWechatId(), extractedContent.length());
                    return false;
                }
            } else {
                logger.error("Qwen API 调用失败，用户微信ID: {}，状态码: {}，响应体长度: {}", conversation.getWechatId(), resp.statusCode(), resp.body().length());
                return false;
            }
        } catch (Exception e) {
            logger.error("使用大模型分析体重反馈时发生异常，用户微信ID: " + conversation.getWechatId(), e);
            return false;
        }
    }

    public String analyzeBodyPhotos(String wechatId) throws Exception {
        logger.info("开始分析用户舌苔和体型照片，用户微信ID: {}", wechatId);

        try {
            // 1. 查询指定用户首次聊天两天内的对话记录
            logger.info("正在查询用户首次聊天两天内的对话记录，用户微信ID: {}", wechatId);
            Conversation userConversation = wechatMessageMapper.findConversationsWithinTwoDaysOfFirstChatByWechatId(wechatId);
            logger.info("查询完成，用户微信ID: {}，查询结果是否为空: {}", wechatId, userConversation == null);

            if (userConversation == null || userConversation.getMessages() == null || userConversation.getMessages().isEmpty()) {
                logger.warn("未找到用户对话记录，用户微信ID: {}", wechatId);
                return "{\"wechatId\": \"" + wechatId + "\", \"result\": \"未找到用户对话记录\"}";
            }

            logger.info("找到用户对话记录，用户微信ID: {}，消息数量: {}", wechatId, userConversation.getMessages().size());

            // 2. 分析对话记录中的图片消息
            logger.info("开始分析对话记录中的图片消息，用户微信ID: {}", wechatId);
            List<Message> pictureMessages = new ArrayList<>();
            int totalMessages = 0;
            for (Message message : userConversation.getMessages()) {
                totalMessages++;
                // 查找图片类型的消息
                if ("Picture".equals(message.getType()) || "WxPic".equals(message.getType())) {
                    pictureMessages.add(message);
                    logger.info("找到图片消息，发送者: {}，消息类型: {}",
                            message.getSender(), message.getType());
                }
            }
            logger.info("图片消息分析完成，用户微信ID: {}，总消息数: {}，图片消息数: {}", wechatId, totalMessages, pictureMessages.size());

            // 3. 构建分析提示
            logger.info("正在构建分析提示，用户微信ID: {}", wechatId);
            StringBuilder analysisPrompt = new StringBuilder();
            analysisPrompt.append("请分析以下用户对话记录，判断用户是否发送过舌苔和体型照片：\n\n");
            analysisPrompt.append("分析要求：\n");
            analysisPrompt.append("1. 如果用户只发送了一张照片，认为是舌苔照片\n");
            analysisPrompt.append("2. 如果用户发送了两张照片，认为既有舌苔照片也有体型照片\n");
            analysisPrompt.append("3. 如果用户在对话中明确提到客服要求发送舌苔和体型照片，请特别标注\n");
            analysisPrompt.append("4. 请以JSON格式返回分析结果，格式如下：\n");
            analysisPrompt.append("{\n");
            analysisPrompt.append("  \"wechatId\": \"用户微信ID\",\n");
            analysisPrompt.append("  \"photoCount\": 照片数量,\n");
            analysisPrompt.append("  \"hasTonguePhoto\": true/false,\n");
            analysisPrompt.append("  \"hasBodyTypePhoto\": true/false,\n");
            analysisPrompt.append("  \"customerServiceRequested\": true/false,\n");
            analysisPrompt.append("  \"analysis\": \"详细分析说明\"\n");
            analysisPrompt.append("}\n\n");
            analysisPrompt.append("用户对话记录：\n");

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            analysisPrompt.append("# ").append(userConversation.getWechatId()).append(" ").append(userConversation.getDate()).append('\n');
            userConversation.getMessages().forEach(m -> analysisPrompt
                    .append('[').append(m.getChatTime() == null ? "" : m.getChatTime().format(dtf)).append("] ")
                    .append(m.getSender()).append(": ")
                    .append(m.getMessage())
                    .append('\n'));
            logger.info("分析提示构建完成，用户微信ID: {}，提示长度: {}", wechatId, analysisPrompt.length());

            // 4. 调用大模型进行分析
            logger.info("正在调用大模型进行分析，用户微信ID: {}", wechatId);
            String payload = "{\n" +
                    "  \"model\": \"" + QWEN_MODEL + "\",\n" +
                    "  \"messages\": [\n" +
                    "    {\"role\": \"system\", \"content\": \"你是专业的客服对话分析师，擅长分析用户发送的照片类型和客服要求。\"},\n" +
                    "    {\"role\": \"user\", \"content\": " + jsonEscape(analysisPrompt.toString()) + "}\n" +
                    "  ]\n" +
                    "}";

            String apiKey = apiKeyProp.trim();
            if (apiKey == null || apiKey.isBlank()) {
                logger.error("未配置百炼 API Key，用户微信ID: {}", wechatId);
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
            logger.info("正在发送HTTP请求到大模型API，用户微信ID: {}", wechatId);
            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            logger.info("收到HTTP响应，用户微信ID: {}，状态码: {}", wechatId, resp.statusCode());

            if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                String body = resp.body();
                logger.info("大模型API响应体长度: {}，用户微信ID: {}", body.length(), wechatId);

                // 提取响应中的content部分
                String extractedContent = extractContentFromResponse(body);
                logger.info("提取的响应内容长度: {}，用户微信ID: {}", extractedContent.length(), wechatId);

                // 验证返回的是否为有效JSON
                if (isValidJson(extractedContent)) {
                    // 如果包含代码块，进一步提取纯正JSON
                    String cleanJson = extractJsonFromContent(extractedContent);
                    String finalResult = (cleanJson != null && !cleanJson.isEmpty()) ? cleanJson : extractedContent;

                    // 保存分析结果到用户首次反馈表
                    try {
                        saveAnalysisResultToFeedbackTable(finalResult);
                    } catch (Exception e) {
                        logger.error("保存分析结果到用户首次反馈表时发生异常，用户微信ID: " + wechatId, e);
                    }

                    logger.info("分析完成并返回结果，用户微信ID: {}", wechatId);
                    return finalResult;
                } else {
                    // 如果不是有效JSON，包装成错误格式
                    logger.warn("AI返回格式错误，用户微信ID: {}，原始响应长度: {}", wechatId, extractedContent.length());
                    return "{\"error\": \"AI返回格式错误\", \"raw_response_length\": " + extractedContent.length() + "}";
                }
            } else {
                logger.error("Qwen API 调用失败，用户微信ID: {}，状态码: {}，响应体长度: {}", wechatId, resp.statusCode(), resp.body().length());
                throw new RuntimeException("Qwen API 调用失败: status=" + resp.statusCode() + ", body_length=" + resp.body().length());
            }
        } catch (Exception e) {
            logger.error("分析用户舌苔和体型照片时发生异常，用户微信ID: " + wechatId, e);
            throw e;
        }
    }
}


