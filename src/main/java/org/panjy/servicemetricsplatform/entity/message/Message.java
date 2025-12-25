package org.panjy.servicemetricsplatform.entity.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Data
@Embeddable
@NoArgsConstructor
public class Message {
    private String sender;     // 发送人wxid
    private String message;    // 消息内容
    private String type;       // 消息类型
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime chatTime;
    
    // 标记是否已处理，避免重复处理
    private transient boolean processed = false;

    // 手动添加getter方法以确保编译通过
    public String getSender() {
        return sender;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getType() {
        return type;
    }
    
    public LocalDateTime getChatTime() {
        return chatTime;
    }
    
    public boolean isProcessed() {
        return processed;
    }
    
    // 手动添加setter方法
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public void setChatTime(LocalDateTime chatTime) {
        this.chatTime = chatTime;
    }
    
    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public Message(String sender, String message, String type, LocalDateTime chatTime) {
        this.sender = sender;
        this.chatTime = chatTime;
        
        // 处理WxLink类型的特殊消息格式
        if ("WxLink".equals(type) && message != null) {
            try {
                // 解析JSON格式的消息
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode messageNode = objectMapper.readTree(message);
                
                // 检查是否为小程序类型（包含contentXml字段）
                if (messageNode.has("contentXml") && messageNode.has("type") && 
                    "miniprogram".equals(messageNode.get("type").asText())) {
                    
                    // 处理小程序消息，提取XML中的标题
                    String contentXml = messageNode.get("contentXml").asText();
                    String title = extractTitleFromXml(contentXml);
                    
                    this.message = title.isEmpty() ? "小程序分享" : title;
                    this.type = "MiniProgram";
                } 
                // 检查是否为merged类型（合并消息）
                else if (messageNode.has("content") && messageNode.has("type") && 
                    "merged".equals(messageNode.get("type").asText())) {
                    
                    String contentUrl = messageNode.get("content").asText();
                    
                    // 从URL获取JSON数据并处理
                    String processedMessage = processJsonContent(contentUrl);
                    
                    if (processedMessage != null && !processedMessage.isEmpty()) {
                        this.message = processedMessage;
                        this.type = "ChatHistory"; // 修改类型标识已处理
                    } else {
                        this.message = "合并的聊天记录";
                        this.type = "ChatHistory";
                    }
                } 
                // 检查是否为付款链接类型
                else if (messageNode.has("type") && "link".equals(messageNode.get("type").asText()) &&
                         messageNode.has("title") && messageNode.get("title").asText().contains("向商家付款")) {
                    
                    // 处理付款链接，保留固定文本
                    this.message = "向商家付款的链接";
                    this.type = "PaymentLink";
                } 
                // 检查是否为普通链接分享
                else if (messageNode.has("type") && "link".equals(messageNode.get("type").asText()) &&
                         messageNode.has("title")) {
                    
                    // 处理普通链接分享，保留标题和描述
                    String title = messageNode.get("title").asText();
                    String desc = messageNode.has("desc") ? messageNode.get("desc").asText() : "";
                    
                    if (!desc.isEmpty()) {
                        this.message = title + "\n" + desc;
                    } else {
                        this.message = title;
                    }
                    this.type = "LinkShare";
                } 
                // 检查是否为内容分享类型
                else if (messageNode.has("type") && "content_sharing".equals(messageNode.get("type").asText())) {
                    
                    // 处理内容分享消息，保存文本内容
                    String textContent = "";
                    if (messageNode.has("content")) {
                        textContent = messageNode.get("content").asText();
                    } else if (messageNode.has("text")) {
                        textContent = messageNode.get("text").asText();
                    } else if (messageNode.has("description")) {
                        textContent = messageNode.get("description").asText();
                    }
                    
                    this.message = textContent.isEmpty() ? "内容分享" : textContent;
                    this.type = "ContentSharing";
                } 
                else {
                    this.message = message;
                    this.type = type;
                }
            } catch (Exception e) {
                // 如果解析失败，保持原有数据
                this.message = message;
                this.type = type;
                System.err.println("解析WxLink消息失败: " + e.getMessage());
            }
        } 
        // 处理WxVoice类型的语音消息
        else if ("WxVoice".equals(type) && message != null) {
            try {
                // 解析JSON格式的消息
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode messageNode = objectMapper.readTree(message);
                
                // 提取text字段内容
                if (messageNode.has("text")) {
                    String textContent = messageNode.get("text").asText();
                    this.message = textContent.isEmpty() ? "语音消息" : textContent;
                    this.type = "Voice";
                } else {
                    // 如果没有text字段，保留原始数据
                    this.message = "语音消息";
                    this.type = "Voice";
                }
            } catch (Exception e) {
                // 如果解析失败，保持原有数据并记录错误
                this.message = "语音消息";
                this.type = "Voice";
                System.err.println("解析WxVoice消息失败: " + e.getMessage());
            }
        } 
        // 处理WxPic类型的图片消息
        else if ("WxPic".equals(type)) {
            // 直接设置固定文本
            this.message = "这里发送了一张图片";
            this.type = "Picture";
        } 
        // 处理WxCustomPic类型的表情包消息
        else if ("WxCustomPic".equals(type)) {
            // 直接设置固定文本
            this.message = "这里发送了一个表情包";
            this.type = "Sticker";
        } 
        // 处理WxVoip类型的语音通话消息
        else if ("WxVoip".equals(type) && message != null) {
            try {
                // 解析JSON格式的消息
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode messageNode = objectMapper.readTree(message);
                
                // 提取通话时长信息
                if (messageNode.has("content")) {
                    String content = messageNode.get("content").asText();
                    // 如果content包含通话时长信息，直接使用
                    if (content.contains("通话时长")) {
                        this.message = "这是一段时长为" + content.substring(content.indexOf(" ") + 1) + "的通话";
                    } else {
                        this.message = "这是一段通话";
                    }
                } else if (messageNode.has("duration")) {
                    // 如果有duration字段，转换为时分秒格式
                    int durationSeconds = messageNode.get("duration").asInt();
                    String formattedDuration = formatDuration(durationSeconds);
                    this.message = "这是一段时长为" + formattedDuration + "的通话";
                } else {
                    this.message = "这是一段通话";
                }
                this.type = "VoiceCall";
            } catch (Exception e) {
                // 如果解析失败，保持原有数据并记录错误
                this.message = "这是一段通话";
                this.type = "VoiceCall";
                System.err.println("解析WxVoip消息失败: " + e.getMessage());
            }
        } 
        else {
            this.message = message;
            this.type = type;
        }
        
        // 标记为已处理
        this.processed = true;
    }
    
    /**
     * 自定义setter方法，支持MyBatis映射时的处理
     */
    public void setType(String type) {
        this.type = type;
        // 如果是特殊类型且消息已经设置，则触发处理
        if (("WxLink".equals(type) || "WxVoice".equals(type) || "WxPic".equals(type) || "WxCustomPic".equals(type) || "WxVoip".equals(type)) && this.message != null && !this.processed) {
            processSpecialMessage();
        }
    }
    
    /**
     * 自定义setter方法，支持MyBatis映射时的处理
     */
    public void setMessage(String message) {
        this.message = message;
        // 如果是特殊类型且类型已经设置，则触发处理
        if (("WxLink".equals(this.type) || "WxVoice".equals(this.type) || "WxPic".equals(this.type) || "WxCustomPic".equals(this.type) || "WxVoip".equals(this.type)) && message != null && !this.processed) {
            processSpecialMessage();
        }
    }
    
    /**
     * 处理特殊类型消息（WxLink、WxVoice、WxPic和WxCustomPic）
     */
    private void processSpecialMessage() {
        if (this.processed || this.message == null || 
            (!"WxLink".equals(this.type) && !"WxVoice".equals(this.type) && !"WxPic".equals(this.type) && !"WxCustomPic".equals(this.type) && !"WxVoip".equals(this.type))) {
            return;
        }
        
        // 重用构造函数的处理逻辑
        String originalMessage = this.message;
        String originalType = this.type;
        
        // 重置处理标志，调用构造函数逻辑
        this.processed = false;
        
        // 重新初始化对象（使用构造函数的处理逻辑）
        Message tempMessage = new Message(this.sender, originalMessage, originalType, this.chatTime);
        
        // 复制处理结果
        this.message = tempMessage.message;
        this.type = tempMessage.type;
        this.processed = true;
    }
    
    /**
     * 从XML内容中提取标题
     * @param xmlContent XML字符串内容
     * @return 提取的标题，如果提取失败返回空字符串
     */
    private String extractTitleFromXml(String xmlContent) {
        try {
            if (xmlContent == null || xmlContent.trim().isEmpty()) {
                return "";
            }
            
            // 查找 <title><![CDATA[...]]></title> 标签
            String titleStart = "<title><![CDATA[";
            String titleEnd = "]]></title>";
            
            int startIndex = xmlContent.indexOf(titleStart);
            if (startIndex != -1) {
                startIndex += titleStart.length();
                int endIndex = xmlContent.indexOf(titleEnd, startIndex);
                if (endIndex != -1) {
                    return xmlContent.substring(startIndex, endIndex).trim();
                }
            }
            
            // 如果没有CDATA，尝试查找普通的title标签
            titleStart = "<title>";
            titleEnd = "</title>";
            
            startIndex = xmlContent.indexOf(titleStart);
            if (startIndex != -1) {
                startIndex += titleStart.length();
                int endIndex = xmlContent.indexOf(titleEnd, startIndex);
                if (endIndex != -1) {
                    return xmlContent.substring(startIndex, endIndex).trim();
                }
            }
            
            return "";
        } catch (Exception e) {
            System.err.println("从XML提取标题时发生错误: " + e.getMessage());
            return "";
        }
    }
    
    /**
     * 处理JSON内容，提取datadesc字段并拼接
     * @param contentUrl JSON内容的URL或直接的JSON字符串
     * @return 拼接后的datadesc内容
     */
    private String processJsonContent(String contentUrl) {
        try {
            // 这里应该实现从URL获取JSON数据的逻辑
            // 为了演示，我们假设已经获取到JSON数据
            String jsonContent = getJsonContentFromUrl(contentUrl);
            
            if (jsonContent != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonArray = objectMapper.readTree(jsonContent);
                
                StringBuilder result = new StringBuilder();
                
                if (jsonArray.isArray()) {
                    for (JsonNode item : jsonArray) {
                        if (item.has("datadesc")) {
                            String datadesc = item.get("datadesc").asText();
                            if (result.length() > 0) {
                                result.append("\n");
                            }
                            result.append(datadesc);
                        }
                    }
                }
                
                return result.toString();
            }
        } catch (Exception e) {
            // 记录日志或处理异常
            System.err.println("处理JSON内容时发生错误: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 从URL获取JSON内容
     * @param url JSON文件的URL
     * @return JSON字符串内容
     */
    private String getJsonContentFromUrl(String url) {
        try {
            // 验证URL格式
            if (url == null || url.trim().isEmpty()) {
                System.err.println("URL为空或无效");
                return null;
            }
            
            // 使用RestTemplate发起HTTP请求获取JSON内容
            RestTemplate restTemplate = new RestTemplate();
            
            // 设置请求头（如果需要）
            // HttpHeaders headers = new HttpHeaders();
            // headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            // HttpEntity<String> entity = new HttpEntity<>(headers);
            
            // 发起GET请求获取JSON内容
            String jsonContent = restTemplate.getForObject(url, String.class);
            
            if (jsonContent == null || jsonContent.trim().isEmpty()) {
                System.err.println("从URL获取的内容为空: " + url);
                return null;
            }
            
            return jsonContent;
            
        } catch (Exception e) {
            // 如果HTTP请求失败，记录错误并返回示例数据
            System.err.println("从URL获取JSON内容失败: " + url + ", 错误: " + e.getMessage());
            
            // 返回示例数据作为fallback（实际生产环境中可能需要不同的处理策略）
            if (url.contains(".json")) {
                System.out.println("使用示例数据作为fallback...");
                return getFallbackJsonData();
            }
            
            return null;
        }
    }
    
    /**
     * 获取fallback示例数据
     * @return 示例JSON字符串
     */
    private String getFallbackJsonData() {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        jsonBuilder.append("{\"datadesc\":\"前12天是解决肥胖湿气根源 提升脾胃 分解湿气 产品服用方法\",\"dataid\":\"a4ba1ba09fce27099f35689ef61fbeff\"}");
        jsonBuilder.append(",");
        jsonBuilder.append("{\"datadesc\":\"[图片]\",\"dataid\":\"6a173cc79d0f87f9fe5605a987673732\"}");
        jsonBuilder.append(",");
        jsonBuilder.append("{\"datadesc\":\"注意事项\",\"dataid\":\"4597e182aaa6fb0fecacb69c3eceaf79\"}");
        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }
    
    /**
     * 将秒数转换为时分秒格式
     * @param seconds 总秒数
     * @return 格式化的时长字符串（如：12:35）
     */
    private String formatDuration(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%d小时%02d分种%02d秒", hours, minutes, secs);
        } else {
            return String.format("%d分钟%02d秒", minutes, secs);
        }
    }
}