package org.panjy.servicemetricsplatform.service.order;

import org.panjy.servicemetricsplatform.entity.order.OrderingUserStats;
import org.panjy.servicemetricsplatform.entity.serverTime.ServerTime;
import org.panjy.servicemetricsplatform.mapper.order.OrderMapper;
import org.panjy.servicemetricsplatform.mapper.order.SalesCountMapper;
import org.panjy.servicemetricsplatform.mapper.message.WechatMessageMapper;
import org.panjy.servicemetricsplatform.constant.Constants;
import org.panjy.servicemetricsplatform.mapper.serverTime.ServerTimeMapper;
import org.panjy.servicemetricsplatform.service.serverTime.ServerTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 订单服务类
 * 提供订单统计分析相关的业务逻辑实现
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private SalesCountMapper salesCountMapper;

    @Autowired
    private WechatMessageMapper wechatMessageMapper;

    @Autowired
    private ServerTimeService serverTimeService;

    @Autowired
    private ServerTimeMapper serverTimeMapper;

    /**
     * 获取实际销售人员数量
     * 通过查询数据库中符合条件的销售人员个数来替代硬编码的常量
     * 
     * @return 销售人员数量
     */
    public Long getActualSalesCount() {
        try {
            // 使用默认的'E%'前缀查询销售人员数量
            Long salesCount = salesCountMapper.getSalesCount("E%");
            logger.info("获取实际销售人员数量: {}", salesCount);
            return salesCount != null ? salesCount : 0L;
        } catch (Exception e) {
            logger.error("获取实际销售人员数量失败，使用默认值27", e);
            // 出错时返回默认值
            return 27L;
        }
    }

    /**
     * 根据处理后的地址串进行模糊查询，返回匹配到的第一条记录的客户ID
     *
     * @param processedAddress 处理后的地址串
     * @return 匹配到的第一条记录的客户ID，如果未找到则返回null
     */
    public String getCltIdByProcessedAddress(String processedAddress) {
        try {
            logger.info("开始根据处理后的地址串进行模糊查询，地址串: {}", processedAddress);

            String cltId = orderMapper.getCltIdByProcessedAddress(processedAddress);

            logger.info("地址串模糊查询完成，地址串: {}, 查询结果: {}", processedAddress, cltId);

            return cltId;
        } catch (Exception e) {
            logger.error("根据处理后的地址串进行模糊查询失败，地址串: {}", processedAddress, e);
            return null;
        }
    }

    /**
     * 计算指定日期所在天的平均客单价
     * 平均客单价 = 日销售总额 / 日下单用户数
     *
     * @param date 指定日期
     * @return 平均客单价，保留2位小数
     */
    public BigDecimal calculateDailyAverageOrderValue(LocalDate date) {
        try {
            logger.info("开始计算指定日期所在天的平均客单价，日期: {}", date);

            // 获取日销售总额
            BigDecimal dailySales = orderMapper.getDailySalesAmount(date);
            logger.debug("日销售总额: {}", dailySales);

            // 获取日下单用户数
            Long dailyUsers = orderMapper.getDailyOrderingUsers(date);
            logger.debug("日下单用户数: {}", dailyUsers);

            // 检查数据有效性
            if (dailySales == null || dailySales.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("日销售总额为null或小于等于0，返回0");
                return BigDecimal.ZERO;
            }

            if (dailyUsers == null || dailyUsers <= 0) {
                logger.warn("日下单用户数为null或小于等于0，无法计算平均客单价，返回0");
                return BigDecimal.ZERO;
            }

            // 计算平均客单价：销售总额 / 下单用户数，保留2位小数
            BigDecimal averageOrderValue = dailySales.divide(new BigDecimal(dailyUsers), 2, RoundingMode.HALF_UP);

            logger.info("指定日期所在天的平均客单价计算完成: 日期={}, 平均客单价={}", date, averageOrderValue);
            return averageOrderValue;

        } catch (Exception e) {
            logger.error("计算指定日期所在天的平均客单价失败，日期: {}", date, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算指定日期所在自然周的平均客单价
     * 平均客单价 = 周销售总额 / 周下单用户数
     *
     * @param date 指定日期
     * @return 平均客单价，保留2位小数
     */
    public BigDecimal calculateWeeklyAverageOrderValue(LocalDate date) {
        try {
            logger.info("开始计算指定日期所在自然周的平均客单价，日期: {}", date);

            // 获取周销售总额
            BigDecimal weeklySales = orderMapper.getWeeklySalesAmount(date);
            logger.debug("周销售总额: {}", weeklySales);

            // 获取周下单用户数
            Long weeklyUsers = orderMapper.getWeeklyOrderingUsers(date);
            logger.debug("周下单用户数: {}", weeklyUsers);

            // 检查数据有效性
            if (weeklySales == null || weeklySales.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("周销售总额为null或小于等于0，返回0");
                return BigDecimal.ZERO;
            }

            if (weeklyUsers == null || weeklyUsers <= 0) {
                logger.warn("周下单用户数为null或小于等于0，无法计算平均客单价，返回0");
                return BigDecimal.ZERO;
            }

            // 计算平均客单价：销售总额 / 下单用户数，保留2位小数
            BigDecimal averageOrderValue = weeklySales.divide(new BigDecimal(weeklyUsers), 2, RoundingMode.HALF_UP);

            logger.info("指定日期所在自然周的平均客单价计算完成: 日期={}, 平均客单价={}", date, averageOrderValue);
            return averageOrderValue;

        } catch (Exception e) {
            logger.error("计算指定日期所在自然周的平均客单价失败，日期: {}", date, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算指定日期前一周的平均客单価
     * 平均客单価 = 前一周销售总额 / 前一周下单用户数
     *
     * @param date 指定日期
     * @return 平均客单価，保留2位小数
     */
    public BigDecimal calculatePreviousWeeklyAverageOrderValue(LocalDate date) {
        try {
            logger.info("開始計算指定日期前一周的平均客单価，日期: {}", date);

            // 获取前一周销售总额
            BigDecimal previousWeeklySales = orderMapper.getPreviousWeeklySalesAmount(date);
            logger.debug("前一周销售总额: {}", previousWeeklySales);

            // 获取前一周下单用户数
            Long previousWeeklyUsers = orderMapper.getPreviousWeekOrderingUsers(date);
            logger.debug("前一周下单用户数: {}", previousWeeklyUsers);

            // 检查数据有效性
            if (previousWeeklySales == null || previousWeeklySales.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("前一周销售总额为null或小于等于0，返回0");
                return BigDecimal.ZERO;
            }

            if (previousWeeklyUsers == null || previousWeeklyUsers <= 0) {
                logger.warn("前一周下单用户数为null或小于等于0，无法计算平均客单価，返回0");
                return BigDecimal.ZERO;
            }

            // 计算平均客单価：销售总额 / 下单用户数，保留2位小数
            BigDecimal averageOrderValue = previousWeeklySales.divide(new BigDecimal(previousWeeklyUsers), 2, RoundingMode.HALF_UP);

            logger.info("指定日期前一周的平均客单価計算完成: 日期={}, 平均客单価={}", date, averageOrderValue);
            return averageOrderValue;

        } catch (Exception e) {
            logger.error("计算指定日期前一周的平均客单価失败，日期: {}", date, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算指定日期所在自然月的平均客单価
     * 平均客单価 = 月销售总额 / 月下单用户数
     *
     * @param date 指定日期
     * @return 平均客单価，保留2位小数
     */
    public BigDecimal calculateMonthlyAverageOrderValue(LocalDate date) {
        try {
            logger.info("開始計算指定日期所在自然月的平均客单価，日期: {}", date);

            // 获取月销售总额
            BigDecimal monthlySales = orderMapper.getMonthlySalesAmount(date);
            logger.debug("月销售总额: {}", monthlySales);

            // 获取月下单用户数
            Long monthlyUsers = orderMapper.getMonthlyOrderingUsers(date);
            logger.debug("月下单用户数: {}", monthlyUsers);

            // 检查数据有效性
            if (monthlySales == null || monthlySales.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("月销售总额为null或小于等于0，返回0");
                return BigDecimal.ZERO;
            }

            if (monthlyUsers == null || monthlyUsers <= 0) {
                logger.warn("月下单用户数为null或小于等于0，无法计算平均客单価，返回0");
                return BigDecimal.ZERO;
            }

            // 计算平均客单価：销售总额 / 下单用户数，保留2位小数
            BigDecimal averageOrderValue = monthlySales.divide(new BigDecimal(monthlyUsers), 2, RoundingMode.HALF_UP);

            logger.info("指定日期所在自然月的平均客单価計算完成: 日期={}, 平均客单価={}", date, averageOrderValue);
            return averageOrderValue;

        } catch (Exception e) {
            logger.error("计算指定日期所在自然月的平均客单価失败，日期: {}", date, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算指定日期前一个月的平均客单価
     * 平均客单価 = 前一月销售总额 / 前一月下单用户数
     *
     * @param date 指定日期
     * @return 平均客单価，保留2位小数
     */
    public BigDecimal calculatePreviousMonthlyAverageOrderValue(LocalDate date) {
        try {
            logger.info("开始计算指定日期前一个月的平均客单価，日期: {}", date);

            // 获取前一月销售总额
            BigDecimal previousMonthlySales = orderMapper.getPreviousMonthlySalesAmount(date);
            logger.debug("前一月销售总额: {}", previousMonthlySales);

            // 获取前一月下单用户数
            Long previousMonthlyUsers = orderMapper.getPreviousMonthOrderingUsers(date);
            logger.debug("前一月下单用户数: {}", previousMonthlyUsers);

            // 检查数据有效性
            if (previousMonthlySales == null || previousMonthlySales.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("前一月销售总额为null或小于等于0，返回0");
                return BigDecimal.ZERO;
            }

            if (previousMonthlyUsers == null || previousMonthlyUsers <= 0) {
                logger.warn("前一月下单用户数为null或小于等于0，无法计算平均客单価，返回0");
                return BigDecimal.ZERO;
            }

            // 计算平均客单価：销售总额 / 下单用户数，保留2位小数
            BigDecimal averageOrderValue = previousMonthlySales.divide(new BigDecimal(previousMonthlyUsers), 2, RoundingMode.HALF_UP);

            logger.info("指定日期前一个月的平均客单価計算完成: 日期={}, 平均客单価={}", date, averageOrderValue);
            return averageOrderValue;

        } catch (Exception e) {
            logger.error("计算指定日期前一个月的平均客单価失败，日期: {}", date, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 获取指定日期的日、周、月销售总额
     *
     * @param date 指定日期
     * @return 包含日、周、月销售总额的Map
     */
    public Map<String, BigDecimal> getSalesAmountStatsByDate(LocalDate date) {
        try {
            logger.info("开始获取指定日期的销售总额统计，日期: {}", date);

            Map<String, BigDecimal> stats = new HashMap<>();

            // 获取日销售总额
            BigDecimal dailySales = orderMapper.getDailySalesAmount(date);
            stats.put("dailySales", dailySales != null ? dailySales : BigDecimal.ZERO);

            // 获取周销售总额
            BigDecimal weeklySales = orderMapper.getWeeklySalesAmount(date);
            stats.put("weeklySales", weeklySales != null ? weeklySales : BigDecimal.ZERO);

            // 获取月销售总额
            BigDecimal monthlySales = orderMapper.getMonthlySalesAmount(date);
            stats.put("monthlySales", monthlySales != null ? monthlySales : BigDecimal.ZERO);

            logger.info("获取指定日期的销售总额统计完成: {}", stats);
            return stats;

        } catch (Exception e) {
            logger.error("获取指定日期的销售总额统计失败，日期: {}", date, e);
            Map<String, BigDecimal> errorStats = new HashMap<>();
            errorStats.put("dailySales", BigDecimal.ZERO);
            errorStats.put("weeklySales", BigDecimal.ZERO);
            errorStats.put("monthlySales", BigDecimal.ZERO);
            return errorStats;
        }
    }

    /**
     * 获取指定日期的销售总额统计（包含增长率）
     *
     * @param date 指定日期
     * @return 包含日、周、月销售总额及其增长率的Map
     */
    public Map<String, Object> getSalesAmountStatsWithGrowthByDate(LocalDate date) {
        try {
            logger.info("开始获取指定日期的销售总额统计（包含增长率），日期: {}", date);

            Map<String, Object> stats = new HashMap<>();

            // 获取当前日、周、月销售总额
            BigDecimal dailySales = orderMapper.getDailySalesAmount(date);
            BigDecimal weeklySales = orderMapper.getWeeklySalesAmount(date);
            BigDecimal monthlySales = orderMapper.getMonthlySalesAmount(date);

            // 获取前一天、前一周、前一个月销售总额
            BigDecimal previousDailySales = orderMapper.getPreviousDaySalesAmount(date);
            BigDecimal previousWeeklySales = orderMapper.getPreviousWeeklySalesAmount(date);
            BigDecimal previousMonthlySales = orderMapper.getPreviousMonthlySalesAmount(date);

            // 计算增长率
            BigDecimal dailyGrowthRate = calculateGrowthRate(dailySales, previousDailySales);
            BigDecimal weeklyGrowthRate = calculateGrowthRate(weeklySales, previousWeeklySales);
            BigDecimal monthlyGrowthRate = calculateGrowthRate(monthlySales, previousMonthlySales);

            // 组装结果
            Map<String, Object> dailyStats = new HashMap<>();
            dailyStats.put("amount", dailySales != null ? dailySales : BigDecimal.ZERO);
            dailyStats.put("growthRate", dailyGrowthRate);
            // dailyStats.put("growthRatePercent", dailyGrowthRate + "%");

            Map<String, Object> weeklyStats = new HashMap<>();
            weeklyStats.put("amount", weeklySales != null ? weeklySales : BigDecimal.ZERO);
            weeklyStats.put("growthRate", weeklyGrowthRate);
            // weeklyStats.put("growthRatePercent", weeklyGrowthRate + "%");

            Map<String, Object> monthlyStats = new HashMap<>();
            monthlyStats.put("amount", monthlySales != null ? monthlySales : BigDecimal.ZERO);
            monthlyStats.put("growthRate", monthlyGrowthRate);
            // monthlyStats.put("growthRatePercent", monthlyGrowthRate + "%");

            stats.put("daily", dailyStats);
            stats.put("weekly", weeklyStats);
            stats.put("monthly", monthlyStats);

            logger.info("获取指定日期的销售总额统计（包含增长率）完成: {}", stats);
            return stats;

        } catch (Exception e) {
            logger.error("获取指定日期的销售总额统计（包含增长率）失败，日期: {}", date, e);
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "获取销售总额统计失败: " + e.getMessage());
            return errorStats;
        }
    }

    /**
     * 计算指定月份人均成交订单数
     * 人均订单数 = 指定月份总订单数 / 实际销售人员数量
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

            // 获取实际销售人员数量
            Long actualSalesCount = getActualSalesCount();

            logger.info("指定月份统计数据 - 月份: {}, 总订单数: {}, 实际销售人员数量: {}", yearMonth, totalOrders, actualSalesCount);

            if (totalOrders == null || totalOrders == 0) {
                logger.warn("指定月份没有成交订单，月份: {}，返回0", yearMonth);
                return BigDecimal.ZERO;
            }

            // 计算人均订单数：总订单数 / 实际销售人员数量，保留2位小数
            BigDecimal avgOrders = new BigDecimal(totalOrders)
                .divide(new BigDecimal(actualSalesCount), 2, RoundingMode.HALF_UP);

            logger.info("指定月份人均订单数计算完成: 月份={}, 人均订单数={}", yearMonth, avgOrders);
            return avgOrders;

        } catch (Exception e) {
            logger.error("计算指定月份人均订单数失败，月份: {}", yearMonth, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算当月人均成交订单数（保持向后兼容）
     * 人均订单数 = 当月总订单数 / 实际销售人员数量
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
     * 人均销售额 = 指定月份总成交额 / 实际销售人员数量
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

            // 获取实际销售人员数量
            Long actualSalesCount = getActualSalesCount();

            logger.info("指定月份统计数据 - 月份: {}, 总销售额: {}, 实际销售人员数量: {}", yearMonth, totalSales, actualSalesCount);

            if (totalSales == null || totalSales.compareTo(BigDecimal.ZERO) <= 0) {
                logger.warn("指定月份没有成交额，月份: {}，返回0", yearMonth);
                return BigDecimal.ZERO;
            }

            // 计算人均销售额：总销售额 / 实际销售人员数量，保留2位小数
            BigDecimal avgSales = totalSales.divide(new BigDecimal(actualSalesCount), 2, RoundingMode.HALF_UP);

            logger.info("指定月份人均销售额计算完成: 月份={}, 人均销售额={}", yearMonth, avgSales);
            return avgSales;

        } catch (Exception e) {
            logger.error("计算指定月份人均销售额失败，月份: {}", yearMonth, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算当月人均成交销售额（保持向后兼容）
     * 人均销售额 = 当月总成交额 / 实际销售人员数量
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
     * 公式：（当前値 - 上期値）/ 上期値 * 100%
     *
     * @param currentValue 当前値
     * @param previousValue 上期値
     * @return 增长率（百分比）
     */
    private BigDecimal calculateGrowthRate(BigDecimal currentValue, BigDecimal previousValue) {
        // 防止除零错误
        if (previousValue == null || previousValue.compareTo(BigDecimal.ZERO) == 0) {
            if (currentValue == null || currentValue.compareTo(BigDecimal.ZERO) == 0) {
                logger.debug("当前値和上期値都为0，增长率设为0");
                return BigDecimal.ZERO;
            } else {
                logger.warn("上期値为0，无法计算增长率，返回100%表示全新增长");
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

            logger.debug("增长率计算: 当前値={}, 上期値={}, 增长率={}%", currentValue, previousValue, growthRate);
            return growthRate;

        } catch (Exception e) {
            logger.error("计算增长率失败: 当前値={}, 上期値={}", currentValue, previousValue, e);
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
     * 包括人均订单数和人均销售额（都基于实际销售人员数量计算）
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

            logger.info("開始計算指定月份综合统计，月份: {}，时间范围: {} - {}", yearMonth, startTime, endTime);

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
     * 包括人均订单数和人均销售额（都基于实际销售人员数量计算）
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

    /**
     * 计算指定客户的使用服务时间（直接查询ServerTime表）
     *
     * @param clientId 客户ID
     * @return 服务时间（以天为单位），如果无法计算则返回null
     */
    public Double calculateServiceTimeForClient(String clientId) {
        try {
            logger.info("开始查询客户的服务时间，客户ID: {}", clientId);

            // 直接查询ServerTime表获取服务时间
            ServerTime serverTime = serverTimeMapper.getByClientId(clientId);

            // 检查是否获取到服务时间记录
            if (serverTime == null || serverTime.getColSerTi() == null) {
                logger.warn("无法获取客户的服务时间数据，客户ID: {}", clientId);
                return null;
            }

            // 从ServerTime表中获取的服务时间是秒数，需要转换为天数
            // colSerTi字段存储的是服务时长（秒）
            long serviceTimeInSeconds = serverTime.getColSerTi();
            double serviceTimeInDays = serviceTimeInSeconds / (24.0 * 60 * 60);

            logger.info("客户服务时间查询完成: 客户ID={}, 服务时间={}秒 ({}天)", clientId, serviceTimeInSeconds, serviceTimeInDays);

            return serviceTimeInDays;

        } catch (Exception e) {
            logger.error("查询客户服务时间失败，客户ID: {}", clientId, e);
            return null;
        }
    }

    /**
     * 计算所有客户的平均服务时间
     * 先计算每个用户的服务时间，之后对所有用户服务时间取平均值
     *
     * @return 平均服务时间（以天为单位）
     */
    public Double calculateAverageServiceTime() {
        try {
            logger.info("开始计算所有客户的平均服务时间");

            // 直接从ServerTime表中获取所有客户的服务时间
            List<ServerTime> serverTimes = serverTimeService.getAllServerTimes();

            if (serverTimes == null || serverTimes.isEmpty()) {
                logger.warn("没有找到任何客户的服务时间记录");
                return null;
            }

            logger.info("找到 {} 条客户的服务时间记录", serverTimes.size());

            // 计算总服务时间
            double totalServiceTime = 0.0;
            int validRecordCount = 0;

            for (ServerTime serverTime : serverTimes) {
                if (serverTime.getColSerTi() != null) {
                    // 将服务时间（秒数）转换为天数并累加
                    double serviceTimeInDays = serverTime.getColSerTi() / (24.0 * 60 * 60);
                    totalServiceTime += serviceTimeInDays;
                    validRecordCount++;
                }
            }

            // 检查是否有有效的记录
            if (validRecordCount == 0) {
                logger.warn("没有有效的客户服务时间数据");
                return null;
            }

            // 计算平均服务时间
            double averageServiceTime = totalServiceTime / validRecordCount;

            logger.info("所有客户平均服务时间计算完成: 记录总数={}, 有效记录数={}, 总服务时间={}天, 平均服务时间={}天",
                    serverTimes.size(), validRecordCount, totalServiceTime, averageServiceTime);

            return averageServiceTime;

        } catch (Exception e) {
            logger.error("计算所有客户的平均服务时间失败", e);
            return null;
        }
    }

    /**
     * 获取指定日期之后首次下单的客户ID列表
     *
     * @param date 指定日期
     * @return 在指定日期之后首次下单的客户ID列表
     */
    public List<String> getNewClientsAfterDate(LocalDateTime date) {
        try {
            logger.info("开始查询指定日期之后首次下单的客户，日期: {}", date);

            List<String> newClientIds = orderMapper.getNewClientIdsByMothy(date);

            logger.info("查询完成，找到 {} 个在指定日期之后首次下单的客户",
                    newClientIds != null ? newClientIds.size() : 0);

            return newClientIds;

        } catch (Exception e) {
            logger.error("查询指定日期之后首次下单的客户失败，日期: {}", date, e);
            return null;
        }
    }

    /**
     * 计算指定日期之后用户的十日成交转换率
     * 十日成交转换率 = 服务时间在2天到10天之间的用户数 / 指定日期之后用户数
     *
     * @param date 指定日期
     * @return 十日成交转换率
     */
    public Double calculateTenDayConversionRate(LocalDateTime date) {
        try {
            logger.info("开始计算指定日期之后用户的十日成交转换率，日期: {}", date);
            // 获取指定日期之后首次下单的客户ID列表
            List<String> newClientIds = getNewClientsAfterDate(date);

            // 检查是否有客户数据
            if (newClientIds == null || newClientIds.isEmpty()) {
                logger.warn("指定日期之后没有新客户");
                return 0.0;
            }
            LocalDateTime MontyFirstDay = date.withDayOfMonth(1).with(LocalTime.MIN);
            // 直接从ServerTime表中获取指定日期之后所有客户的服务时间
            Map<String, Double> serviceTimeMap = getServiceTimeForClientsAfterDate(MontyFirstDay);

            // 计算服务时间在2天到10天之间的客户数
            int clientsWithValidServiceTime = 0;
            for (Map.Entry<String, Double> entry : serviceTimeMap.entrySet()) {
                Double serviceTime = entry.getValue();
                // 服务时间大于等于2天且小于10天的客户才被认为是成交用户
                if (serviceTime != null && serviceTime >= 1.0 && serviceTime < 10.0) {
                    clientsWithValidServiceTime++;
                }
            }

            // 计算十日成交转换率
            int totalNewClients = newClientIds.size();
            double conversionRate = (double) clientsWithValidServiceTime / totalNewClients;

            logger.info("十日成交转换率計算完成: 指定日期之后用户数={}, 服务时间在2天到10天之间的用户数={}, 转换率={}",
                    totalNewClients, clientsWithValidServiceTime, conversionRate);

            return conversionRate;

        } catch (Exception e) {
            logger.error("计算十日成交转换率失败，日期: {}", date, e);
            return null;
        }
    }

    /**
     * 获取指定日期之后所有客户的服务时间
     *
     * @param date 指定日期
     * @return 客户ID和服务时间的映射
     */
    public Map<String, Double> getServiceTimeForClientsAfterDate(LocalDateTime date) {
        try {
            logger.info("开始获取指定日期之后所有客户的服务时间，日期: {}", date);

            // 直接从ServerTime表中获取所有客户的服务时间
            List<ServerTime> serverTimes = serverTimeService.getServerTimesAfterDate(date.toLocalDate());

            // 检查是否有服务时间数据
            if (serverTimes == null || serverTimes.isEmpty()) {
                logger.warn("指定日期之后没有客户的服务时间记录");
                return new HashMap<>();
            }

            logger.info("找到 {} 条客户的服务时间记录", serverTimes.size());

            // 转换服务时间为天数并构建映射
            Map<String, Double> serviceTimeMap = new HashMap<>();

            for (ServerTime serverTime : serverTimes) {
                // 将服务时间（秒数）转换为天数
                double serviceTimeInDays = serverTime.getColSerTi() / (24.0 * 60 * 60);
                serviceTimeMap.put(serverTime.getColCltID(), serviceTimeInDays);
            }

            logger.info("获取指定日期之后所有客户的服务时间完成，有效客户数: {}", serviceTimeMap.size());

            return serviceTimeMap;

        } catch (Exception e) {
            logger.error("获取指定日期之后所有客户的服务时间失败，日期: {}", date, e);
            return new HashMap<>();
        }
    }

    /**
     * 计算指定日期之后用户的十五日成交转换率
     * 十五日成交转换率 = 服务时间在2天到15天之间的用户数 / 指定日期之后用户数
     *
     * @param date 指定日期
     * @return 十五日成交转换率
     */
    public Double calculateFifteenDayConversionRate(LocalDateTime date) {
        try {
            logger.info("开始计算指定日期之后用户的十五日成交转换率，日期: {}", date);

            // 获取指定日期之后首次下单的客户ID列表
            List<String> newClientIds = getNewClientsAfterDate(date);

            // 检查是否有客户数据
            if (newClientIds == null || newClientIds.isEmpty()) {
                logger.warn("指定日期之后没有新客户");
                return 0.0;
            }
            LocalDateTime FirstDayOfMonth = date.withDayOfMonth(1).with(LocalTime.MIN);
            // 直接从ServerTime表中获取指定日期之后所有客户的服务时间
            Map<String, Double> serviceTimeMap = getServiceTimeForClientsAfterDate(FirstDayOfMonth);

            // 计算服务时间在2天到15天之间的客户数
            int clientsWithValidServiceTime = 0;
            for (Map.Entry<String, Double> entry : serviceTimeMap.entrySet()) {
                Double serviceTime = entry.getValue();
                // 服务时间大于等于2天且小于15天的客户才被认为是成交用户
                if (serviceTime != null && serviceTime >= 2.0 && serviceTime < 15.0) {
                    clientsWithValidServiceTime++;
                }
            }

            // 计算十五日成交转换率
            int totalNewClients = newClientIds.size();
            double conversionRate = (double) clientsWithValidServiceTime / totalNewClients;

            logger.info("十五日成交转换率计算完成: 指定日期之后用户数={}, 服务时间在2天到15天之间的用户数={}, 转换率={}",
                    totalNewClients, clientsWithValidServiceTime, conversionRate);

            return conversionRate;

        } catch (Exception e) {
            logger.error("计算十五日成交转换率失败，日期: {}", date, e);
            return null;
        }
    }

    /**
     * 获取指定日期的日、周、月下单用户数统计结果（包含增长率）
     *
     * @param date 指定日期
     * @return 下单用户数统计结果（包含增长率）
     */
    public OrderingUserStats getOrderingUserStatsByDate(LocalDate date) {
        try {
            logger.info("开始获取指定日期的下单用户数统计，日期: {}", date);

            OrderingUserStats stats = new OrderingUserStats();
            stats.setDate(date);

            // 获取当前日、周、月下单用户数
            Long dailyUsers = orderMapper.getDailyOrderingUsers(date);
            Long weeklyUsers = orderMapper.getWeeklyOrderingUsers(date);
            Long monthlyUsers = orderMapper.getMonthlyOrderingUsers(date);
            
            stats.setDailyOrderingUsers(dailyUsers != null ? dailyUsers : 0L);
            stats.setWeeklyOrderingUsers(weeklyUsers != null ? weeklyUsers : 0L);
            stats.setMonthlyOrderingUsers(monthlyUsers != null ? monthlyUsers : 0L);

            logger.info("获取指定日期的下单用户数统计完成: {}", stats);
            return stats;

        } catch (Exception e) {
            logger.error("获取指定日期的下单用户数统计失败，日期: {}", date, e);
            return new OrderingUserStats(date, 0L, 0L, 0L);
        }
    }
    
    /**
     * 获取指定日期的日、周、月下单用户数统计结果（包含增长率）
     *
     * @param date 指定日期
     * @return 下单用户数统计结果（包含增长率）
     */
    public Map<String, Object> getOrderingUserStatsWithGrowthByDate(LocalDate date) {
        try {
            logger.info("開始获取指定日期的下单用户数统计（包含增长率），日期: {}", date);

            Map<String, Object> stats = new HashMap<>();

            // 获取当前日、周、月下单用户数
            Long dailyUsers = orderMapper.getDailyOrderingUsers(date);
            Long weeklyUsers = orderMapper.getWeeklyOrderingUsers(date);
            Long monthlyUsers = orderMapper.getMonthlyOrderingUsers(date);

            // 获取前一天、前一周、前一个月下单用户数
            Long previousDailyUsers = orderMapper.getPreviousDayOrderingUsers(date);
            Long previousWeeklyUsers = orderMapper.getPreviousWeekOrderingUsers(date);
            Long previousMonthlyUsers = orderMapper.getPreviousMonthOrderingUsers(date);

            // 计算增长率
            BigDecimal dailyGrowthRate = calculateGrowthRate(
                dailyUsers != null ? new BigDecimal(dailyUsers) : BigDecimal.ZERO,
                previousDailyUsers != null ? new BigDecimal(previousDailyUsers) : BigDecimal.ZERO
            );
            
            BigDecimal weeklyGrowthRate = calculateGrowthRate(
                weeklyUsers != null ? new BigDecimal(weeklyUsers) : BigDecimal.ZERO,
                previousWeeklyUsers != null ? new BigDecimal(previousWeeklyUsers) : BigDecimal.ZERO
            );
            
            BigDecimal monthlyGrowthRate = calculateGrowthRate(
                monthlyUsers != null ? new BigDecimal(monthlyUsers) : BigDecimal.ZERO,
                previousMonthlyUsers != null ? new BigDecimal(previousMonthlyUsers) : BigDecimal.ZERO
            );

            // 组装结果
            Map<String, Object> dailyStats = new HashMap<>();
            dailyStats.put("count", dailyUsers != null ? dailyUsers : 0L);
            dailyStats.put("previousCount", previousDailyUsers != null ? previousDailyUsers : 0L);
            dailyStats.put("growthRate", dailyGrowthRate);
//            dailyStats.put("growthRatePercent", dailyGrowthRate + "%");

            Map<String, Object> weeklyStats = new HashMap<>();
            weeklyStats.put("count", weeklyUsers != null ? weeklyUsers : 0L);
            weeklyStats.put("previousCount", previousWeeklyUsers != null ? previousWeeklyUsers : 0L);
            weeklyStats.put("growthRate", weeklyGrowthRate);
//            weeklyStats.put("growthRatePercent", weeklyGrowthRate + "%");

            Map<String, Object> monthlyStats = new HashMap<>();
            monthlyStats.put("count", monthlyUsers != null ? monthlyUsers : 0L);
            monthlyStats.put("previousCount", previousMonthlyUsers != null ? previousMonthlyUsers : 0L);
            monthlyStats.put("growthRate", monthlyGrowthRate);
//            monthlyStats.put("growthRatePercent", monthlyGrowthRate + "%");

            stats.put("daily", dailyStats);
            stats.put("weekly", weeklyStats);
            stats.put("monthly", monthlyStats);

            logger.info("获取指定日期的下单用户数统计（包含增长率）完成: {}", stats);
            return stats;

        } catch (Exception e) {
            logger.error("获取指定日期的下单用户数统计（包含增长率）失败，日期: {}", date, e);
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("error", "获取下单用户数统计失败: " + e.getMessage());
            return errorStats;
        }
    }

}
