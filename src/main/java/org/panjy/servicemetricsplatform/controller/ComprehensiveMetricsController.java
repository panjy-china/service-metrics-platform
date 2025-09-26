package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.service.*;
import org.panjy.servicemetricsplatform.mapper.WechatMessageAnalyzeAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 综合指标控制器
 * 提供包含性别、年龄、体重、地区分布，本日、本周、本月新增及其环比，
 * 三日留存率、七日留存率、十日留存率、七日流失率、十日转化率、十五日转化率，
 * 人均成交客户数及成交销售额及其增长比例，平均服务天数及成交天数以及其增长比例的综合API接口
 */
@RestController
@RequestMapping("/api/comprehensive")
public class ComprehensiveMetricsController {

    private final ClientService clientService;
    private final StrategicLayerService strategicLayerService;
    private final OrderService orderService;
    private final LLMAnalysisService llmAnalysisService;
    private final WechatMessageAnalyzeAddressMapper wechatMessageAnalyzeAddressMapper;

    // 构造函数注入
    public ComprehensiveMetricsController(
            ClientService clientService,
            StrategicLayerService strategicLayerService,
            OrderService orderService,
            LLMAnalysisService llmAnalysisService,
            WechatMessageAnalyzeAddressMapper wechatMessageAnalyzeAddressMapper) {
        this.clientService = clientService;
        this.strategicLayerService = strategicLayerService;
        this.orderService = orderService;
        this.llmAnalysisService = llmAnalysisService;
        this.wechatMessageAnalyzeAddressMapper = wechatMessageAnalyzeAddressMapper;
    }

