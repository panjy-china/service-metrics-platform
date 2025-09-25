package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单指标控制器
 * 提供订单统计分析相关的API接口，包括人均订单数、人均销售额等核心业务指标
 */
@RestController
@RequestMapping("/api/order-metrics")
public class OrderMetricsController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderMetricsController.class);
    
    @Autowired
    private OrderService orderService;

    /**
     * 获取指定月份人均成交订单数
     * 
     * @param yearMonth 指定月份（格式: YYYY-MM）
     * @return 指定月份人均订单数统计
     */
    @GetMapping("/avg-orders-per-customer/{yearMonth}")
    public ResponseEntity<?> getMonthlyAvgOrdersPerCustomer(
            @PathVariable("yearMonth") String yearMonth) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始查询指定月份人均成交订单数，月份: {}", yearMonth);
            
            BigDecimal avgOrders = orderService.calculateMonthlyAvgOrdersPerCustomer(yearMonth);
            
            logger.info("指定月份人均成交订单数查询完成: 月份={}, 结果={}", yearMonth, avgOrders);
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("yearMonth", yearMonth);
            response.put("avgOrdersPerCustomer", avgOrders);
            response.put("description", "指定月份人均成交订单数");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询指定月份人均成交订单数失败: 月份={}", yearMonth, e);
            
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取当月人均成交订单数（保持向后兼容）
     * 
     * @return 当月人均订单数统计
     */
    @GetMapping("/avg-orders-per-customer")
    public ResponseEntity<?> getCurrentMonthAvgOrdersPerCustomer() {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始查询当月人均成交订单数");
            
            BigDecimal avgOrders = orderService.calculateCurrentMonthAvgOrdersPerCustomer();
            
            logger.info("当月人均成交订单数查询完成: {}", avgOrders);
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("avgOrdersPerCustomer", avgOrders);
            response.put("description", "当月人均成交订单数");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询当月人均成交订单数失败: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取指定月份人均成交销售额
     * 
     * @param yearMonth 指定月份（格式: YYYY-MM）
     * @return 指定月份人均销售额统计
     */
    @GetMapping("/avg-sales-per-customer/{yearMonth}")
    public ResponseEntity<?> getMonthlySalesPerCustomer(
            @PathVariable("yearMonth") String yearMonth) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始查询指定月份人均成交销售额，月份: {}", yearMonth);
            
            BigDecimal avgSales = orderService.calculateMonthlySalesPerCustomer(yearMonth);
            
            logger.info("指定月份人均成交销售额查询完成: 月份={}, 结果={}", yearMonth, avgSales);
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("yearMonth", yearMonth);
            response.put("avgSalesPerCustomer", avgSales);
            response.put("description", "指定月份人均成交销售额");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询指定月份人均成交销售额失败: 月份={}", yearMonth, e);
            
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取当月人均成交销售额（保持向后兼容）
     * 
     * @return 当月人均销售额统计
     */
    @GetMapping("/avg-sales-per-customer")
    public ResponseEntity<?> getCurrentMonthAvgSalesPerCustomer() {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始查询当月人均成交销售额");
            
            BigDecimal avgSales = orderService.calculateCurrentMonthAvgSalesPerCustomer();
            
            logger.info("当月人均成交销售额查询完成: {}", avgSales);
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("avgSalesPerCustomer", avgSales);
            response.put("description", "当月人均成交销售额");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询当月人均成交销售额失败: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取指定月份综合统计数据（包含同比增长）
     * 包括人均订单数、人均订单数同比上月增长、人均销售额、人均销售额同比上月增长
     * 
     * @param yearMonth 指定月份（格式: YYYY-MM）
     * @return 指定月份综合统计数据及同比增长
     */
    @GetMapping("/monthly-stats/{yearMonth}")
    public ResponseEntity<?> getMonthlyStats(
            @PathVariable("yearMonth") String yearMonth) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始查询指定月份综合统计数据（包含同比增长），月份: {}", yearMonth);
            
            Map<String, Object> stats = orderService.calculateMonthlyStatsWithGrowth(yearMonth);
            
            // 检查是否有错误
            if (stats.containsKey("error")) {
                logger.error("指定月份综合统计数据计算失败: 月份={}, 错误={}", yearMonth, stats.get("error"));
                
                response.put("success", false);
                response.put("message", "统计计算失败");
                response.put("errorCode", "CALCULATION_ERROR");
                response.put("errorDetails", stats.get("error"));
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            logger.info("指定月份综合统计数据查询完成，月份: {}", yearMonth);
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", stats);
            response.put("description", "指定月份订单综合统计数据（包含同比增长）");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询指定月份综合统计数据失败: 月份={}", yearMonth, e);
            
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取当月综合统计数据（包含同比增长，保持向后兼容）
     * 包括人均订单数、人均订单数同比上月增长、人均销售额、人均销售额同比上月增长
     * 
     * @return 当月综合统计数据及同比增长
     */
    @GetMapping("/current-month-stats")
    public ResponseEntity<?> getCurrentMonthStats() {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始查询当月综合统计数据（包含同比增长）");
            
            Map<String, Object> stats = orderService.calculateCurrentMonthStatsWithGrowth();
            
            // 检查是否有错误
            if (stats.containsKey("error")) {
                logger.error("当月综合统计数据计算失败: {}", stats.get("error"));
                
                response.put("success", false);
                response.put("message", "统计计算失败");
                response.put("errorCode", "CALCULATION_ERROR");
                response.put("errorDetails", stats.get("error"));
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            logger.info("当月综合统计数据查询完成");
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", stats);
            response.put("description", "当月订单综合统计数据（包含同比增长）");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询当月综合统计数据失败: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取订单指标概览（包含同比增长）
     * 提供订单相关核心指标的简化视图，包括同比上月增长情况
     * 
     * @return 订单指标概览（包含同比增长）
     */
    @GetMapping("/overview")
    public ResponseEntity<?> getOrderMetricsOverview() {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始获取订单指标概览（包含同比增长）");
            
            // 获取当月综合统计数据（包含同比增长）
            Map<String, Object> stats = orderService.calculateCurrentMonthStatsWithGrowth();
            
            // 检查是否有错误
            if (stats.containsKey("error")) {
                logger.error("获取订单指标概览失败: {}", stats.get("error"));
                
                response.put("success", false);
                response.put("message", "获取概览失败");
                response.put("errorCode", "OVERVIEW_ERROR");
                response.put("errorDetails", stats.get("error"));
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            // 构建概览数据
            Map<String, Object> overview = new HashMap<>();
            overview.put("period", stats.get("period"));
            overview.put("previousPeriod", stats.get("previousPeriod"));
            
            // 人均订单数及增长
            overview.put("avgOrdersPerCustomer", stats.get("avgOrdersPerCustomer"));
            overview.put("avgOrdersGrowthRate", stats.get("avgOrdersGrowthRate"));
            overview.put("avgOrdersGrowthRatePercent", stats.get("avgOrdersGrowthRatePercent"));
            
            // 人均销售额及增长
            overview.put("avgSalesPerCustomer", stats.get("avgSalesPerCustomer"));
            overview.put("avgSalesGrowthRate", stats.get("avgSalesGrowthRate"));
            overview.put("avgSalesGrowthRatePercent", stats.get("avgSalesGrowthRatePercent"));
            
            overview.put("calculationMethod", "基于ACCOUNT_NUMBER常量计算人均指标，同比上月增长率");
            
            logger.info("订单指标概览获取完成");
            
            response.put("success", true);
            response.put("message", "获取概览成功");
            response.put("overview", overview);
            response.put("description", "订单核心指标概览（包含同比增长）");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("获取订单指标概览失败: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "获取概览失败: " + e.getMessage());
            response.put("errorCode", "OVERVIEW_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取指定客户的服务时间
     * 服务时间 = 最晚下单时间 - 最早下单时间
     * 
     * @param clientId 客户ID
     * @return 客户服务时间
     */
    @GetMapping("/service-time/client/{clientId}")
    public ResponseEntity<?> getClientServiceTime(@PathVariable("clientId") String clientId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始查询客户服务时间，客户ID: {}", clientId);
            
            Double serviceTime = orderService.calculateServiceTimeForClient(clientId);
            
            if (serviceTime == null) {
                logger.warn("无法计算客户服务时间，客户ID: {}", clientId);
                
                response.put("success", false);
                response.put("message", "无法计算客户服务时间");
                response.put("clientId", clientId);
                response.put("errorCode", "CALCULATION_ERROR");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            logger.info("客户服务时间查询完成: 客户ID={}, 服务时间={}天", clientId, serviceTime);
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("clientId", clientId);
            response.put("serviceTimeInDays", serviceTime);
            response.put("description", "客户使用服务的时间（最晚下单时间 - 最早下单时间）");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询客户服务时间失败: 客户ID={}", clientId, e);
            
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("clientId", clientId);
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取所有客户的平均成交天数
     * 平均成交天数 = 所有客户中最晚下单时间 - 所有客户中最早下单时间
     * 
     * @return 平均成交天数
     */
    @GetMapping("/average-service-time")
    public ResponseEntity<?> getAverageServiceTime() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始查询所有客户的平均成交天数");
            
            Double averageServiceTime = orderService.calculateAverageServiceTime();
            
            if (averageServiceTime == null) {
                logger.warn("无法计算所有客户的平均成交天数");
                
                response.put("success", false);
                response.put("message", "无法计算平均成交天数");
                response.put("errorCode", "CALCULATION_ERROR");
                
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            logger.info("所有客户平均成交天数查询完成: {}天", averageServiceTime);
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("averageServiceTimeInDays", averageServiceTime);
            response.put("description", "所有客户平均成交天数（最晚下单时间 - 最早下单时间）");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询所有客户平均成交天数失败: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 获取指定日期之后首次下单的客户列表
     * 
     * @param dateStr 指定日期（格式: yyyy-MM-dd HH:mm:ss）
     * @return 在指定日期之后首次下单的客户列表
     */
    @GetMapping("/new-clients-after/{dateStr}")
    public ResponseEntity<?> getNewClientsAfterDate(@PathVariable("dateStr") String dateStr) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始查询指定日期之后首次下单的客户，日期: {}", dateStr);
            
            // 解析日期字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
            
            // 获取在指定日期之后首次下单的客户列表
            List<String> newClientIds = orderService.getNewClientsAfterDate(date);
            
            if (newClientIds == null) {
                logger.warn("查询指定日期之后首次下单的客户失败");
                
                response.put("success", false);
                response.put("message", "查询失败");
                response.put("errorCode", "QUERY_ERROR");
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            logger.info("查询完成，找到 {} 个在指定日期之后首次下单的客户", newClientIds.size());
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("date", dateStr);
            response.put("newClientCount", newClientIds.size());
            response.put("newClientIds", newClientIds);
            response.put("description", "在指定日期之后首次下单的客户列表");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("查询指定日期之后首次下单的客户失败: 日期={}", dateStr, e);
            
            response.put("success", false);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("errorCode", "QUERY_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 计算指定日期之后用户的十日成交转换率
     * 十日成交转换率 = 服务时间在2天到10天之间的用户数 / 指定日期之后用户数
     * 
     * @param dateStr 指定日期（格式: yyyy-MM-dd HH:mm:ss）
     * @return 十日成交转换率
     */
    @GetMapping("/ten-day-conversion-rate/{dateStr}")
    public ResponseEntity<?> getTenDayConversionRate(@PathVariable("dateStr") String dateStr) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始计算指定日期之后用户的十日成交转换率，日期: {}", dateStr);
            
            // 解析日期字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
            
            // 计算十日成交转换率
            Double conversionRate = orderService.calculateTenDayConversionRate(date);
            
            if (conversionRate == null) {
                logger.warn("计算十日成交转换率失败");
                
                response.put("success", false);
                response.put("message", "计算失败");
                response.put("errorCode", "CALCULATION_ERROR");
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            // 转换为百分比格式，保留2位小数
            BigDecimal percentage = new BigDecimal(conversionRate * 100)
                    .setScale(2, RoundingMode.HALF_UP);
            
            logger.info("十日成交转换率计算完成: 转换率={} ({}%)", conversionRate, percentage);
            
            response.put("success", true);
            response.put("message", "计算成功");
            response.put("date", dateStr);
            response.put("conversionRate", conversionRate);
            response.put("conversionRatePercentage", percentage + "%");
            response.put("description", "十日成交转换率（服务时间在2天到10天之间的用户数 / 指定日期之后用户数）");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("计算十日成交转换率失败: 日期={}", dateStr, e);
            
            response.put("success", false);
            response.put("message", "计算失败: " + e.getMessage());
            response.put("errorCode", "CALCULATION_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 计算指定日期之后用户的十五日成交转换率
     * 十五日成交转换率 = 服务时间在2天到15天之间的用户数 / 指定日期之后用户数
     * 
     * @param dateStr 指定日期（格式: yyyy-MM-dd HH:mm:ss）
     * @return 十五日成交转换率
     */
    @GetMapping("/fifteen-day-conversion-rate/{dateStr}")
    public ResponseEntity<?> getFifteenDayConversionRate(@PathVariable("dateStr") String dateStr) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("开始计算指定日期之后用户的十五日成交转换率，日期: {}", dateStr);
            
            // 解析日期字符串
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime date = LocalDateTime.parse(dateStr, formatter);
            
            // 计算十五日成交转换率
            Double conversionRate = orderService.calculateFifteenDayConversionRate(date);
            
            if (conversionRate == null) {
                logger.warn("计算十五日成交转换率失败");
                
                response.put("success", false);
                response.put("message", "计算失败");
                response.put("errorCode", "CALCULATION_ERROR");
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            
            // 转换为百分比格式，保留2位小数
            BigDecimal percentage = new BigDecimal(conversionRate * 100)
                    .setScale(2, RoundingMode.HALF_UP);
            
            logger.info("十五日成交转换率计算完成: 转换率={} ({}%)", conversionRate, percentage);
            
            response.put("success", true);
            response.put("message", "计算成功");
            response.put("date", dateStr);
            response.put("conversionRate", conversionRate);
            response.put("conversionRatePercentage", percentage + "%");
            response.put("description", "十五日成交转换率（服务时间在2天到15天之间的用户数 / 指定日期之后用户数）");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("计算十五日成交转换率失败: 日期={}", dateStr, e);
            
            response.put("success", false);
            response.put("message", "计算失败: " + e.getMessage());
            response.put("errorCode", "CALCULATION_ERROR");
            response.put("errorType", e.getClass().getSimpleName());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 健康检查端点
     * 
     * @return 服务状态信息
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.debug("执行订单指标服务健康检查");
            
            // 简单的服务可用性检查（不依赖数据库）
            response.put("success", true);
            response.put("message", "订单指标服务运行正常");
            response.put("service", "OrderMetricsController");
            response.put("status", "healthy");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("订单指标服务健康检查失败: {}", e.getMessage(), e);
            
            response.put("success", false);
            response.put("message", "服务异常: " + e.getMessage());
            response.put("service", "OrderMetricsController");
            response.put("status", "unhealthy");
            response.put("errorCode", "SERVICE_ERROR");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}