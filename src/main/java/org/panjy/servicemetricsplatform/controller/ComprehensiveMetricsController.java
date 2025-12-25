package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.mapper.analysis.WechatMessageAnalyzeAddressMapper;
import org.panjy.servicemetricsplatform.entity.order.OrderStatistics;
import org.panjy.servicemetricsplatform.service.analysis.ClientService;
import org.panjy.servicemetricsplatform.service.analysis.LLMAnalysisService;
import org.panjy.servicemetricsplatform.service.newuser.FriendFirstChatService;
import org.panjy.servicemetricsplatform.service.newuser.StrategicLayerService;
import org.panjy.servicemetricsplatform.service.order.OrderService;
import org.panjy.servicemetricsplatform.service.order.OrderStatisticsService;
import org.panjy.servicemetricsplatform.service.order.SalesRankingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
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
    private final OrderStatisticsService orderStatisticsService; // 添加OrderStatisticsService依赖
    private final SalesRankingService salesRankingService; // 添加SalesRankingService依赖
    private final FriendFirstChatService friendFirstChatService; // 添加FriendFirstChatService依赖

    // 构造函数注入
    public ComprehensiveMetricsController(
            ClientService clientService,
            StrategicLayerService strategicLayerService,
            OrderService orderService,
            LLMAnalysisService llmAnalysisService,
            WechatMessageAnalyzeAddressMapper wechatMessageAnalyzeAddressMapper,
            OrderStatisticsService orderStatisticsService,
            SalesRankingService salesRankingService,
            FriendFirstChatService friendFirstChatService) { // 添加FriendFirstChatService参数
        this.clientService = clientService;
        this.strategicLayerService = strategicLayerService;
        this.orderService = orderService;
        this.llmAnalysisService = llmAnalysisService;
        this.wechatMessageAnalyzeAddressMapper = wechatMessageAnalyzeAddressMapper;
        this.orderStatisticsService = orderStatisticsService; // 初始化OrderStatisticsService
        this.salesRankingService = salesRankingService; // 初始化SalesRankingService
        this.friendFirstChatService = friendFirstChatService; // 初始化FriendFirstChatService
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
     * 人均成交客户数及成交销售额及其增长比例，平均服务天数及成交天数以及其增长比例，
     * 日、周、月下单用户数统计，日、周、月销售总额统计
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

            // 将日期改为昨日
            Calendar cal = Calendar.getInstance();
            cal.set(2025, 9, 15);  // 6月 = 5（因为从0开始）
//            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            Date yesterday = cal.getTime();

            cal = Calendar.getInstance();
            cal.set(2025, 9, 15);  // 6月 = 5（因为从0开始）
//            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -10);
            Date last10Days = cal.getTime();

            cal = Calendar.getInstance();
            cal.set(2025, 9, 15);  // 6月 = 5（因为从0开始）
//            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, -20);
            Date last20Days = cal.getTime();

            cal.add(Calendar.MONTH, -1);
            Date lastMonth = cal.getTime();

            System.out.println("开始获取综合指标数据，原日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date) +
                              ", 实际查询日期(昨日): " + new SimpleDateFormat("yyyy-MM-dd").format(yesterday));

            // 创建综合数据Map
            Map<String, Object> comprehensiveData = new HashMap<>();

            // 1. 获取客户分布数据（性别、年龄、体重、地区）
            getCustomerDistributionData(comprehensiveData);

            // 2. 获取新增用户数据（本日、本周、本月新增及其环比）
            getNewUsersData(yesterday, comprehensiveData);

            // 3. 获取留存率数据（三日、七日、十日）
            getRetentionRateData(yesterday, comprehensiveData);

            // 4. 获取流失率数据（七日）
            getChurnRateData(last10Days, comprehensiveData);

            // 5. 获取转化率数据（十日、十五日）
            getConversionRateData(lastMonth, comprehensiveData);

            // 6. 获取订单相关数据（人均成交客户数及成交销售额及其增长比例）
            getOrderMetricsData(yesterday, comprehensiveData);

            // 7. 获取服务时间数据（平均服务天数及成交天数以及其增长比例）
            getServiceTimeData(lastMonth, comprehensiveData);

            // 8. 获取指定日期的日、周、月订单统计数据
            getOrderStatisticsData(yesterday, comprehensiveData);

            // 9. 获取指定日期的日、周、月下单用户数统计
            getOrderingUserStatsData(yesterday, comprehensiveData);

            // 10. 获取指定日期的日、周、月销售总额统计
            getSalesAmountStatsData(yesterday, comprehensiveData);

            // 11. 获取指定日期的周、月平均客单价
            getAverageOrderValueData(yesterday, comprehensiveData);

            // 12. 获取指定日期的销售额自然周、自然月排行榜
            getSalesRankingData(yesterday, comprehensiveData);

            // 13. 获取销售人员数量
            getSalesCountData(comprehensiveData);

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
            Map<String, Object> dailyGrowth = friendFirstChatService.calculateDailyNewUsersWithGrowth(date);
            data.put("dailyNewUsers", dailyGrowth);

            // 周新增用户及环比 - 获取指定日期所在周的新增用户数及其与上周的环比增长率
            Map<String, Object> weeklyGrowth = friendFirstChatService.calculateWeeklyNewUsersWithGrowth(date);
            data.put("weeklyNewUsers", weeklyGrowth);

            // 月新增用户及环比 - 获取指定日期所在月的新增用户数及其与上月的环比增长率
            Map<String, Object> monthlyGrowth = friendFirstChatService.calculateMonthlyNewUsersWithGrowth(date);
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
            cal.add(Calendar.DAY_OF_MONTH, -15);
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
                BigDecimal.valueOf(tenDayConversion * 100).setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO);

            // 十五日转化率 - 获取指定日期之后用户的十五日成交转换率
            Double fifteenDayConversion = orderService.calculateFifteenDayConversionRate(localDateTime);
            data.put("fifteenDayConversionRate", fifteenDayConversion != null ?
                BigDecimal.valueOf(fifteenDayConversion * 100).setScale(2, java.math.RoundingMode.HALF_UP) : BigDecimal.ZERO);

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
     * @param date 查询日期
     * @param data 存储订单相关数据的Map对象
     */
    private void getOrderMetricsData(Date date, Map<String, Object> data) {
        try {
            System.out.println("开始获取订单相关数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDate
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            // 获取指定日期所在月份
            YearMonth targetMonth = YearMonth.from(localDate);
            String targetMonthStr = targetMonth.toString();
            
            // 获取指定月份的人均成交订单数
            BigDecimal avgOrders = orderService.calculateMonthlyAvgOrdersPerCustomer(targetMonthStr);
            data.put("currentMonthAvgOrdersPerCustomer", avgOrders);

            // 获取指定月份的人均成交销售额
            BigDecimal avgSales = orderService.calculateMonthlySalesPerCustomer(targetMonthStr);
            data.put("currentMonthAvgSalesPerCustomer", avgSales);

            // 获取上月数据用于计算增长比例
            YearMonth previousMonth = targetMonth.minusMonths(1);
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
     * 获取指定日期的日、周、月订单统计数据（包含增长率）
     *
     * @param date 查询日期
     * @param data 存储订单统计数据的Map对象
     */
    private void getOrderStatisticsData(Date date, Map<String, Object> data) {
        try {
            System.out.println("开始获取订单统计数据");

            // 将Date转换为字符串格式 'YYYY-MM-DD'
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateString = sdf.format(date);

            // 调用OrderStatisticsService获取订单统计数据
            OrderStatistics currentOrderStatistics = orderStatisticsService.getOrderStatisticsByDate(dateString);
            
            // 调用OrderStatisticsService获取前序订单统计数据
            OrderStatistics previousOrderStatistics = orderStatisticsService.getPreviousOrderStatisticsByDate(dateString);

            // 计算日、周、月订单数的增长率
            Map<String, Object> orderStatsWithGrowth = new HashMap<>();
            
            // 日订单数增长率
            Map<String, Object> dailyStats = new HashMap<>();
            dailyStats.put("count", currentOrderStatistics.getDailyCount() != null ? currentOrderStatistics.getDailyCount() : 0);
            // dailyStats.put("previousCount", previousOrderStatistics.getDailyCount() != null ? previousOrderStatistics.getDailyCount() : 0);
            BigDecimal dailyGrowthRate = calculateGrowthRate(
                currentOrderStatistics.getDailyCount() != null ? new BigDecimal(currentOrderStatistics.getDailyCount()) : BigDecimal.ZERO,
                previousOrderStatistics.getDailyCount() != null ? new BigDecimal(previousOrderStatistics.getDailyCount()) : BigDecimal.ZERO
            );
            dailyStats.put("growthRate", dailyGrowthRate);
            // dailyStats.put("growthRatePercent", dailyGrowthRate + "%");
            
            // 周订单数增长率
            Map<String, Object> weeklyStats = new HashMap<>();
            weeklyStats.put("count", currentOrderStatistics.getWeeklyCount() != null ? currentOrderStatistics.getWeeklyCount() : 0);
            // weeklyStats.put("previousCount", previousOrderStatistics.getWeeklyCount() != null ? previousOrderStatistics.getWeeklyCount() : 0);
            BigDecimal weeklyGrowthRate = calculateGrowthRate(
                currentOrderStatistics.getWeeklyCount() != null ? new BigDecimal(currentOrderStatistics.getWeeklyCount()) : BigDecimal.ZERO,
                previousOrderStatistics.getWeeklyCount() != null ? new BigDecimal(previousOrderStatistics.getWeeklyCount()) : BigDecimal.ZERO
            );
            weeklyStats.put("growthRate", weeklyGrowthRate);
            // weeklyStats.put("growthRatePercent", weeklyGrowthRate + "%");
            
            // 月订单数增长率
            Map<String, Object> monthlyStats = new HashMap<>();
            monthlyStats.put("count", currentOrderStatistics.getMonthlyCount() != null ? currentOrderStatistics.getMonthlyCount() : 0);
            // monthlyStats.put("previousCount", previousOrderStatistics.getMonthlyCount() != null ? previousOrderStatistics.getMonthlyCount() : 0);
            BigDecimal monthlyGrowthRate = calculateGrowthRate(
                currentOrderStatistics.getMonthlyCount() != null ? new BigDecimal(currentOrderStatistics.getMonthlyCount()) : BigDecimal.ZERO,
                previousOrderStatistics.getMonthlyCount() != null ? new BigDecimal(previousOrderStatistics.getMonthlyCount()) : BigDecimal.ZERO
            );
            monthlyStats.put("growthRate", monthlyGrowthRate);
            // monthlyStats.put("growthRatePercent", monthlyGrowthRate + "%");
            
            // 组装结果
            orderStatsWithGrowth.put("daily", dailyStats);
            orderStatsWithGrowth.put("weekly", weeklyStats);
            orderStatsWithGrowth.put("monthly", monthlyStats);

            // 将订单统计数据放入返回结果中
            data.put("orderStatistics", orderStatsWithGrowth);

            System.out.println("订单统计数据获取完成");
        } catch (Exception e) {
            System.err.println("获取订单统计数据失败: " + e.getMessage());
            // 发生异常时放入默认值
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "获取订单统计数据失败: " + e.getMessage());
            data.put("orderStatistics", errorStats);
        }
    }

    /**
     * 获取指定日期的日、周、月下单用户数统计
     *
     * @param date 查询日期
     * @param data 存储下单用户数统计的Map对象
     */
    private void getOrderingUserStatsData(Date date, Map<String, Object> data) {
        try {
            System.out.println("开始获取下单用户数统计");

            // 将Date转换为LocalDate
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // 调用OrderService获取下单用户数统计（包含增长率）
            Map<String, Object> orderingUserStats = orderService.getOrderingUserStatsWithGrowthByDate(localDate);

            // 将下单用户数统计放入返回结果中
            data.put("orderingUserStats", orderingUserStats);

            System.out.println("下单用户数统计获取完成");
        } catch (Exception e) {
            System.err.println("获取下单用户数统计失败: " + e.getMessage());
            // 发生异常时放入默认值
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "获取下单用户数统计失败: " + e.getMessage());
            data.put("orderingUserStats", errorStats);
        }
    }

    /**
     * 获取指定日期的日、周、月销售总额统计
     *
     * @param date 查询日期
     * @param data 存储销售总额统计的Map对象
     */
    private void getSalesAmountStatsData(Date date, Map<String, Object> data) {
        try {
            System.out.println("开始获取销售总额统计");

            // 将Date转换为LocalDate
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // 调用OrderService获取销售总额统计（包含增长率）
            Map<String, Object> salesAmountStats = orderService.getSalesAmountStatsWithGrowthByDate(localDate);

            // 将销售总额统计放入返回结果中
            data.put("salesAmountStats", salesAmountStats);

            System.out.println("销售总额统计获取完成");
        } catch (Exception e) {
            System.err.println("获取销售总额统计失败: " + e.getMessage());
            // 发生异常时放入默认值
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "获取销售总额统计失败: " + e.getMessage());
            data.put("salesAmountStats", errorStats);
        }
    }

    /**
     * 获取指定日期的周、月平均客单价
     *
     * @param date 查询日期
     * @param data 存储平均客单价的Map对象
     */
    private void getAverageOrderValueData(Date date, Map<String, Object> data) {
        try {
            System.out.println("开始获取周、月平均客单价");

            // 将Date转换为LocalDate
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // 调用OrderService获取日平均客单价
            BigDecimal dailyAverageOrderValue = orderService.calculateDailyAverageOrderValue(localDate);

            // 调用OrderService获取周平均客单价
            BigDecimal weeklyAverageOrderValue = orderService.calculateWeeklyAverageOrderValue(localDate);

            // 调用OrderService获取月平均客单价
            BigDecimal monthlyAverageOrderValue = orderService.calculateMonthlyAverageOrderValue(localDate);

            // 调用OrderService获取前一日平均客单价
            LocalDate previousDay = localDate.minusDays(1);
            BigDecimal previousDailyAverageOrderValue = orderService.calculateDailyAverageOrderValue(previousDay);

            // 调用OrderService获取前一周平均客单价
            BigDecimal previousWeeklyAverageOrderValue = orderService.calculatePreviousWeeklyAverageOrderValue(localDate);

            // 调用OrderService获取前一月平均客单价
            BigDecimal previousMonthlyAverageOrderValue = orderService.calculatePreviousMonthlyAverageOrderValue(localDate);

            // 计算增长率
            BigDecimal dailyGrowthRate = calculateGrowthRate(dailyAverageOrderValue, previousDailyAverageOrderValue);
            BigDecimal weeklyGrowthRate = calculateGrowthRate(weeklyAverageOrderValue, previousWeeklyAverageOrderValue);
            BigDecimal monthlyGrowthRate = calculateGrowthRate(monthlyAverageOrderValue, previousMonthlyAverageOrderValue);

            // 创建平均客单价Map
            Map<String, Object> averageOrderValueStats = new HashMap<>();
            averageOrderValueStats.put("dailyAverageOrderValue", dailyAverageOrderValue);
            averageOrderValueStats.put("weeklyAverageOrderValue", weeklyAverageOrderValue);
            averageOrderValueStats.put("monthlyAverageOrderValue", monthlyAverageOrderValue);
//            averageOrderValueStats.put("previousDailyAverageOrderValue", previousDailyAverageOrderValue);
//            averageOrderValueStats.put("previousWeeklyAverageOrderValue", previousWeeklyAverageOrderValue);
//            averageOrderValueStats.put("previousMonthlyAverageOrderValue", previousMonthlyAverageOrderValue);
            averageOrderValueStats.put("dailyGrowthRate", dailyGrowthRate);
            averageOrderValueStats.put("weeklyGrowthRate", weeklyGrowthRate);
            averageOrderValueStats.put("monthlyGrowthRate", monthlyGrowthRate);
//            averageOrderValueStats.put("dailyGrowthRatePercent", dailyGrowthRate + "%");
//            averageOrderValueStats.put("weeklyGrowthRatePercent", weeklyGrowthRate + "%");
//            averageOrderValueStats.put("monthlyGrowthRatePercent", monthlyGrowthRate + "%");

            // 将平均客单价放入返回结果中
            data.put("averageOrderValueStats", averageOrderValueStats);

            System.out.println("日、周、月平均客单价获取完成");
        } catch (Exception e) {
            System.err.println("获取日、周、月平均客单价失败: " + e.getMessage());
            // 发生异常时放入默认值
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("dailyAverageOrderValue", BigDecimal.ZERO);
            errorStats.put("weeklyAverageOrderValue", BigDecimal.ZERO);
            errorStats.put("monthlyAverageOrderValue", BigDecimal.ZERO);
            // errorStats.put("previousDailyAverageOrderValue", BigDecimal.ZERO);
            // errorStats.put("previousWeeklyAverageOrderValue", BigDecimal.ZERO);
            // errorStats.put("previousMonthlyAverageOrderValue", BigDecimal.ZERO);
            errorStats.put("dailyGrowthRate", BigDecimal.ZERO);
            errorStats.put("weeklyGrowthRate", BigDecimal.ZERO);
            errorStats.put("monthlyGrowthRate", BigDecimal.ZERO);
            // errorStats.put("dailyGrowthRatePercent", "0%");
            // errorStats.put("weeklyGrowthRatePercent", "0%");
            // errorStats.put("monthlyGrowthRatePercent", "0%");
            data.put("averageOrderValueStats", errorStats);
        }
    }

    /**
     * 获取指定日期的销售额自然周、自然月排行榜
     *
     * @param date 查询日期
     * @param data 存储销售排行榜数据的Map对象
     */
    private void getSalesRankingData(Date date, Map<String, Object> data) {
        try {
            System.out.println("开始获取销售额自然周、自然月排行榜");

            // 将Date转换为LocalDate
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            // 调用SalesRankingService获取销售排行榜数据
            Map<String, Object> salesRankingResult = salesRankingService.getSalesRankingsByDate(localDate);

            // 将销售排行榜数据放入返回结果中
            data.put("salesRankingData", salesRankingResult);

            System.out.println("销售额自然周、自然月排行榜获取完成");
        } catch (Exception e) {
            System.err.println("获取销售额自然周、自然月排行榜失败: " + e.getMessage());
            // 发生异常时放入默认值
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "获取销售排行榜数据失败: " + e.getMessage());
            data.put("salesRankingData", errorResult);
        }
    }

    /**
     * 获取销售人员数量
     *
     * @param data 存储销售人员数量的Map对象
     */
    private void getSalesCountData(Map<String, Object> data) {
        try {
            System.out.println("开始获取销售人员数量");

            // 调用OrderService获取实际销售人员数量
            Long salesCount = orderService.getActualSalesCount();

            // 将销售人员数量放入返回结果中
            data.put("salesCount", salesCount);

            System.out.println("销售人员数量获取完成: " + salesCount);
        } catch (Exception e) {
            System.err.println("获取销售人员数量失败: " + e.getMessage());
            // 发生异常时放入默认值
            data.put("salesCount", 27L);
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