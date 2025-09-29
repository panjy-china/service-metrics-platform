package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.service.LLMAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 大模型分析控制器
 */
@RestController
@RequestMapping("/api/llm-analysis")
public class LLMAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(LLMAnalysisController.class);

    @Autowired
    private LLMAnalysisService llmAnalysisService;

    /**
     * 分析用户首次聊天两天内的对话记录，判断用户是否发送过舌苔和体型照片
     *
     * @param wechatId 用户微信ID
     * @return 分析结果
     */
    @GetMapping("/body-photos/{wechatId}")
    public ResponseEntity<String> analyzeBodyPhotos(@PathVariable String wechatId) {
        logger.info("收到分析用户舌苔和体型照片的请求，用户微信ID: {}", wechatId);
        
        try {
            // 检查参数
            if (wechatId == null || wechatId.trim().isEmpty()) {
                logger.warn("请求参数错误，微信ID为空，用户微信ID: {}", wechatId);
                String errorResponse = "{\n" +
                        "  \"error\": \"参数错误\",\n" +
                        "  \"message\": \"微信ID不能为空\"\n" +
                        "}";
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            logger.info("开始调用LLM分析服务，用户微信ID: {}", wechatId);
            String result = llmAnalysisService.analyzeBodyPhotos(wechatId);
            logger.info("LLM分析服务调用完成，用户微信ID: {}", wechatId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            // 构建详细的错误响应结果
            String errorMessage = e.getMessage();
            if (errorMessage == null || errorMessage.isEmpty()) {
                errorMessage = "未知错误";
            }
            
            logger.error("分析用户舌苔和体型照片时发生异常，用户微信ID: " + wechatId, e);
            
            String errorResponse = "{\n" +
                    "  \"error\": \"分析过程中发生错误\",\n" +
                    "  \"message\": \"" + errorMessage + "\",\n" +
                    "  \"exception\": \"" + e.getClass().getName() + "\"\n" +
                    "}";
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}