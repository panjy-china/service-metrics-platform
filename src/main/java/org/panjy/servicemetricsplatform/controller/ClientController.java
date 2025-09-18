package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.Client;
import org.panjy.servicemetricsplatform.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 客户信息控制器
 * 
 * @author System Generated
 */
@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final ClientService clientService;
    
    // 构造函数
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * 执行客户备注信息分析
     * GET /api/clients/valid-demo
     * 
     * @return 分析后的客户列表和统计信息
     */
    @GetMapping("/valid-demo")
    public ResponseEntity<Map<String, Object>> getClientsWithValidDemo() {
        try {
            log.info("接收到执行客户备注分析请求");
            
            // 执行大模型分析
            ClientService.AnalysisResult result = clientService.analyzeClientDemoWithLLM();
            List<Client> analyzedClients = result.getAnalyzedClients();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "分析成功");
            response.put("data", analyzedClients);
            response.put("total", analyzedClients != null ? analyzedClients.size() : 0);
            
            // 添加分析统计信息
            Map<String, Object> statistics = new HashMap<>();
            if (analyzedClients != null) {
                long maleCount = analyzedClients.stream()
                        .filter(c -> "男".equals(c.getColGender()) || "男性".equals(c.getColGender()))
                        .count();
                long femaleCount = analyzedClients.stream()
                        .filter(c -> "女".equals(c.getColGender()) || "女性".equals(c.getColGender()))
                        .count();
                long unknownGenderCount = analyzedClients.stream()
                        .filter(c -> "未知".equals(c.getColGender()) || c.getColGender() == null)
                        .count();
                
                long validAgeCount = analyzedClients.stream()
                        .filter(c -> c.getColAge() != null && c.getColAge() > 0)
                        .count();
                        
                long validHeightCount = analyzedClients.stream()
                        .filter(c -> c.getColHeight() != null && c.getColHeight() > 0)
                        .count();
                        
                long validWeightCount = analyzedClients.stream()
                        .filter(c -> c.getColWeight() != null && c.getColWeight() > 0)
                        .count();
                
                statistics.put("maleCount", maleCount);
                statistics.put("femaleCount", femaleCount);
                statistics.put("unknownGenderCount", unknownGenderCount);
                statistics.put("validAgeCount", validAgeCount);
                statistics.put("validHeightCount", validHeightCount);
                statistics.put("validWeightCount", validWeightCount);
            }
            response.put("statistics", statistics);
            
            // 添加数据库更新统计
            Map<String, Object> updateInfo = new HashMap<>();
            updateInfo.put("totalClients", result.getTotalClients());
            updateInfo.put("updatedCount", result.getUpdatedCount());
            updateInfo.put("successfulBatches", result.getSuccessfulBatches());
            updateInfo.put("updateRate", result.getTotalClients() > 0 ? 
                    String.format("%.2f%%", (double) result.getUpdatedCount() / result.getTotalClients() * 100) : "0.00%");
            response.put("updateInfo", updateInfo);
            
            log.info("返回客户备注分析结果，共{}条记录，数据库更新{}条", 
                    analyzedClients != null ? analyzedClients.size() : 0, result.getUpdatedCount());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("执行客户备注分析失败", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "分析失败: " + e.getMessage());
            response.put("data", null);
            response.put("total", 0);
            response.put("statistics", null);
            response.put("updateInfo", null);
            
            return ResponseEntity.status(500).body(response);
        }
    }

}