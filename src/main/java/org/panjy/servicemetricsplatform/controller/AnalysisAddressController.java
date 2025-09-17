package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.Message;
import org.panjy.servicemetricsplatform.entity.WechatMessageAnalyzeAddress;
import org.panjy.servicemetricsplatform.mapper.mysql.MessageMapper;
import org.panjy.servicemetricsplatform.mapper.mysql.WechatMessageAnalyzeAddressMapper;
import org.panjy.servicemetricsplatform.service.LLMAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/analyze/address")
public class AnalysisAddressController {
    @Autowired
    private LLMAnalysisService llmAnalysisService;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private WechatMessageAnalyzeAddressMapper wechatMessageAnalyzeAddressMapper;

    @GetMapping("/{date}")
    public ResponseEntity<?> findMessagesLikeAddress(
            @PathVariable("date") 
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 验证日期参数
            if (date == null) {
                response.put("success", false);
                response.put("message", "日期参数不能为空");
                response.put("errorCode", "INVALID_DATE");
                return ResponseEntity.badRequest().body(response);
            }
            
            System.out.println("开始处理地址分析请求，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            // 1. 查询包含地址关键词的消息
            List<Message> messagesLikeAddress = messageMapper.findMessagesLikeAddress(date);
            
            if (messagesLikeAddress == null || messagesLikeAddress.isEmpty()) {
                System.out.println("没有找到包含地址关键词的消息");
                response.put("success", true);
                response.put("message", "没有找到包含地址关键词的消息");
                response.put("data", new ArrayList<>());
                response.put("totalFound", 0);
                return ResponseEntity.ok(response);
            }
            
            // 限制处理数量，避免超时
            List<Message> limitedMessages = messagesLikeAddress;
            
            System.out.println("找到包含地址关键词的消息数量: " + messagesLikeAddress.size() + 
                             ", 实际处理数量: " + limitedMessages.size());
            
            // 2. 使用LLM分析消息中的地址信息
            List<Message> messages = llmAnalysisService.analyzeAddressWithLLMAsMessages(limitedMessages);
            
            if (messages == null || messages.isEmpty()) {
                System.out.println("LLM分析未找到有效地址信息");
                response.put("success", true);
                response.put("message", "LLM分析未找到有效地址信息");
                response.put("data", new ArrayList<>());
                response.put("totalFound", messagesLikeAddress.size());
                response.put("processedCount", limitedMessages.size());
                response.put("extractedAddresses", 0);
                return ResponseEntity.ok(response);
            }
            
            System.out.println("LLM分析得到的地址信息数量: " + messages.size());
            
            // 3. 将messages转变为List<WechatMessageAnalyzeAddress>
            List<WechatMessageAnalyzeAddress> addressEntities = convertMessagesToAddressEntities(messages, limitedMessages);
            
            // 4. 将得到的内容批量插入到数据库
            int insertedCount = 0;
            if (!addressEntities.isEmpty()) {
                insertedCount = wechatMessageAnalyzeAddressMapper.batchInsert(addressEntities);
                System.out.println("成功插入地址分析结果: " + insertedCount + " 条");
            }
            
            // 构建成功响应
            response.put("success", true);
            response.put("message", "地址分析处理完成");
            response.put("data", messages);
            response.put("totalFound", messagesLikeAddress.size());
            response.put("processedCount", limitedMessages.size());
            response.put("extractedAddresses", messages.size());
            response.put("savedToDatabase", insertedCount);
            response.put("queryDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("地址分析处理过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "地址分析处理失败: " + e.getMessage());
            response.put("errorCode", "PROCESSING_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 将Message列表转换为WechatMessageAnalyzeAddress实体列表
     * @param analysisMessages LLM分析后的消息列表
     * @param originalMessages 原始消息列表
     * @return 转换后的实体列表
     */
    private List<WechatMessageAnalyzeAddress> convertMessagesToAddressEntities(List<Message> analysisMessages, List<Message> originalMessages) {
        List<WechatMessageAnalyzeAddress> entities = new ArrayList<>();
        
        for (Message analysisMsg : analysisMessages) {
            if ("AddressAnalysis".equals(analysisMsg.getType())) {
                // 查找对应的原始消息
                Message originalMsg = findOriginalMessage(analysisMsg.getSender(), originalMessages);
                
                if (originalMsg != null) {
                    // 检查是否已存在
                    Long wechatTime = convertLocalDateTimeToTimestamp(originalMsg.getChatTime());
                    if (wechatMessageAnalyzeAddressMapper.countByWechatIdAndTime(analysisMsg.getSender(), wechatTime) == 0) {
                        // 从分析结果中提取地址信息
                        String extractedAddress = extractAddressFromAnalysisMessage(analysisMsg.getMessage());
                        
                        WechatMessageAnalyzeAddress entity = WechatMessageAnalyzeAddress.builder()
                                .wechatId(analysisMsg.getSender())
                                .msgType(convertMessageTypeToInt(originalMsg.getType()))
                                .wechatTime(wechatTime)
                                .content(originalMsg.getMessage())
                                .address(extractedAddress)
                                .build();
                        
                        entities.add(entity);
                    } else {
                        System.out.println("地址分析记录已存在，跳过: 微信ID=" + analysisMsg.getSender() + ", 时间=" + wechatTime);
                    }
                }
            }
        }
        
        return entities;
    }
    
    /**
     * 查找对应的原始消息
     * @param sender 发送者
     * @param originalMessages 原始消息列表
     * @return 匹配的原始消息
     */
    private Message findOriginalMessage(String sender, List<Message> originalMessages) {
        for (Message msg : originalMessages) {
            if (sender.equals(msg.getSender())) {
                return msg;
            }
        }
        return null;
    }
    
    /**
     * 从分析消息中提取地址信息
     * @param analysisMessage 分析消息内容
     * @return 提取的地址
     */
    private String extractAddressFromAnalysisMessage(String analysisMessage) {
        if (analysisMessage == null || !analysisMessage.contains("地址分析结果:")) {
            return "未识别";
        }
        
        try {
            // 提取 "地址分析结果: " 后面的内容
            int startIndex = analysisMessage.indexOf("地址分析结果: ") + "地址分析结果: ".length();
            int endIndex = analysisMessage.indexOf(" (原文:");
            
            if (endIndex == -1) {
                endIndex = analysisMessage.length();
            }
            
            if (startIndex < endIndex) {
                return analysisMessage.substring(startIndex, endIndex).trim();
            }
        } catch (Exception e) {
            System.err.println("提取地址信息时发生错误: " + e.getMessage());
        }
        
        return "提取失败";
    }
    
    /**
     * 生成唯一ID
     * @param message 消息对象
     * @return 唯一ID
     */
    private Long generateUniqueId(Message message) {
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
    private Long convertLocalDateTimeToTimestamp(java.time.LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return System.currentTimeMillis();
        }
        return localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
