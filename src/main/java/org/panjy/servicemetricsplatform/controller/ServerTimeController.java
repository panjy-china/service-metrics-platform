package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.ServerTime;
import org.panjy.servicemetricsplatform.service.ServerTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务时间控制器
 * 提供服务时间相关的API接口
 */
@RestController
@RequestMapping("/api/server-time")
public class ServerTimeController {
    
    private static final Logger logger = LoggerFactory.getLogger(ServerTimeController.class);
    
    @Autowired
    private ServerTimeService serverTimeService;
    
    /**
     * 处理指定日期之后的所有记录：查询指定日期后出现的用户id，之后查询该用户id的最早一条记录以及最晚一条记录，将两者之差作为服务时间
     * 
     * @param dateStr 指定日期（格式: yyyy-MM-dd）
     * @return 处理结果
     */
    @PostMapping("/process-after/{dateStr}")
    public ResponseEntity<?> processServerTimeAfterDate(@PathVariable("dateStr") String dateStr) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始处理指定日期之后的所有记录，日期: {}", dateStr);
            
            // 解析日期字符串
            LocalDate date;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                date = LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                logger.error("日期格式错误: {}", dateStr);
                
                response.put("success", false);
                response.put("message", "日期格式错误，请使用 yyyy-MM-dd 格式");
                response.put("date", dateStr);
                response.put("errorCode", "INVALID_DATE_FORMAT");
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // 处理服务时间记录
            boolean result = serverTimeService.processServerTimeAfterDate(date);
            
            if (result) {
                logger.info("处理指定日期之后的所有记录成功，日期: {}", dateStr);
                
                response.put("success", true);
                response.put("message", "处理成功");
                response.put("date", dateStr);
                response.put("description", "已将指定日期之后的所有记录的每个用户服务时间写入到tbl_ServerTime表中");
                
                return ResponseEntity.ok(response);
            } else {
                logger.warn("处理指定日期之后的所有记录失败，日期: {}", dateStr);
                
                response.put("success", false);
                response.put("message", "处理失败，没有找到符合条件的数据");
                response.put("date", dateStr);
                response.put("errorCode", "NO_DATA_FOUND");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            logger.error("处理指定日期之后的所有记录失败: 日期={}", dateStr, e);
            
            response.put("success", false);
            response.put("message", "处理失败: " + e.getMessage());
            response.put("date", dateStr);
            response.put("errorCode", "PROCESSING_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 查询指定日期之后的所有服务时间记录
     * 
     * @param dateStr 指定日期（格式: yyyy-MM-dd）
     * @return 服务时间记录列表
     */
    @GetMapping("/after/{dateStr}")
    public ResponseEntity<?> getServerTimesAfterDate(@PathVariable("dateStr") String dateStr) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始查询指定日期之后的所有服务时间记录，日期: {}", dateStr);
            
            // 解析日期字符串
            LocalDate date;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                date = LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e) {
                logger.error("日期格式错误: {}", dateStr);
                
                response.put("success", false);
                response.put("message", "日期格式错误，请使用 yyyy-MM-dd 格式");
                response.put("date", dateStr);
                response.put("errorCode", "INVALID_DATE_FORMAT");
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            
            // 查询服务时间记录
            List<ServerTime> serverTimes = serverTimeService.getServerTimesAfterDate(date);
            
            logger.info("查询指定日期之后的所有服务时间记录完成，日期: {}, 记录数: {}", dateStr, serverTimes.size());
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("date", dateStr);
            response.put("recordCount", serverTimes.size());
            response.put("serverTimes", serverTimes);
            response.put("description", "指定日期之后的所有服务时间记录");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询指定日期之后的所有服务时间记录失败: 日期={}", dateStr, e);
            
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("date", dateStr);
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 查询所有客户的服务时间记录
     * 
     * @return 服务时间记录列表
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllServerTimes() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始查询所有客户的服务时间记录");
            
            // 查询所有服务时间记录
            List<ServerTime> serverTimes = serverTimeService.getAllServerTimes();
            
            logger.info("查询所有客户的服务时间记录完成，记录数: {}", serverTimes.size());
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("recordCount", serverTimes.size());
            response.put("serverTimes", serverTimes);
            response.put("description", "所有客户的服务时间记录");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询所有客户的服务时间记录失败", e);
            
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}