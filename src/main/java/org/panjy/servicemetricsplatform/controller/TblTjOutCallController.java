package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.TblTjOutCall;
import org.panjy.servicemetricsplatform.service.TblTjOutCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Tbl_Tj_OutCall控制器
 */
@RestController
@RequestMapping("/api/tbl-tj-out-call")
public class TblTjOutCallController {
    
    @Autowired
    private TblTjOutCallService tblTjOutCallService;
    
    /**
     * 根据用户ID查询符合条件的第一条记录
     * 
     * @param userId 用户ID
     * @return 查询结果
     */
    @GetMapping("/first-record/{userId}")
    public TblTjOutCall getFirstRecordByUserId(@PathVariable String userId) {
        return tblTjOutCallService.getFirstRecordByUserId(userId);
    }
    
    /**
     * 查询所有符合条件的客户ID（去重）
     * 
     * @return 客户ID列表
     */
    @GetMapping("/distinct-client-ids")
    public List<String> getDistinctClientIds() {
        return tblTjOutCallService.getDistinctClientIds();
    }
    
    /**
     * 处理所有客户的首通电话数据并插入到汇总表
     * 
     * @return 处理结果
     */
    @PostMapping("/process-all-first-call-summary")
    public ResponseEntity<Map<String, Object>> processAllFirstCallSummary() {
        try {
            int processedCount = tblTjOutCallService.processAndInsertFirstCallSummary();
            
            // 构建响应结果
            Map<String, Object> response = Map.of(
                "success", true,
                "processedCount", processedCount,
                "message", "成功处理并插入了 " + processedCount + " 条首通电话摘要记录"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 构建错误响应结果
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "处理首通电话摘要记录时发生错误: " + e.getMessage()
            );
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 处理单个客户的首通电话数据并插入到汇总表
     * 
     * @param clientId 客户ID
     * @return 处理结果
     */
    @PostMapping("/process-single-first-call-summary/{clientId}")
    public ResponseEntity<Map<String, Object>> processSingleFirstCallSummary(@PathVariable String clientId) {
        try {
            boolean success = tblTjOutCallService.processAndInsertSingleFirstCallSummary(clientId);
            
            // 构建响应结果
            Map<String, Object> response;
            if (success) {
                response = Map.of(
                    "success", true,
                    "message", "成功处理并插入了客户 " + clientId + " 的首通电话摘要记录"
                );
            } else {
                response = Map.of(
                    "success", false,
                    "message", "未找到客户 " + clientId + " 的符合条件的首通电话记录"
                );
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 构建错误响应结果
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "处理客户 " + clientId + " 的首通电话摘要记录时发生错误: " + e.getMessage()
            );
            
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据用户ID查询通话次数
     * 
     * @param userId 用户ID
     * @return 通话次数
     */
    @GetMapping("/call-count/{userId}")
    public Integer getCallCountByUserId(@PathVariable String userId) {
        return tblTjOutCallService.getCallCountByUserId(userId);
    }
    
    /**
     * 处理所有用户的通话次数并插入到统计表
     * 
     * @return 处理结果
     */
    @PostMapping("/process-user-call-count")
    public ResponseEntity<Map<String, Object>> processUserCallCount() {
        try {
            int processedCount = tblTjOutCallService.processAndInsertUserCallCount();
            
            // 构建响应结果
            Map<String, Object> response = Map.of(
                "success", true,
                "processedCount", processedCount,
                "message", "成功处理并插入了 " + processedCount + " 条用户通话次数统计记录"
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 构建错误响应结果
            Map<String, Object> response = Map.of(
                "success", false,
                "message", "处理用户通话次数统计时发生错误: " + e.getMessage()
            );
            
            return ResponseEntity.status(500).body(response);
        }
    }
}