package org.panjy.servicemetricsplatform.service.analysis;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.panjy.servicemetricsplatform.entity.label.WechatUserLabel;
import org.panjy.servicemetricsplatform.entity.message.Conversation;
import org.panjy.servicemetricsplatform.entity.message.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * 健康画像分析服务
 * 专门用于分析用户聊天记录中的健康相关信息，生成健康画像标签
 */
@Service
public class HealthProfileAnalysisService {

    private static final String QWEN_API_URL = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    private static final String QWEN_MODEL = "qwen-max";

    @Value("${dashscope.apiKey:}")
    private String apiKeyProp;

    /**
     * 从对话中提取健康画像标签
     * 
     * @param conversation 对话记录
     * @return WechatUserLabel对象，包含标签信息
     */
    public WechatUserLabel extractHealthProfileTags(Conversation conversation) throws Exception {
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

        // 使用用户提供的精确提示词
        String instruction = "你是一名健康用户画像分析助手，服务于健康管理类销售场景。\n\n" +
                "你擅长从真实、非结构化的聊天记录中，提炼对销售有价值、但不构成医疗判断的健康相关标签。\n\n" +
                "给你一段销售与客户的聊天记录，请你：\n" +
                " 只基于客户自己的表达\n" +
                " 提取与客户身体状况、健康问题、健康关注点相关的信息\n" +
                " 将其整理为适合展示在销售插件中的\"健康画像标签\"\n\n" +
                "可提取的信息类型（面向销售实用）：\n\n" +
                "1 明确慢病 / 长期疾病标签\n" +
                "客户明确承认或反复提到的疾病或慢性问题。\n" +
                "示例标签：\n" +
                " 高血压（已提及）\n" +
                " 糖尿病（自述）\n" +
                " 心脏问题（模糊描述）\n" +
                " 胃病 / 肠胃问题\n" +
                " 骨关节问题\n\n" +
                "2 重要医疗史标签（销售需知）\n" +
                "会明显影响产品推荐或沟通方式的医疗经历。\n" +
                "示例标签：\n" +
                " 做过心脏支架\n" +
                " 心脏搭桥手术史\n" +
                " 胆囊切除\n" +
                " 骨科手术史\n\n" +
                "3 当前或反复出现的身体不适\n" +
                "客户近期或长期抱怨的身体问题，是销售跟进的重点线索。\n" +
                "示例标签：\n" +
                " 关节疼 / 膝盖疼\n" +
                " 胃胀 / 消化不良\n" +
                " 腹胀 / 腹痛\n" +
                " 睡眠不好\n" +
                " 容易疲劳\n\n" +
                "4 健康指标异常或医生提醒（非诊断）\n" +
                "客户提到过但未必确诊的健康风险信号。\n" +
                "示例标签：\n" +
                " 血压偏高\n" +
                " 血糖偏高\n" +
                " 血脂不太好\n" +
                " 医生提醒要注意心脏\n\n" +
                "5 与健康管理直接相关的行为线索\n" +
                "有助于销售判断客户当前健康阶段。\n" +
                "示例标签：\n" +
                " 长期服用降压药 \n" +
                " 正在控制饮食\n" +
                " 不太愿意吃药\n" +
                " 关注养生 / 调理\n\n" +
                "标签生成规则（非常重要）：\n\n" +
                " 只生成聊天中有依据的标签\n\n" +
                " 允许模糊，但要标注清楚\n\n" +
                " 用：明确 / 模糊 / 自述\n\n" +
                " 不做医学推断、不升级风险\n\n" +
                " 宁可少，不可乱\n\n" +
                "输出格式（销售插件友好）：\n\n" +
                "请严格按照以下结构输出：\n\n" +
                "[\n" +
                "      { \"标签\": \"高血压\", \"确定性\": \"明确\" },\n" +
                "      { \"标签\": \"心脏支架手术史\", \"确定性\": \"明确\" },\n" +
                "      { \"标签\": \"关节疼\", \"确定性\": \"反复提及\" },\n" +
                "      { \"标签\": \"血糖偏高\", \"确定性\": \"自述\" },\n" +
                "      { \"标签\": \"长期服用降压药\", \"确定性\": \"明确\" }\n" +
                "]";

        String payload = "{\n" +
                "  \"model\": \"" + QWEN_MODEL + "\",\n" +
                "  \"messages\": [\n" +
                "    {\"role\": \"system\", \"content\": \"你是专业的健康画像分析助手，能够准确识别和提取客户聊天中涉及的健康相关信息，严格按照指定格式输出健康标签。\"},\n" +
                "    {\"role\": \"user\", \"content\": " + jsonEscape(instruction + "\n\n" + sb.toString()) + "}\n" +
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
            String body = resp.body();
            
            // 尝试从响应中提取content部分
            int idx = body.indexOf("\"content\":");
            if (idx > 0) {
                int start = body.indexOf('"', idx + 10);
                if (start > 0) {
                    // 查找匹配的引号，处理转义字符
                    int endIdx = -1;
                    boolean escaped = false;
                    for (int i = start + 1; i < body.length(); i++) {
                        char c = body.charAt(i);
                        if (c == '\\' && !escaped) {
                            escaped = true;
                            continue;
                        }
                        if (c == '"' && !escaped) {
                            endIdx = i;
                            break;
                        }
                        escaped = false;
                    }
                    
                    if (endIdx > start) {
                        String content = body.substring(start + 1, endIdx);
                        // 解码转义字符
                        content = content.replace("\\n", "\n").replace("\\t", "\t").replace("\\r", "\r").replace("\\\\", "\\").replace("\\\"", "\"");
                        
                        // 解析API返回的JSON数据并创建WechatUserLabel对象
                        try {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Map<String, String>>>(){}.getType();
                            List<Map<String, String>> tagList = gson.fromJson(content, listType);
                            
                            WechatUserLabel wechatUserLabel = new WechatUserLabel();
                            wechatUserLabel.setWechatFriendId(conversation.getWechatId());
                            // 从消息中提取销售账号ID，如果无法获取则使用默认值
                            String wechatAccountId = "";
                            wechatUserLabel.setWechatAccountId(wechatAccountId);
                            wechatUserLabel.setEvidence(sb.toString());
                            wechatUserLabel.setCreatedAt(LocalDateTime.now());
                            wechatUserLabel.setUpdatedAt(LocalDateTime.now());
                            
                            if (tagList != null && !tagList.isEmpty()) {
                                // 将标签列表转换为符合要求的格式：[{"label": "高血压", "certainty": "明确"}, {"label": "类风湿", "certainty": "明确"}]
                                List<Map<String, String>> formattedTags = tagList.stream()
                                    .map(tag -> {
                                        Map<String, String> formattedTag = new HashMap<>();
                                        formattedTag.put("label", tag.get("标签"));
                                        formattedTag.put("certainty", tag.get("确定性"));
                                        return formattedTag;
                                    })
                                    .collect(Collectors.toList());
                                
                                // 将格式化后的标签列表转换为JSON字符串并设置为label字段
                                String labelInfo = gson.toJson(formattedTags);
                                wechatUserLabel.setLabel(labelInfo);
                            } else {
                                // 如果没有标签，设置空数组
                                wechatUserLabel.setLabel("[]");
                            }
                            
                            return wechatUserLabel;
                        } catch (Exception e) {
                            // 如果解析JSON失败，创建一个包含原始内容的WechatUserLabel对象
                            WechatUserLabel wechatUserLabel = new WechatUserLabel();
                            wechatUserLabel.setWechatFriendId(conversation.getWechatId());
                            // 从消息中提取销售账号ID，如果无法获取则使用默认值
                            String wechatAccountId = "";
                            wechatUserLabel.setWechatAccountId(wechatAccountId);
                            wechatUserLabel.setLabel("JSON解析失败");
                            wechatUserLabel.setEvidence(content);
                            wechatUserLabel.setCreatedAt(LocalDateTime.now());
                            wechatUserLabel.setUpdatedAt(LocalDateTime.now());
                            
                            return wechatUserLabel;
                        }
                    }
                }
            }
            
            // 如果无法提取content，返回整个响应体
            String content = body;
            
            // 解析API返回的JSON数据并创建WechatUserLabel对象
            try {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<Map<String, String>>>(){}.getType();
                List<Map<String, String>> tagList = gson.fromJson(content, listType);
                
                WechatUserLabel wechatUserLabel = new WechatUserLabel();
                wechatUserLabel.setWechatFriendId(conversation.getWechatId());
                // 从消息中提取销售账号ID，如果无法获取则使用默认值
                String wechatAccountId = "";
                wechatUserLabel.setWechatAccountId(wechatAccountId);
                wechatUserLabel.setEvidence(sb.toString());
                wechatUserLabel.setCreatedAt(LocalDateTime.now());
                wechatUserLabel.setUpdatedAt(LocalDateTime.now());
                
                if (tagList != null && !tagList.isEmpty()) {
                    // 将标签列表转换为符合要求的格式：[{"label": "高血压", "certainty": "明确"}, {"label": "类风湿", "certainty": "明确"}]
                    List<Map<String, String>> formattedTags = tagList.stream()
                        .map(tag -> {
                            Map<String, String> formattedTag = new HashMap<>();
                            formattedTag.put("label", tag.get("标签"));
                            formattedTag.put("certainty", tag.get("确定性"));
                            return formattedTag;
                        })
                        .collect(Collectors.toList());
                    
                    // 将格式化后的标签列表转换为JSON字符串并设置为label字段
                    String labelInfo = gson.toJson(formattedTags);
                    wechatUserLabel.setLabel(labelInfo);
                } else {
                    // 如果没有标签，设置空数组
                    wechatUserLabel.setLabel("[]");
                }
                
                return wechatUserLabel;
            } catch (Exception e) {
                // 如果解析JSON失败，创建一个包含原始内容的WechatUserLabel对象
                WechatUserLabel wechatUserLabel = new WechatUserLabel();
                wechatUserLabel.setWechatFriendId(conversation.getWechatId());
                // 从消息中提取销售账号ID，如果无法获取则使用默认值
                String wechatAccountId = "";
                wechatUserLabel.setWechatAccountId(wechatAccountId);
                wechatUserLabel.setLabel("JSON解析失败");
                wechatUserLabel.setEvidence(content);
                wechatUserLabel.setCreatedAt(LocalDateTime.now());
                wechatUserLabel.setUpdatedAt(LocalDateTime.now());
                
                return wechatUserLabel;
            }
        }
        throw new RuntimeException("Qwen API 调用失败: status=" + resp.statusCode() + ", body=" + resp.body());
    }

    /**
     * JSON字符串转义方法
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
}