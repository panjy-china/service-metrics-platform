package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.message.Message;
import org.panjy.servicemetricsplatform.entity.analysis.WechatMessageAnalyzeAddress;
import org.panjy.servicemetricsplatform.mapper.message.MessageMapper;
import org.panjy.servicemetricsplatform.mapper.analysis.WechatMessageAnalyzeAddressMapper;
import org.panjy.servicemetricsplatform.service.analysis.LLMAnalysisService;
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

    @GetMapping("/distribution")
    public ResponseEntity<?> getUserAddressDistribution() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("开始处理全国各地用户分布查询请求");
            
            // 1. 查询每个用户的最新地址（排除北京大兴）
            List<String> userAddresses = wechatMessageAnalyzeAddressMapper.getUserLatestAddresses();
            
            if (userAddresses == null || userAddresses.isEmpty()) {
                System.out.println("没有找到有效的用户地址数据");
                response.put("success", true);
                response.put("message", "没有找到有效的用户地址数据");
                response.put("data", new HashMap<>());
                response.put("totalUsers", 0);
                return ResponseEntity.ok(response);
            }
            
            System.out.println("查询到的用户地址数量: " + userAddresses.size());
            
            // 2. 统计地址分布（取前两个字符作为key）
            Map<String, Integer> addressDistribution = calculateAddressDistribution(userAddresses);
            
            // 3. 对结果进行排序（按数量降序）
            List<Map<String, Object>> sortedDistribution = addressDistribution.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("province", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .sorted((a, b) -> Integer.compare((Integer) b.get("count"), (Integer) a.get("count")))
                .collect(java.util.stream.Collectors.toList());
            
            // 构建成功响应
            response.put("success", true);
            response.put("message", "用户地址分布查询成功");
            response.put("data", sortedDistribution);
            response.put("totalUsers", userAddresses.size());
            response.put("provinceCount", addressDistribution.size());
            response.put("queryTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            
            System.out.println("用户地址分布统计完成，涉及 " + addressDistribution.size() + " 个省份/地区");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("用户地址分布查询过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "用户地址分布查询失败: " + e.getMessage());
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
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
            
            System.out.println("找到包含地址关键词的消息数量: " + messagesLikeAddress.size());
            
            // 2. 分批处理消息，避免超时和内存问题
            BatchProcessResult batchResult = processBatchMessages(messagesLikeAddress);
            
            // 构建成功响应
            response.put("success", true);
            response.put("message", "地址分析处理完成");
            response.put("data", batchResult.getAllAnalyzedMessages());
            response.put("totalFound", messagesLikeAddress.size());
            response.put("processedCount", batchResult.getTotalProcessed());
            response.put("extractedAddresses", batchResult.getTotalExtracted());
            response.put("savedToDatabase", batchResult.getTotalSaved());
            response.put("batchCount", batchResult.getBatchCount());
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
    
    /**
     * 计算地址分布（取前两个字符作为key，对内蒙古和黑龙江特殊处理）
     * @param userAddresses 用户地址列表
     * @return 地址分布统计结果
     */
    private Map<String, Integer> calculateAddressDistribution(List<String> userAddresses) {
        Map<String, Integer> distribution = new HashMap<>();
        
        for (String address : userAddresses) {
            if (address == null || address.trim().isEmpty() || 
                "未识别".equals(address) || "提取失败".equals(address)) {
                continue;
            }
            
            String province = extractProvinceKey(address.trim());
            if (province != null && !province.isEmpty()) {
                distribution.put(province, distribution.getOrDefault(province, 0) + 1);
            }
        }
        
        return distribution;
    }
    
    /**
     * 提取省份关键字（对内蒙古和黑龙江进行特殊处理）
     * @param address 地址字符串
     * @return 省份关键字
     */
    private String extractProvinceKey(String address) {
        if (address == null || address.length() < 2) {
            return null;
        }
        
        // 特殊处理：内蒙古
        if (address.startsWith("内蒙")) {
            return "内蒙古";
        }
        
        // 特殊处理：黑龙江
        if (address.startsWith("黑龙")) {
            return "黑龙江";
        }
        
        // 默认取前两个字符
        return address.substring(0, 2);
    }
    
    /**
     * 分批处理消息，避免超时和内存问题
     * @param messagesLikeAddress 包含地址关键词的消息列表
     * @return 批处理结果
     */
    private BatchProcessResult processBatchMessages(List<Message> messagesLikeAddress) {
        final int BATCH_SIZE = 10; // 每批处理10条消息，避免LLM API超时
        final int MAX_RETRY_ATTEMPTS = 3; // 最大重试次数
        final long RETRY_DELAY_MS = 2000; // 重试延迟2秒
        
        BatchProcessResult result = new BatchProcessResult();
        int totalBatches = (int) Math.ceil((double) messagesLikeAddress.size() / BATCH_SIZE);
        
        System.out.println("开始分批处理，总计 " + messagesLikeAddress.size() + " 条消息，分为 " + totalBatches + " 批处理");
        
        for (int batchIndex = 0; batchIndex < totalBatches; batchIndex++) {
            int startIndex = batchIndex * BATCH_SIZE;
            int endIndex = Math.min(startIndex + BATCH_SIZE, messagesLikeAddress.size());
            List<Message> batchMessages = messagesLikeAddress.subList(startIndex, endIndex);
            
            System.out.println("处理第 " + (batchIndex + 1) + "/" + totalBatches + " 批，消息数量: " + batchMessages.size());
            
            // 重试机制处理当前批次
            boolean batchSuccess = false;
            int retryCount = 0;
            
            while (!batchSuccess && retryCount < MAX_RETRY_ATTEMPTS) {
                try {
                    // LLM 分析当前批次消息
                    List<Message> analyzedMessages = llmAnalysisService.analyzeAddressWithLLMAsMessages(batchMessages);
                    
                    if (analyzedMessages != null && !analyzedMessages.isEmpty()) {
                        // 转换为实体对象
                        List<WechatMessageAnalyzeAddress> addressEntities = 
                            convertMessagesToAddressEntities(analyzedMessages, batchMessages);
                        
                        // 立即写入数据库
                        int insertedCount = 0;
                        if (!addressEntities.isEmpty()) {
                            insertedCount = wechatMessageAnalyzeAddressMapper.batchInsert(addressEntities);
                            System.out.println("第 " + (batchIndex + 1) + " 批成功插入地址分析结果: " + insertedCount + " 条");
                        }
                        
                        // 更新统计结果
                        result.addBatchResult(batchMessages.size(), analyzedMessages.size(), insertedCount, analyzedMessages);
                        batchSuccess = true;
                        
                        // 批次间添加短暂延迟，避免API限流
                        if (batchIndex < totalBatches - 1) {
                            Thread.sleep(1000); // 1秒延迟
                        }
                        
                    } else {
                        System.out.println("第 " + (batchIndex + 1) + " 批LLM分析未找到有效地址信息");
                        result.addBatchResult(batchMessages.size(), 0, 0, new ArrayList<>());
                        batchSuccess = true;
                    }
                    
                } catch (Exception e) {
                    retryCount++;
                    System.err.println("第 " + (batchIndex + 1) + " 批处理失败，重试次数: " + retryCount + "/" + MAX_RETRY_ATTEMPTS + ", 错误: " + e.getMessage());
                    
                    if (retryCount < MAX_RETRY_ATTEMPTS) {
                        try {
                            Thread.sleep(RETRY_DELAY_MS * retryCount); // 递增延迟
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    } else {
                        System.err.println("第 " + (batchIndex + 1) + " 批处理最终失败，跳过该批次");
                        result.addBatchResult(batchMessages.size(), 0, 0, new ArrayList<>());
                    }
                }
            }
        }
        
        System.out.println("分批处理完成，总计处理: " + result.getTotalProcessed() + 
                         " 条，提取地址: " + result.getTotalExtracted() + 
                         " 条，保存到数据库: " + result.getTotalSaved() + " 条");
        
        return result;
    }
    
    /**
     * 批处理结果类
     */
    private static class BatchProcessResult {
        private int totalProcessed = 0;
        private int totalExtracted = 0;
        private int totalSaved = 0;
        private int batchCount = 0;
        private List<Message> allAnalyzedMessages = new ArrayList<>();
        
        public void addBatchResult(int processed, int extracted, int saved, List<Message> analyzedMessages) {
            this.totalProcessed += processed;
            this.totalExtracted += extracted;
            this.totalSaved += saved;
            this.batchCount++;
            this.allAnalyzedMessages.addAll(analyzedMessages);
        }
        
        public int getTotalProcessed() { return totalProcessed; }
        public int getTotalExtracted() { return totalExtracted; }
        public int getTotalSaved() { return totalSaved; }
        public int getBatchCount() { return batchCount; }
        public List<Message> getAllAnalyzedMessages() { return allAnalyzedMessages; }
    }
}
