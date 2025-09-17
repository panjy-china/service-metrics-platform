package org.panjy.servicemetricsplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.panjy.servicemetricsplatform.mapper.clickhouse.OrderMapper;
import org.panjy.servicemetricsplatform.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 订单服务类
 * 提供订单相关的业务逻辑
 */
@Service
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 计算指定月份人均成交订单数
     * 人均订单数 = 指定月份总订单数 / ACCOUNT_NUMBER常量
     * 
     * @param yearMonth 指定月份（格式: YYYY-MM）
     * @return 人均订单数，保留2位小数
     */
    public BigDecimal calculateMonthlyAvgOrdersPerCustomer(String yearMonth) {
        try {
            // 获取指定月份的开始和结束时间
            Map<String, String> monthRange = getMonthRange(yearMonth);
            String startTime = monthRange.get("startTime");
            String endTime = monthRange.get("endTime");
            
            logger.info("开始计算指定月份人均订单数，月份: {}，时间范围: {} - {}", yearMonth, startTime, endTime);
            
            // 查询指定月份总订单数
            Long totalOrders = orderMapper.getTotalOrderCount(startTime, endTime);
            
            logger.info("指定月份统计数据 - 月份: {}, 总订单数: {}, 账户数量常量: {}", yearMonth, totalOrders, Constants.ACCOUNT_NUMBER);
            
            if (totalOrders == null || totalOrders == 0) {
                logger.warn("指定月份没有成交订单，月份: {}，返回0", yearMonth);
                return BigDecimal.ZERO;
            }
            
            // 计算人均订单数：总订单数 / ACCOUNT_NUMBER常量，保留2位小数
            BigDecimal avgOrders = new BigDecimal(totalOrders)
                .divide(new BigDecimal(Constants.ACCOUNT_NUMBER), 2, RoundingMode.HALF_UP);
            
            logger.info("指定月份人均订单数计算完成: 月份={}, 人均订单数={}", yearMonth, avgOrders);
            return avgOrders;
            
        } catch (Exception e) {
            logger.error("计算指定月份人均订单数失败，月份: {}", yearMonth, e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 计算当月人均成交订单数（保持向后兼容）
     * 人均订单数 = 当月总订单数 / ACCOUNT_NUMBER常量
     * 
     * @return 人均订单数，保留2位小数
     */
    public BigDecimal calculateCurrentMonthAvgOrdersPerCustomer() {
        YearMonth currentMonth = YearMonth.now();
        String yearMonth = currentMonth.toString();
        logger.info("调用当月人均订单数计算，自动使用当月: {}", yearMonth);
        return calculateMonthlyAvgOrdersPerCustomer(yearMonth);
    }
    
    /**
     * 计算指定月份人均成交销售额
     * 人均销售额 = 指定月份总成交额 / ACCOUNT_NUMBER常量
     * 
     * @param yearMonth 指定月份（格式: YYYY-MM）
     * @return 人均销售额，保留2位小数
     */
    public BigDecimal calculateMonthlySalesPerCustomer(String yearMonth) {
        try {
            // 获取指定月份的开始和结束时间
            Map<String, String> monthRange = getMonthRange(yearMonth);
            String startTime = monthRange.get("startTime");
            String endTime = monthRange.get("endTime");
            
            logger.info("开始计算指定月份人均销售额，月份: {}，时间范围: {} - {}", yearMonth, startTime, endTime);
            
            // 查询指定月份总成交额
            BigDecimal totalSales = orderMapper.getTotalSalesAmount(startTime, endTime);
            
            logger.info("指定月份统计数据 - 月份: {}, 总销售额: {}, 账户数量常量: {}", yearMonth, totalSales, Constants.ACCOUNT_NUMBER);
            
            if (totalSales == null || totalSales.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("指定月份没有成交额，月份: {}，返回0", yearMonth);
                return BigDecimal.ZERO;
            }
            
            // 计算人均销售额：总销售额 / ACCOUNT_NUMBER常量，保留2位小数
            BigDecimal avgSales = totalSales.divide(new BigDecimal(Constants.ACCOUNT_NUMBER), 2, RoundingMode.HALF_UP);
            
            logger.info("指定月份人均销售额计算完成: 月份={}, 人均销售额={}", yearMonth, avgSales);
            return avgSales;
            
        } catch (Exception e) {
            logger.error("计算指定月份人均销售额失败，月份: {}", yearMonth, e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 计算当月人均成交销售额（保持向后兼容）
     * 人均销售额 = 当月总成交额 / ACCOUNT_NUMBER常量
     * 
     * @return 人均销售额，保留2位小数
     */
    public BigDecimal calculateCurrentMonthAvgSalesPerCustomer() {
        YearMonth currentMonth = YearMonth.now();
        String yearMonth = currentMonth.toString();
        logger.info("调用当月人均销售额计算，自动使用当月: {}", yearMonth);
        return calculateMonthlySalesPerCustomer(yearMonth);
    }
    
    /**
     * 计算指定月份综合统计数据（包含同比增长）
     * 包括人均订单数、人均销售额及其同比上月增长率
     * 
     * @param yearMonth 指定月份（格式: YYYY-MM）
     * @return 包含综合统计数据和同比增长的Map
     */
    public Map<String, Object> calculateMonthlyStatsWithGrowth(String yearMonth) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 获取指定月份时间范围
            Map<String, String> monthRange = getMonthRange(yearMonth);
            String startTime = monthRange.get("startTime");
            String endTime = monthRange.get("endTime");
            
            // 获取上月时间范围
            String previousYearMonth = getPreviousMonth(yearMonth);
            Map<String, String> previousMonthRange = getMonthRange(previousYearMonth);
            
            logger.info("开始计算指定月份综合统计（包含同比增长），当前月份: {}，上月: {}", yearMonth, previousYearMonth);
            
            // 获取当月基础数据
            BigDecimal totalSales = orderMapper.getTotalSalesAmount(startTime, endTime);
            Long totalOrders = orderMapper.getTotalOrderCount(startTime, endTime);
            Long totalCustomers = orderMapper.getTotalCustomerCount(startTime, endTime);
            
            // 计算当月人均指标
            BigDecimal avgOrdersPerCustomer = calculateMonthlyAvgOrdersPerCustomer(yearMonth);
            BigDecimal avgSalesPerCustomer = calculateMonthlySalesPerCustomer(yearMonth);
            
            // 计算上月人均指标
            BigDecimal previousAvgOrdersPerCustomer = calculateMonthlyAvgOrdersPerCustomer(previousYearMonth);
            BigDecimal previousAvgSalesPerCustomer = calculateMonthlySalesPerCustomer(previousYearMonth);
            
            // 计算同比增长率
            BigDecimal ordersGrowthRate = calculateGrowthRate(avgOrdersPerCustomer, previousAvgOrdersPerCustomer);
            BigDecimal salesGrowthRate = calculateGrowthRate(avgSalesPerCustomer, previousAvgSalesPerCustomer);
            
            logger.info("指标计算完成 - 当月人均订单数: {}, 上月: {}, 增长率: {}%", 
                    avgOrdersPerCustomer, previousAvgOrdersPerCustomer, ordersGrowthRate);
            logger.info("指标计算完成 - 当月人均销售额: {}, 上月: {}, 增长率: {}%", 
                    avgSalesPerCustomer, previousAvgSalesPerCustomer, salesGrowthRate);
            
            // 组装结果
            stats.put("period", yearMonth);
            stats.put("previousPeriod", previousYearMonth);
            stats.put("startTime", startTime);
            stats.put("endTime", endTime);
            stats.put("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);
            stats.put("totalOrders", totalOrders != null ? totalOrders : 0L);
            stats.put("totalCustomers", totalCustomers != null ? totalCustomers : 0L);
            
            // 人均指标及增长率
            stats.put("avgOrdersPerCustomer", avgOrdersPerCustomer);
            stats.put("previousAvgOrdersPerCustomer", previousAvgOrdersPerCustomer);
            stats.put("avgOrdersGrowthRate", ordersGrowthRate);
            stats.put("avgOrdersGrowthRatePercent", ordersGrowthRate + "%");
            
            stats.put("avgSalesPerCustomer", avgSalesPerCustomer);
            stats.put("previousAvgSalesPerCustomer", previousAvgSalesPerCustomer);
            stats.put("avgSalesGrowthRate", salesGrowthRate);
            stats.put("avgSalesGrowthRatePercent", salesGrowthRate + "%");
            
            logger.info("指定月份综合统计（包含同比增长）完成: {}", stats);
            
        } catch (Exception e) {
            logger.error("计算指定月份综合统计（包含同比增长）失败，月份: {}", yearMonth, e);
            stats.put("error", "计算失败: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * 计算当月综合统计数据（包含同比增长，保持向后兼容）
     * 包括人均订单数、人均销售额及其同比上月增长率
     * 
     * @return 包含综合统计数据和同比增长的Map
     */
    public Map<String, Object> calculateCurrentMonthStatsWithGrowth() {
        YearMonth currentMonth = YearMonth.now();
        String yearMonth = currentMonth.toString();
        logger.info("调用当月综合统计计算（包含同比增长），自动使用当月: {}", yearMonth);
        return calculateMonthlyStatsWithGrowth(yearMonth);
    }
    
    /**
     * 计算增长率
     * 公式：（当前值 - 上期值）/ 上期值 * 100%
     * 
     * @param currentValue 当前值
     * @param previousValue 上期值
     * @return 增长率（百分比）
     */
    private BigDecimal calculateGrowthRate(BigDecimal currentValue, BigDecimal previousValue) {
        // 防止除零错误
        if (previousValue == null || previousValue.compareTo(BigDecimal.ZERO) == 0) {
            if (currentValue == null || currentValue.compareTo(BigDecimal.ZERO) == 0) {
                logger.debug("当前值和上期值都为0，增长率设为0");
                return BigDecimal.ZERO;
            } else {
                logger.warn("上期值为0，无法计算增长率，返回100%表示全新增长");
                return new BigDecimal("100.00");
            }
        }
        
        if (currentValue == null) {
            currentValue = BigDecimal.ZERO;
        }
        
        try {
            // 计算增长率：(current - previous) / previous * 100
            BigDecimal difference = currentValue.subtract(previousValue);
            BigDecimal growthRate = difference.divide(previousValue, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"))
                    .setScale(2, RoundingMode.HALF_UP);
            
            logger.debug("增长率计算: 当前值={}, 上期倽={}, 增长率={}%", currentValue, previousValue, growthRate);
            return growthRate;
            
        } catch (Exception e) {
            logger.error("计算增长率失败: 当前值={}, 上期倽={}", currentValue, previousValue, e);
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * 获取上一个月的月份字符串
     * 
     * @param yearMonth 当前月份（格式: YYYY-MM）
     * @return 上一个月的月份字符串
     */
    private String getPreviousMonth(String yearMonth) {
        YearMonth targetMonth = YearMonth.parse(yearMonth);
        YearMonth previousMonth = targetMonth.minusMonths(1);
        return previousMonth.toString();
    }
    
    /**
     * 计算指定月份综合统计数据（不包含同比增长，保持向后兼容）
     * 包括人均订单数和人均销售额（都基于ACCOUNT_NUMBER常量计算）
     * 
     * @param yearMonth 指定月份（格式: YYYY-MM）
     * @return 包含综合统计数据的Map
     */
    public Map<String, Object> calculateMonthlyStats(String yearMonth) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 获取指定月份时间范围
            Map<String, String> monthRange = getMonthRange(yearMonth);
            String startTime = monthRange.get("startTime");
            String endTime = monthRange.get("endTime");
            
            logger.info("开始计算指定月份综合统计，月份: {}，时间范围: {} - {}", yearMonth, startTime, endTime);
            
            // 获取基础数据
            BigDecimal totalSales = orderMapper.getTotalSalesAmount(startTime, endTime);
            Long totalOrders = orderMapper.getTotalOrderCount(startTime, endTime);
            Long totalCustomers = orderMapper.getTotalCustomerCount(startTime, endTime);
            
            // 计算人均指标
            BigDecimal avgOrdersPerCustomer = calculateMonthlyAvgOrdersPerCustomer(yearMonth);
            BigDecimal avgSalesPerCustomer = calculateMonthlySalesPerCustomer(yearMonth);
            
            // 组装结果
            stats.put("period", yearMonth);
            stats.put("startTime", startTime);
            stats.put("endTime", endTime);
            stats.put("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);
            stats.put("totalOrders", totalOrders != null ? totalOrders : 0L);
            stats.put("totalCustomers", totalCustomers != null ? totalCustomers : 0L);
            stats.put("avgOrdersPerCustomer", avgOrdersPerCustomer);
            stats.put("avgSalesPerCustomer", avgSalesPerCustomer);
            
            logger.info("指定月份综合统计完成: {}", stats);
            
        } catch (Exception e) {
            logger.error("计算指定月份综合统计失败，月份: {}", yearMonth, e);
            stats.put("error", "计算失败: " + e.getMessage());
        }
        
        return stats;
    }
    
    /**
     * 计算当月综合统计数据（保持向后兼容）
     * 包括人均订单数和人均销售额（都基于ACCOUNT_NUMBER常量计算）
     * 
     * @return 包含综合统计数据的Map
     */
    public Map<String, Object> calculateCurrentMonthStats() {
        YearMonth currentMonth = YearMonth.now();
        String yearMonth = currentMonth.toString();
        logger.info("调用当月综合统计计算，自动使用当月: {}", yearMonth);
        return calculateMonthlyStats(yearMonth);
    }
    
    /**
     * 获取指定月份的时间范围
     * 
     * @param yearMonth 指定月份（格式: YYYY-MM）
     * @return 包含开始和结束时间的Map
     */
    private Map<String, String> getMonthRange(String yearMonth) {
        YearMonth targetMonth = YearMonth.parse(yearMonth);
        LocalDate startOfMonth = targetMonth.atDay(1);
        LocalDate endOfMonth = targetMonth.atEndOfMonth();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.TimeFormat.STANDARD_DATETIME);
        
        Map<String, String> range = new HashMap<>();
        range.put("startTime", startOfMonth.atStartOfDay().format(formatter));
        range.put("endTime", endOfMonth.atTime(23, 59, 59).format(formatter));
        
        logger.debug("生成月份时间范围: {} -> {} 到 {}", yearMonth, range.get("startTime"), range.get("endTime"));
        return range;
    }
    
    /**
     * 获取当月的时间范围（保持向后兼容）
     * 
     * @return 包含开始和结束时间的Map
     */
    private Map<String, String> getCurrentMonthRange() {
        YearMonth currentMonth = YearMonth.now();
        return getMonthRange(currentMonth.toString());
    }
}