    /**
     * 创建统一的成功响应
     *
     * @param message 响应消息
     * @param data 响应数据
     * @return 统一格式的响应
     */
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * 创建统一的失败响应
     *
     * @param message 错误消息
     * @param errorCode 错误代码
     * @return 统一格式的响应
     */
    private Map<String, Object> createErrorResponse(String message, String errorCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("errorCode", errorCode);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * 获取综合指标数据总接口
     * 包含性别、年龄、体重、地区分布，本日、本周、本月新增及其环比，
     * 三日留存率、七日留存率、十日留存率、七日流失率、十日转化率、十五日转化率，
     * 人均成交客户数及成交销售额及其增长比例，平均服务天数及成交天数以及其增长比例
     *
     * @param date 查询日期 (格式: yyyy-MM-dd)
     * @return 综合指标数据
     */
    @GetMapping("/metrics/{date}")
    public ResponseEntity<?> getComprehensiveMetrics(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        try {
            if (date == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("日期参数不能为空", "INVALID_DATE")
                );
            }
            
            System.out.println("开始获取综合指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            // 创建综合数据Map
            Map<String, Object> comprehensiveData = new HashMap<>();
            
            // 1. 获取客户分布数据（性别、年龄、体重、地区）
            getCustomerDistributionData(comprehensiveData);
            
            // 2. 获取新增用户数据（本日、本周、本月新增及其环比）
            getNewUsersData(date, comprehensiveData);
            
            // 3. 获取留存率数据（三日、七日、十日）
            getRetentionRateData(date, comprehensiveData);
            
            // 4. 获取流失率数据（七日）
            getChurnRateData(date, comprehensiveData);
            
            // 5. 获取转化率数据（十日、十五日）
            getConversionRateData(date, comprehensiveData);
            
            // 6. 获取订单相关数据（人均成交客户数及成交销售额及其增长比例）
            getOrderMetricsData(comprehensiveData);
            
            // 7. 获取服务时间数据（平均服务天数及成交天数以及其增长比例）
            getServiceTimeData(date, comprehensiveData);
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", comprehensiveData));
            
        } catch (Exception e) {
            System.err.println("获取综合指标数据失败: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("查询失败: " + e.getMessage(), "QUERY_ERROR")
            );
        }
    }
    
    /**
     * 获取客户分布数据（性别、年龄、体重、地区）
     * 
     * @param data 存储客户分布数据的Map对象
     */
    private void getCustomerDistributionData(Map<String, Object> data) {
        try {
            System.out.println("开始获取客户分布数据");
            
            // 性别分布 - 获取客户的性别分布情况，包含男女性别及对应人数
            List<Map<String, Object>> genderDistribution = clientService.getGenderDistribution();
            data.put("genderDistribution", genderDistribution);
            
            // 年龄分布 - 获取客户的年龄分布情况，包含不同年龄段及对应人数
            List<Map<String, Object>> ageDistribution = clientService.getAgeDistribution();
            data.put("ageDistribution", ageDistribution);
            
            // 体重分布 - 获取客户的体重分布情况，包含不同体重范围及对应人数
            List<Map<String, Object>> weightDistribution = clientService.getWeightDistribution();
            data.put("weightDistribution", weightDistribution);
            
            // 地区分布 - 获取客户的地区分布情况，包含不同省份及对应人数
            List<String> userAddresses = wechatMessageAnalyzeAddressMapper.getUserLatestAddresses();
            Map<String, Integer> addressDistribution = calculateAddressDistribution(userAddresses);
            List<Map<String, Object>> sortedDistribution = addressDistribution.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("province", entry.getKey());
                    item.put("count", entry.getValue());
                    return item;
                })
                .sorted((a, b) -> Integer.compare((Integer) b.get("count"), (Integer) a.get("count")))
                .collect(java.util.stream.Collectors.toList());
            data.put("regionDistribution", sortedDistribution);
            
            System.out.println("客户分布数据获取完成");
        } catch (Exception e) {
            System.err.println("获取客户分布数据失败: " + e.getMessage());
            data.put("genderDistribution", new ArrayList<>());
            data.put("ageDistribution", new ArrayList<>());
            data.put("weightDistribution", new ArrayList<>());
            data.put("regionDistribution", new ArrayList<>());
        }
    }
    
    /**
     * 获取新增用户数据（本日、本周、本月新增及其环比）
     * 
     * @param date 查询日期
     * @param data 存储新增用户数据的Map对象
     */
    private void getNewUsersData(Date date, Map<String, Object> data) {
        try {
            System.out.println("开始获取新增用户数据");
            
            // 日新增用户及环比 - 获取指定日期的日新增用户数及其与前一日的环比增长率
            Map<String, Object> dailyGrowth = strategicLayerService.calculateDailyNewUsersWithGrowth(date);
            data.put("dailyNewUsers", dailyGrowth);
            
            // 周新增用户及环比 - 获取指定日期所在周的新增用户数及其与上周的环比增长率
            Map<String, Object> weeklyGrowth = strategicLayerService.calculateWeeklyNewUsersWithGrowth(date);
            data.put("weeklyNewUsers", weeklyGrowth);
            
            // 月新增用户及环比 - 获取指定日期所在月的新增用户数及其与上月的环比增长率
            Map<String, Object> monthlyGrowth = strategicLayerService.calculateMonthlyNewUsersWithGrowth(date);
            data.put("monthlyNewUsers", monthlyGrowth);
            
            System.out.println("新增用户数据获取完成");
        } catch (Exception e) {
            System.err.println("获取新增用户数据失败: " + e.getMessage());
            data.put("dailyNewUsers", new HashMap<>());
            data.put("weeklyNewUsers", new HashMap<>());
            data.put("monthlyNewUsers", new HashMap<>());
        }
    }
    
    /**
     * 获取留存率数据（三日、七日、十日）
     * 
     * @param date 查询日期
     * @param data 存储留存率数据的Map对象
     */
    private void getRetentionRateData(Date date, Map<String, Object> data) {
        try {
            System.out.println("开始获取留存率数据");
            
            // 创建日历实例用于日期计算
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, -17);
            Date agoDate = cal.getTime();
            
            // 三日留存率及同比增长 - 获取指定日期的三日用户留存率及其与上年同期的增长率
            // 将日期减去3天
//            cal.setTime(date);
//            cal.add(Calendar.DAY_OF_MONTH, -3 - 10);
//            Date threeDayDate = cal.getTime();
            Map<String, Object> threeDayRetention = strategicLayerService.calculateRetentionRateWithGrowth(agoDate, 3);
            data.put("threeDayRetentionRate", threeDayRetention);
            
            // 七日留存率及同比增长 - 获取指定日期的七日用户留存率及其与上年同期的增长率
            // 将日期减去7天
//            cal.setTime(date);
//            cal.add(Calendar.DAY_OF_MONTH, -7 - 10);
//            Date sevenDayDate = cal.getTime();
            Map<String, Object> sevenDayRetention = strategicLayerService.calculateRetentionRateWithGrowth(agoDate, 7);
            data.put("sevenDayRetentionRate", sevenDayRetention);
            
            // 十日留存率及同比增长 - 获取指定日期的十日用户留存率及其与上年同期的增长率
            // 将日期减去10天
//            cal.setTime(date);
//            cal.add(Calendar.DAY_OF_MONTH, -10 - );
//            Date tenDayDate = cal.getTime();
            Map<String, Object> tenDayRetention = strategicLayerService.calculateRetentionRateWithGrowth(agoDate, 10);
            data.put("tenDayRetentionRate", tenDayRetention);
            
            System.out.println("留存率数据获取完成");
        } catch (Exception e) {
            System.err.println("获取留存率数据失败: " + e.getMessage());
            data.put("3", new HashMap<>());
            data.put("7", new HashMap<>());
            data.put("10", new HashMap<>());
        }
    }
    
    /**
     * 获取流失率数据（七日）
     * 
     * @param date 查询日期
     * @param data 存储流失率数据的Map对象
     */
    private void getChurnRateData(Date date, Map<String, Object> data) {
        try {
            System.out.println("开始获取流失率数据");
            
            // 七日流失率及同比增长 - 获取指定日期的七日用户流失率及其与上年同期的增长率
            Map<String, Object> sevenDayChurn = strategicLayerService.calculateChurnRateWithGrowth(date, 7);
            data.put("sevenDayChurnRate", sevenDayChurn);
            
            System.out.println("流失率数据获取完成");
        } catch (Exception e) {
            System.err.println("获取流失率数据失败: " + e.getMessage());
            data.put("sevenDayChurnRate", new HashMap<>());
        }
    }
    
    /**
     * 获取转化率数据（十日、十五日）
     * 
     * @param date 查询日期
     * @param data 存储转化率数据的Map对象
     */
    private void getConversionRateData(Date date, Map<String, Object> data) {
        try {
            System.out.println("开始获取转化率数据");
            
            // 获取当天的LocalDateTime用于转化率计算
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            java.time.LocalDateTime localDateTime = cal.getTime().toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
            
            // 十日转化率 - 获取指定日期之后用户的十日成交转换率
            Double tenDayConversion = orderService.calculateTenDayConversionRate(localDateTime);
            data.put("tenDayConversionRate", tenDayConversion != null ? 
                BigDecimal.valueOf(tenDayConversion).setScale(4, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO);
            
            // 十五日转化率 - 获取指定日期之后用户的十五日成交转换率
            Double fifteenDayConversion = orderService.calculateFifteenDayConversionRate(localDateTime);
            data.put("fifteenDayConversionRate", fifteenDayConversion != null ? 
                BigDecimal.valueOf(fifteenDayConversion).setScale(4, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO);
            
            System.out.println("转化率数据获取完成");
        } catch (Exception e) {
            System.err.println("获取转化率数据失败: " + e.getMessage());
            data.put("tenDayConversionRate", BigDecimal.ZERO);
            data.put("fifteenDayConversionRate", BigDecimal.ZERO);
        }
    }
    
    /**
     * 获取订单相关数据（人均成交客户数及成交销售额及其增长比例）
     * 
     * @param data 存储订单相关数据的Map对象
     */
    private void getOrderMetricsData(Map<String, Object> data) {
        try {
            System.out.println("开始获取订单相关数据");
            
            // 当月人均成交订单数 - 获取当前月份的人均成交订单数
            BigDecimal avgOrders = orderService.calculateCurrentMonthAvgOrdersPerCustomer();
            data.put("currentMonthAvgOrdersPerCustomer", avgOrders);
            
            // 当月人均成交销售额 - 获取当前月份的人均成交销售额
            BigDecimal avgSales = orderService.calculateCurrentMonthAvgSalesPerCustomer();
            data.put("currentMonthAvgSalesPerCustomer", avgSales);
            
            // 获取上月数据用于计算增长比例
            java.time.YearMonth currentMonth = java.time.YearMonth.now();
            java.time.YearMonth previousMonth = currentMonth.minusMonths(1);
            String previousMonthStr = previousMonth.toString();
            
            BigDecimal previousAvgOrders = orderService.calculateMonthlyAvgOrdersPerCustomer(previousMonthStr);
            BigDecimal previousAvgSales = orderService.calculateMonthlySalesPerCustomer(previousMonthStr);
            
            // 计算增长比例 - 计算人均订单数和销售额的环比增长率
            BigDecimal ordersGrowthRate = calculateGrowthRate(avgOrders, previousAvgOrders);
            BigDecimal salesGrowthRate = calculateGrowthRate(avgSales, previousAvgSales);
            
            data.put("avgOrdersGrowthRate", ordersGrowthRate);
            data.put("avgSalesGrowthRate", salesGrowthRate);
            
            System.out.println("订单相关数据获取完成");
        } catch (Exception e) {
            System.err.println("获取订单相关数据失败: " + e.getMessage());
            data.put("currentMonthAvgOrdersPerCustomer", BigDecimal.ZERO);
            data.put("currentMonthAvgSalesPerCustomer", BigDecimal.ZERO);
            data.put("avgOrdersGrowthRate", BigDecimal.ZERO);
            data.put("avgSalesGrowthRate", BigDecimal.ZERO);
        }
    }
    
    /**
     * 获取服务时间数据（平均服务天数及成交天数以及其增长比例）
     * 
     * @param date 查询日期
     * @param data 存储服务时间数据的Map对象
     */
    private void getServiceTimeData(Date date, Map<String, Object> data) {
        try {
            System.out.println("开始获取服务时间数据");
            
            // 平均服务时间及同比增长 - 获取平均服务时间及其与上年同期的增长率
            Map<String, Object> serviceTimeGrowth = strategicLayerService.calculateAverageServiceTimeWithGrowth(date);
            data.put("averageServiceTime", serviceTimeGrowth);
            
            // 平均成交时间 - 获取所有客户的平均成交时间（基于订单数据）
            Double averageDealTime = orderService.calculateAverageServiceTime();
            Map<String, Object> dealTimeData = new HashMap<>();
            if (averageDealTime != null) {
                dealTimeData.put("currentValue", BigDecimal.valueOf(averageDealTime).setScale(2, java.math.RoundingMode.HALF_UP));
            } else {
                dealTimeData.put("currentValue", BigDecimal.ZERO);
            }
            data.put("averageDealTime", dealTimeData);
            
            System.out.println("服务时间数据获取完成");
        } catch (Exception e) {
            System.err.println("获取服务时间数据失败: " + e.getMessage());
            data.put("averageServiceTime", new HashMap<>());
            data.put("averageDealTime", new HashMap<>());
        }
    }
    
    /**
     * 计算增长率
     * 公式: (当前值 - 上期值) / 上期值 × 100%
     * 
     * @param currentValue 当前值
     * @param previousValue 上期值
     * @return 增长率（百分比），保留2位小数
     */
    private BigDecimal calculateGrowthRate(BigDecimal currentValue, BigDecimal previousValue) {
        if (previousValue == null || previousValue.compareTo(BigDecimal.ZERO) == 0) {
            // 如果上期值为0或null，无法计算增长率
            if (currentValue == null || currentValue.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO; // 0 → 0，增长率为0%
            } else {
                return BigDecimal.valueOf(100.0); // 0 → 正数，增长率为100%
            }
        }
        
        // 正常计算: (current - previous) / previous * 100
        return currentValue.subtract(previousValue)
                .divide(previousValue, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * 计算地址分布（取前两个字符作为key，对内蒙古和黑龙江特殊处理）
     * 
     * @param userAddresses 用户地址列表
     * @return 地址分布统计结果
     */
    private Map<String, Integer> calculateAddressDistribution(List<String> userAddresses) {
        Map<String, Integer> distribution = new HashMap<>();
        
        if (userAddresses == null) return distribution;
        
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
     * 
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
}