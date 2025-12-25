package org.panjy.servicemetricsplatform.controller;


import org.panjy.servicemetricsplatform.entity.message.Conversation;
import org.panjy.servicemetricsplatform.entity.message.UserGuidanceStat;
import org.panjy.servicemetricsplatform.service.analysis.LLMAnalysisService;
import org.panjy.servicemetricsplatform.service.message.UserGuidanceStatService;
import org.panjy.servicemetricsplatform.service.newuser.StrategicLayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 饮食指导分析控制器
 * 提供饮食指导触达分析相关的API接口
 */
@RestController
@RequestMapping("/api/dietary-guidance")
public class DietaryGuidanceAnalysisController {
    
    private static final Logger logger = LoggerFactory.getLogger(DietaryGuidanceAnalysisController.class);
    
    @Autowired
    private StrategicLayerService strategicLayerService;
    
    @Autowired
    private LLMAnalysisService llmAnalysisService;
    
    @Autowired
    private UserGuidanceStatService userGuidanceStatService;
    
    /**
     * 批量分析指定日期之后的饮食指导触达情况
     * 
     * @param startDate 开始日期，格式为 yyyy-MM-dd
     * @return 分析结果
     */
    @PostMapping("/batch-analyze")
    public ResponseEntity<String> batchAnalyzeDietaryGuidance(@RequestParam String startDate) {
        logger.info("收到批量分析饮食指导触达情况的请求，开始日期: {}", startDate);
        
        try {
            // 1. 解析日期参数
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDate);
            logger.info("日期解析完成，开始日期: {}", start);
            
            // 2. 查询指定日期之后的所有对话记录
            logger.info("开始查询指定日期之后的所有对话记录");
            List<Conversation> conversations = strategicLayerService.findConversationsByDate(start);
            logger.info("查询完成，共找到 {} 条对话记录", conversations != null ? conversations.size() : 0);
            
            if (conversations == null || conversations.isEmpty()) {
                String result = "{\n  \"message\": \"未找到指定日期之后的对话记录\",\n  \"count\": 0\n}";
                return ResponseEntity.ok(result);
            }
            
            // 3. 逐条分析对话记录并保存结果
            List<UserGuidanceStat> guidanceStats = new ArrayList<>();
            int successCount = 0;
            int errorCount = 0;
            
            logger.info("开始逐条分析对话记录");
            for (Conversation conversation : conversations) {
                if (conversation == null || conversation.getWechatId() == null) {
                    logger.warn("跳过无效的对话记录");
                    errorCount++;
                    continue;
                }
                
                try {
                    logger.info("开始分析用户 {} 的对话记录", conversation.getWechatId());
                    // 使用大模型分析饮食指导触达情况
                    UserGuidanceStat guidanceStat = llmAnalysisService.analyzeDietaryGuidance(conversation);
                    
                    if (guidanceStat != null) {
                        // 保存分析结果到数据库
                        int saved = userGuidanceStatService.save(guidanceStat);
                        guidanceStats.add(guidanceStat);
                        successCount++;
                        logger.info("用户 {} 的对话记录分析完成并保存，受影响行数: {}", conversation.getWechatId(), saved);
                    } else {
                        logger.warn("用户 {} 的对话记录分析返回空结果", conversation.getWechatId());
                        errorCount++;
                    }
                } catch (Exception e) {
                    logger.error("分析用户 {} 的对话记录时发生异常", conversation.getWechatId(), e);
                    errorCount++;
                }
            }
            
            // 4. 构造返回结果
            String result = "{\n" +
                    "  \"message\": \"批量分析完成\",\n" +
                    "  \"totalCount\": " + conversations.size() + ",\n" +
                    "  \"successCount\": " + successCount + ",\n" +
                    "  \"errorCount\": " + errorCount + "\n" +
                    "}";
            
            logger.info("批量分析完成，总记录数: {}，成功: {}，失败: {}", conversations.size(), successCount, errorCount);
            return ResponseEntity.ok(result);
        } catch (ParseException e) {
            logger.error("日期格式错误: " + startDate, e);
            String errorResponse = "{\n  \"error\": \"日期格式错误\",\n  \"message\": \"请使用 yyyy-MM-dd 格式\"\n}";
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("批量分析饮食指导触达情况时发生异常", e);
            String errorResponse = "{\n  \"error\": \"服务器内部错误\",\n  \"message\": \"" + e.getMessage() + "\"\n}";
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 分析指定用户的饮食指导触达情况
     * 
     * @param wechatId 用户微信ID
     * @return 分析结果
     */
    @PostMapping("/analyze/{wechatId}")
    public ResponseEntity<String> analyzeDietaryGuidance(@PathVariable String wechatId) {
        logger.info("收到分析指定用户饮食指导触达情况的请求，用户微信ID: {}", wechatId);
        
        try {
            // 检查参数
            if (wechatId == null || wechatId.trim().isEmpty()) {
                logger.warn("请求参数错误，微信ID为空");
                String errorResponse = "{\n  \"error\": \"参数错误\",\n  \"message\": \"微信ID不能为空\"\n}";
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // 这里可以根据需要实现特定用户的分析逻辑
            // 由于LLMAnalysisService.analyzeDietaryGuidance需要Conversation对象
            // 我们需要先获取该用户的对话记录
            
            String result = "{\n" +
                    "  \"message\": \"功能待实现\",\n" +
                    "  \"wechatId\": \"" + wechatId + "\"\n" +
                    "}";
            
            logger.info("指定用户饮食指导触达情况分析完成，用户微信ID: {}", wechatId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("分析指定用户饮食指导触达情况时发生异常，用户微信ID: " + wechatId, e);
            String errorResponse = "{\n  \"error\": \"服务器内部错误\",\n  \"message\": \"" + e.getMessage() + "\"\n}";
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}