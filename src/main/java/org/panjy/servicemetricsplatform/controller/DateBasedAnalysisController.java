package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.service.analysis.LLMAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 基于日期的分析控制器
 * 提供基于指定日期的对话记录分析功能
 */
@RestController
@RequestMapping("/api/date-based-analysis")
public class DateBasedAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(DateBasedAnalysisController.class);

    @Autowired
    private LLMAnalysisService llmAnalysisService;

    /**
     * 分析指定日期之后用户首次聊天两天内的对话记录
     *
     * @param dateStr 限定日期字符串 (格式: yyyy-MM-dd)
     * @return 分析结果
     */
    @PostMapping("/analyze-conversations-after-date/{dateStr}")
    public ResponseEntity<Map<String, Object>> analyzeConversationsAfterDate(@PathVariable String dateStr) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("收到分析指定日期之后用户对话记录的请求，日期: {}", dateStr);
            
            // 解析日期参数
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            
            // 调用服务进行分析
            String result = llmAnalysisService.analyzeConversationsAfterDate(date);
            
            response.put("success", true);
            response.put("message", "分析完成");
            response.put("data", result);
            response.put("date", dateStr);
            
            logger.info("分析指定日期之后用户对话记录完成，日期: {}", dateStr);
            return ResponseEntity.ok(response);
        } catch (ParseException e) {
            logger.error("日期格式错误: {}", dateStr, e);
            
            response.put("success", false);
            response.put("message", "日期格式错误，请使用 yyyy-MM-dd 格式");
            response.put("date", dateStr);
            response.put("errorCode", "INVALID_DATE_FORMAT");
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            logger.error("分析指定日期之后用户对话记录时发生异常，日期: " + dateStr, e);
            
            response.put("success", false);
            response.put("message", "分析过程中发生错误: " + e.getMessage());
            response.put("date", dateStr);
            response.put("errorCode", "ANALYSIS_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}