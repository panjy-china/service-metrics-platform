package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.call.CallDurationStatistics;
import org.panjy.servicemetricsplatform.entity.mealcomletion.MealCheckinCompletionRate;
import org.panjy.servicemetricsplatform.entity.order.OrderRetentionRate;
import org.panjy.servicemetricsplatform.service.*;
import org.panjy.servicemetricsplatform.service.call.CallCountComplianceRateService;
import org.panjy.servicemetricsplatform.service.call.CallDurationStatisticsService;
import org.panjy.servicemetricsplatform.service.call.FirstCallSummaryService;
import org.panjy.servicemetricsplatform.service.call.OrderCallTimeDiffService;
import org.panjy.servicemetricsplatform.service.conversionrate.ClientServiceStatsService;
import org.panjy.servicemetricsplatform.service.mealcomletion.MealCheckinCompletionRateService;
import org.panjy.servicemetricsplatform.service.message.UserFirstFeedbackService;
import org.panjy.servicemetricsplatform.service.message.UserGuidanceStatService;
import org.panjy.servicemetricsplatform.service.serverTime.ServerTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

/**
 * 数据看板总接口控制器
 * 整合所有数据看板指标，提供统一的数据查询接口
 */
@RestController
@RequestMapping("/api/data-dashboard")
@CrossOrigin(origins = "*") // 允许跨域请求
public class DataDashboardController {

    // 首电相关服务
    @Autowired
    private FirstCallSummaryService firstCallSummaryService;

    // 用户基础资料提交相关服务
    @Autowired
    private UserFirstFeedbackService userFirstFeedbackService;

    // 用户三餐打卡相关服务
    @Autowired
    private UserMealCheckinService userMealCheckinService;

    // 饮食指导相关服务
    @Autowired
    private UserGuidanceStatService userGuidanceStatService;

    // 通话次数相关服务
    @Autowired
    private CallCountComplianceRateService callCountComplianceRateService;

    // 首电完成时间差相关服务
    @Autowired
    private OrderCallTimeDiffService orderCallTimeDiffService;

    // 通话时长统计服务
    @Autowired
    private CallDurationStatisticsService callDurationStatisticsService;

    // 个性化中医指导服务
    @Autowired
    private TraditionalChineseMedicineGuidanceService traditionalChineseMedicineGuidanceService;

    // 餐食打卡完成率服务
    @Autowired
    private MealCheckinCompletionRateService mealCheckinCompletionRateService;

    // 客户服务统计数据服务（用于推单成交率）
    @Autowired
    private ClientServiceStatsService clientServiceStatsService;

    // 服务时间服务（用于推单后留存率）
    @Autowired
    private ServerTimeService serverTimeService;

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
     * 获取数据看板所有指标数据总接口（包含通话时长统计）
     * 整合所有数据看板的指标数据，包括通话时长统计
     *
     * @return 数据看板所有指标数据
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getAllDataDashboardMetrics() {
        try {
            System.out.println("开始获取数据看板所有指标数据（包含通话时长统计）");

            // 创建综合数据Map
            Map<String, Object> dashboardData = new HashMap<>();

            // 1. 获取首电相关指标数据（使用无参数版本）
            getFirstCallMetrics(dashboardData);

            // 2. 获取用户基础资料提交相关指标数据
            getUserBasicInfoMetrics(dashboardData);

            // 3. 获取用户三餐打卡相关指标数据
            getUserMealCheckinMetrics(dashboardData);

            // 4. 获取饮食指导相关指标数据
            getDietaryGuidanceMetrics(dashboardData);

            // 5. 获取通话次数相关指标数据
            getCallCountMetrics(dashboardData);

            // 6. 获取首电完成时间差相关指标数据
            getOrderCallTimeDiffMetrics(dashboardData);

            // 7. 获取通话时长统计指标数据
            getCallDurationStatisticsMetrics(dashboardData);

            // 8. 获取个性化中医指导完成率指标数据
            getTraditionalChineseMedicineGuidanceMetrics(dashboardData);

            // 9. 获取舌苔照片提交比例指标数据
            getTonguePhotoSubmissionRateMetrics(dashboardData);

            // 10. 获取体型照片提交比例指标数据
            getBodyTypePhotoSubmissionRateMetrics(dashboardData);

            // 11. 获取餐食打卡完成率指标数据
            getMealCheckinCompletionRateMetrics(dashboardData);
            
            // 12. 获取推单成交率指标数据
            getPushOrderConversionRateMetrics(dashboardData);
            
            // 13. 获取推单后留存率指标数据
            getOrderRetentionRateMetrics(dashboardData);

            System.out.println("数据看板所有指标数据（包含通话时长统计）获取完成");
            return ResponseEntity.ok(createSuccessResponse("查询成功", dashboardData));

        } catch (Exception e) {
            System.err.println("获取数据看板所有指标数据（包含通话时长统计）失败: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(500).body(
                createErrorResponse("查询失败: " + e.getMessage(), "QUERY_ERROR")
            );
        }
    }

    /**
     * 获取指定日期的数据看板指标数据
     * 根据指定日期查询和计算相关的数据看板指标
     *
     * @param date 查询日期 (格式: yyyy-MM-dd)
     * @return 指定日期的数据看板指标数据
     */
    @GetMapping("/metrics/{date}")
    public ResponseEntity<Map<String, Object>> getDataDashboardMetricsByDate(
            @PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        try {
            // 检查日期参数是否为空
            if (date == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("日期参数不能为空", "INVALID_DATE")
                );
            }

            System.out.println("开始获取指定日期的数据看板指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 创建综合数据Map
            Map<String, Object> dashboardData = new HashMap<>();

            // 1. 获取首电相关指标数据（传入日期参数）
            getFirstCallMetrics(dashboardData, date);

            // 2. 获取用户基础资料提交相关指标数据
            getUserBasicInfoMetrics(dashboardData, date);

            // 3. 获取用户三餐打卡相关指标数据
            getUserMealCheckinMetrics(dashboardData, date);

            // 4. 获取饮食指导相关指标数据
            getDietaryGuidanceMetrics(dashboardData, date);

            // 5. 获取通话次数相关指标数据
            getCallCountMetrics(dashboardData, date);

            // 6. 获取首电完成时间差相关指标数据
            getOrderCallTimeDiffMetrics(dashboardData, date);

            // 7. 获取通话时长统计指标数据
            getCallDurationStatisticsMetrics(dashboardData, date);

            // 8. 获取个性化中医指导完成率指标数据
            getTraditionalChineseMedicineGuidanceMetrics(dashboardData, date);

            // 9. 获取舌苔照片提交比例指标数据
            getTonguePhotoSubmissionRateMetrics(dashboardData, date);

            // 10. 获取体型照片提交比例指标数据
            getBodyTypePhotoSubmissionRateMetrics(dashboardData, date);

            // 11. 获取餐食打卡完成率指标数据
            getMealCheckinCompletionRateMetrics(dashboardData, date);
            
            // 12. 获取推单成交率指标数据
            getPushOrderConversionRateMetrics(dashboardData, date);
            
            // 13. 获取推单后留存率指标数据
            getOrderRetentionRateMetrics(dashboardData, date);

            System.out.println("指定日期的数据看板指标数据获取完成，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));
            return ResponseEntity.ok(createSuccessResponse("查询成功", dashboardData));

        } catch (Exception e) {
            System.err.println("获取指定日期的数据看板指标数据失败: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(500).body(
                createErrorResponse("查询失败: " + e.getMessage(), "QUERY_ERROR")
            );
        }
    }

    /**
     * 获取首电相关指标数据（无日期参数，使用所有数据）
     *
     * @param data 存储首电相关指标数据的Map对象
     */
    private void getFirstCallMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取首电相关指标数据");

            // 首电平均通话时长
            double averageCallDuration = firstCallSummaryService.calculateAverageCallDuration();
//            Map<String, Object> averageCallDurationMap = new HashMap<>();
//            averageCallDurationMap.put("averageCallDuration", averageCallDuration);
//            averageCallDurationMap.put("unit", "seconds");
            data.put("firstCallAverageDuration", String.format("%.2f", averageCallDuration / 60.0));

            // 首电时长达标电话比例
            double qualifiedRate = firstCallSummaryService.calculateQualifiedRate();
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("firstCallQualifiedRate", String.format("%.2f", qualifiedRate * 100));

            System.out.println("首电相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取首电相关指标数据失败: " + e.getMessage());
            data.put("firstCallAverageDuration", new HashMap<>());
            data.put("firstCallQualifiedRate", "0.00");
        }
    }

    /**
     * 获取首电相关指标数据（传入日期参数）
     *
     * @param data 存储首电相关指标数据的Map对象
     * @param date 查询日期
     */
    private void getFirstCallMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取首电相关指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDateTime
            java.time.LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // 1. 获取指定月份时长达标电话比例及与上个月相比的增长率
            Map<String, Object> qualifiedRateResult = firstCallSummaryService.calculateQualifiedRateByMonth(localDateTime);
            Double qualifiedRate = (Double) qualifiedRateResult.get("rate");
            Double qualifiedRateGrowth = (Double) qualifiedRateResult.get("growthRate");
            
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("firstCallQualifiedRate", String.format("%.2f", qualifiedRate * 100));
            data.put("firstCallQualifiedRateGrowth", String.format("%.2f", qualifiedRateGrowth * 100));

            // 2. 获取指定月份所有首通电话的平均通话时长及与上个月相比的增长率
            Map<String, Object> averageCallDurationResult = firstCallSummaryService.calculateAverageCallDurationByMonth(localDateTime);
            Double averageCallDuration = (Double) averageCallDurationResult.get("averageDuration");
            Double averageCallDurationGrowth = (Double) averageCallDurationResult.get("growthRate");
            
            // 统一格式为保留小数点后两位，单位为分钟
            data.put("firstCallAverageDuration", String.format("%.2f", averageCallDuration / 60.0));
            data.put("firstCallAverageDurationGrowth", String.format("%.2f", averageCallDurationGrowth * 100));

            // 3. 获取指定月份时长达标电话总数和电话总数及上个月的对应数据
            Map<String, Object> countResult = firstCallSummaryService.getQualifiedAndTotalCountByMonth(localDateTime);
            Long qualifiedCount = (Long) countResult.get("qualifiedCount");
            Long totalCount = (Long) countResult.get("totalCount");
            Long previousQualifiedCount = (Long) countResult.get("previousQualifiedCount");
            Long previousTotalCount = (Long) countResult.get("previousTotalCount");
            
            Map<String, Object> countData = new HashMap<>();
            countData.put("qualifiedCount", qualifiedCount);
            countData.put("totalCount", totalCount);
            countData.put("previousQualifiedCount", previousQualifiedCount);
            countData.put("previousTotalCount", previousTotalCount);
            data.put("firstCallCountData", countData);

            System.out.println("首电相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取首电相关指标数据失败: " + e.getMessage());
            data.put("firstCallAverageDuration", "0.00");
            data.put("firstCallQualifiedRate", "0.00");
            data.put("firstCallAverageDurationGrowth", "0.00");
            data.put("firstCallQualifiedRateGrowth", "0.00");
            data.put("firstCallCountData", new HashMap<>());
        }
    }

    /**
     * 获取用户基础资料提交相关指标数据（无日期参数）
     *
     * @param data 存储用户基础资料提交相关指标数据的Map对象
     */
    private void getUserBasicInfoMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取用户基础资料提交相关指标数据");

            // 基础资料提交率
            String basicInfoRate = userFirstFeedbackService.calculateBasicInfoSubmissionRate();
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("userBasicInfoSubmissionRate", basicInfoRate.replace("%", ""));

//            // 基础资料提交统计详情
//            long[] basicInfoStats = userFirstFeedbackService.getBasicInfoSubmissionStats();
//            Map<String, Object> statsData = new HashMap<>();
//            statsData.put("feedbackNums", basicInfoStats[0]);
//            statsData.put("totalRecords", basicInfoStats[1]);
//            data.put("userBasicInfoSubmissionStats", statsData);

            System.out.println("用户基础资料提交相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取用户基础资料提交相关指标数据失败: " + e.getMessage());
            data.put("userBasicInfoSubmissionRate", "0.00");
            data.put("userBasicInfoSubmissionStats", new HashMap<>());
        }
    }

    /**
     * 获取用户基础资料提交相关指标数据（传入日期参数）
     *
     * @param data 存储用户基础资料提交相关指标数据的Map对象
     * @param date 查询日期
     */
    private void getUserBasicInfoMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取用户基础资料提交相关指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDateTime
            java.time.LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // 计算指定月份的基础资料提交率及环比增长率
            Map<String, Object> result = userFirstFeedbackService.calculateBasicInfoSubmissionRateWithGrowth(localDateTime);
            String currentRate = (String) result.get("currentRate");
            String growthRate = (String) result.get("growthRate");

            // 存储结果
            data.put("userBasicInfoSubmissionRate", currentRate);
            data.put("userBasicInfoSubmissionRateGrowth", growthRate);

            System.out.println("用户基础资料提交相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取用户基础资料提交相关指标数据失败: " + e.getMessage());
            data.put("userBasicInfoSubmissionRate", "0.00");
            data.put("userBasicInfoSubmissionRateGrowth", "0.00");
        }
    }

    /**
     * 获取用户三餐打卡相关指标数据（无日期参数）
     *
     * @param data 存储用户三餐打卡相关指标数据的Map对象
     */
    private void getUserMealCheckinMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取用户三餐打卡相关指标数据");

            // 所有用户三餐打卡率
            String allUsersMealCheckinRate = userMealCheckinService.calculateAllUsersMealCheckinRate();
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("allUsersMealCheckinRate", allUsersMealCheckinRate.replace("%", ""));

            // 体重反馈完成率
            String weightFeedbackCompletionRate = userMealCheckinService.calculateWeightFeedbackCompletionRate();
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("weightFeedbackCompletionRate", weightFeedbackCompletionRate.replace("%", ""));

            System.out.println("用户三餐打卡相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取用户三餐打卡相关指标数据失败: " + e.getMessage());
            data.put("allUsersMealCheckinRate", "0.00");
            data.put("weightFeedbackCompletionRate", "0.00");
        }
    }

    /**
     * 获取用户三餐打卡相关指标数据（传入日期参数）
     *
     * @param data 存储用户三餐打卡相关指标数据的Map对象
     * @param date 查询日期
     */
    private void getUserMealCheckinMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取用户三餐打卡相关指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDateTime
            LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // 计算指定月份的所有用户三餐打卡率及环比增长率
            Map<String, Object> mealCheckinResult = userMealCheckinService.calculateMealCheckinRateWithGrowth(localDateTime);
            String currentMealCheckinRate = (String) mealCheckinResult.get("currentRate");
            String mealCheckinGrowthRate = (String) mealCheckinResult.get("growthRate");

            // 存储结果
            data.put("allUsersMealCheckinRate", currentMealCheckinRate);
            data.put("allUsersMealCheckinRateGrowth", mealCheckinGrowthRate);

            // 计算指定月份的体重反馈完成率及环比增长率
            Map<String, Object> weightFeedbackResult = userMealCheckinService.calculateWeightFeedbackRateWithGrowth(localDateTime);
            String currentWeightFeedbackRate = (String) weightFeedbackResult.get("currentRate");
            String weightFeedbackGrowthRate = (String) weightFeedbackResult.get("growthRate");

            // 存储结果
            data.put("weightFeedbackCompletionRate", currentWeightFeedbackRate);
            data.put("weightFeedbackCompletionRateGrowth", weightFeedbackGrowthRate);

            System.out.println("用户三餐打卡相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取用户三餐打卡相关指标数据失败: " + e.getMessage());
            data.put("allUsersMealCheckinRate", "0.00");
            data.put("allUsersMealCheckinRateGrowth", "0.00");
            data.put("weightFeedbackCompletionRate", "0.00");
            data.put("weightFeedbackCompletionRateGrowth", "0.00");
        }
    }

    /**
     * 获取饮食指导相关指标数据（无日期参数）
     *
     * @param data 存储饮食指导相关指标数据的Map对象
     */
    private void getDietaryGuidanceMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取饮食指导相关指标数据");

            // 饮食指导触达率
            BigDecimal guidanceReachRate = userGuidanceStatService.calculateTotalGuidanceReachRate();
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("dietaryGuidanceReachRate", guidanceReachRate.toString());

            System.out.println("饮食指导相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取饮食指导相关指标数据失败: " + e.getMessage());
            data.put("dietaryGuidanceReachRate", "0.00");
        }
    }

    /**
     * 获取饮食指导相关指标数据（传入日期参数）
     *
     * @param data 存储饮食指导相关指标数据的Map对象
     * @param date 查询日期
     */
    private void getDietaryGuidanceMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取饮食指导相关指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDateTime
            LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // 计算指定月份的饮食指导触达率及环比增长率
            Map<String, Object> guidanceReachResult = userGuidanceStatService.calculateGuidanceReachRateWithGrowth(localDateTime);
            String currentGuidanceReachRate = (String) guidanceReachResult.get("currentRate");
            String guidanceReachGrowthRate = (String) guidanceReachResult.get("growthRate");

            // 存储结果
            data.put("dietaryGuidanceReachRate", currentGuidanceReachRate);
            data.put("dietaryGuidanceReachRateGrowth", guidanceReachGrowthRate);

            System.out.println("饮食指导相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取饮食指导相关指标数据失败: " + e.getMessage());
            data.put("dietaryGuidanceReachRate", "0.00");
            data.put("dietaryGuidanceReachRateGrowth", "0.00");
        }
    }

    /**
     * 获取通话次数相关指标数据
     *
     * @param data 存储通话次数相关指标数据的Map对象
     */
    private void getCallCountMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取通话次数相关指标数据");

            // 四次通话达标率
            double fourCallRate = callCountComplianceRateService.calculateFourCallComplianceRate();
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("fourCallComplianceRate", String.format("%.2f", fourCallRate));

            // 六次通话达标率
            double sixCallRate = callCountComplianceRateService.calculateSixCallComplianceRate();
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("sixCallComplianceRate", String.format("%.2f", sixCallRate));

            // 通话次数统计详情
//            long[] callCountStats = callCountComplianceRateService.getCallCountStatistics();
//            Map<String, Object> statsData = new HashMap<>();
//            statsData.put("totalCount", callCountStats[0]);
//            statsData.put("fourCallCompliantCount", callCountStats[1]);
//            statsData.put("sixCallCompliantCount", callCountStats[2]);
//            statsData.put("fourCallComplianceRateDetail", callCountStats[0] > 0 ? (double) callCountStats[1] / callCountStats[0] * 100 : 0.0);
//            statsData.put("sixCallComplianceRateDetail", callCountStats[0] > 0 ? (double) callCountStats[2] / callCountStats[0] * 100 : 0.0);
//            data.put("callCountStatistics", statsData);

            System.out.println("通话次数相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取通话次数相关指标数据失败: " + e.getMessage());
            data.put("fourCallComplianceRate", "0.00");
            data.put("sixCallComplianceRate", "0.00");
            data.put("callCountStatistics", new HashMap<>());
        }
    }
    
    /**
     * 获取通话次数相关指标数据（传入日期参数）
     *
     * @param data 存储通话次数相关指标数据的Map对象
     * @param date 查询日期
     */
    private void getCallCountMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取通话次数相关指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为年月字符串格式 (yyyy-MM)
            java.time.LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            String yearMonth = localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));

            // 四次通话达标率
            double fourCallRate = callCountComplianceRateService.calculateFourCallComplianceRateByMonth(yearMonth);
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("fourCallComplianceRate", String.format("%.2f", fourCallRate));

            // 六次通话达标率
            double sixCallRate = callCountComplianceRateService.calculateSixCallComplianceRateByMonth(yearMonth);
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("sixCallComplianceRate", String.format("%.2f", sixCallRate));

            // 四次通话达标率环比增长率
            double fourCallRateGrowth = callCountComplianceRateService.calculateFourCallComplianceRateGrowthByMonth(yearMonth);
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("fourCallComplianceRateGrowth", String.format("%.2f", fourCallRateGrowth));

            // 六次通话达标率环比增长率
            double sixCallRateGrowth = callCountComplianceRateService.calculateSixCallComplianceRateGrowthByMonth(yearMonth);
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("sixCallComplianceRateGrowth", String.format("%.2f", sixCallRateGrowth));

            System.out.println("通话次数相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取通话次数相关指标数据失败: " + e.getMessage());
            data.put("fourCallComplianceRate", "0.00");
            data.put("sixCallComplianceRate", "0.00");
            data.put("fourCallComplianceRateGrowth", "0.00");
            data.put("sixCallComplianceRateGrowth", "0.00");
        }
    }

    /**
     * 获取首电完成时间差相关指标数据
     *
     * @param data 存储首电完成时间差相关指标数据的Map对象
     */
    private void getOrderCallTimeDiffMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取首电完成时间差相关指标数据");

            // 首电完成平均用时
            BigDecimal averageCallCompletionTime = orderCallTimeDiffService.calculateAverageCallCompletionTimeInDays();
            data.put("firstCallAverageCompletionTime", String.format("%.2f", averageCallCompletionTime));

            // 首电完成时间差统计信息
//            OrderCallTimeDiffService.TimeDiffStatistics timeDiffStats = orderCallTimeDiffService.getStatistics();
//            Map<String, Object> statsData = new HashMap<>();
//            statsData.put("totalCount", timeDiffStats.getTotalCount());
//            statsData.put("filteredCount", timeDiffStats.getFilteredCount());
//            statsData.put("averageTimeInDays", timeDiffStats.getAverageTime());
//            data.put("firstCallTimeDiffStatistics", statsData);

            System.out.println("首电完成时间差相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取首电完成时间差相关指标数据失败: " + e.getMessage());
            data.put("firstCallAverageCompletionTime", BigDecimal.ZERO);
            data.put("firstCallTimeDiffStatistics", new HashMap<>());
        }
    }
    
    /**
     * 获取首电完成时间差相关指标数据（传入日期参数）
     *
     * @param data 存储首电完成时间差相关指标数据的Map对象
     * @param date 查询日期
     */
    private void getOrderCallTimeDiffMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取首电完成时间差相关指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为年月字符串格式 (yyyy-MM)
            java.time.LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            String yearMonth = localDateTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));

            // 首电完成平均用时
            BigDecimal averageCallCompletionTime = orderCallTimeDiffService.calculateAverageCallCompletionTimeInDaysByMonth(yearMonth);
            data.put("firstCallAverageCompletionTime", String.format("%.2f", averageCallCompletionTime));

            // 首电完成平均用时环比增长率
            double averageCallCompletionTimeGrowth = orderCallTimeDiffService.calculateAverageCallCompletionTimeGrowthByMonth(yearMonth);
            data.put("firstCallAverageCompletionTimeGrowth", String.format("%.2f", averageCallCompletionTimeGrowth));

            System.out.println("首电完成时间差相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取首电完成时间差相关指标数据失败: " + e.getMessage());
            data.put("firstCallAverageCompletionTime", BigDecimal.ZERO);
            data.put("firstCallAverageCompletionTimeGrowth", "0.00");
        }
    }

    /**
     * 获取通话时长统计指标数据
     *
     * @param data 存储通话时长统计指标数据的Map对象
     */
    private void getCallDurationStatisticsMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取通话时长统计指标数据");

            // 通话时长统计
            List<CallDurationStatistics> callDurationStats = callDurationStatisticsService.getCallDurationStatistics();
            data.put("callDurationStatistics", callDurationStats);

            System.out.println("通话时长统计指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取通话时长统计指标数据失败: " + e.getMessage());
            data.put("callDurationStatistics", new ArrayList<>());
        }
    }

    /**
     * 获取通话时长统计指标数据（传入日期参数）
     *
     * @param data 存储通话时长统计指标数据的Map对象
     * @param date 查询日期
     */
    private void getCallDurationStatisticsMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取通话时长统计指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDateTime
            LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // 获取指定月份的通话时长统计及环比增长率
            Map<String, Object> result = callDurationStatisticsService.getCallDurationStatisticsWithGrowthByMonth(localDateTime);
            List<CallDurationStatistics> currentStats = (List<CallDurationStatistics>) result.get("currentStats");
            Double growthRate = (Double) result.get("growthRate");

            // 存储结果
            data.put("callDurationStatistics", currentStats);
            data.put("callDurationStatisticsGrowth", String.format("%.2f", growthRate * 100));

            System.out.println("通话时长统计指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取通话时长统计指标数据失败: " + e.getMessage());
            data.put("callDurationStatistics", new ArrayList<>());
            data.put("callDurationStatisticsGrowth", "0.00");
        }
    }

    /**
     * 获取个性化中医指导完成率指标数据（无日期参数）
     *
     * @param data 存储个性化中医指导完成率指标数据的Map对象
     */
    private void getTraditionalChineseMedicineGuidanceMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取个性化中医指导完成率指标数据");

            // 个性化中医指导完成率
            BigDecimal completionRate = orderCallTimeDiffService.calculatePersonalizedGuidanceCompletionRate();
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("traditionalChineseMedicineGuidanceCompletionRate", String.format("%.2f", completionRate.multiply(BigDecimal.valueOf(100))));

            System.out.println("个性化中医指导完成率指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取个性化中医指导完成率指标数据失败: " + e.getMessage());
            data.put("traditionalChineseMedicineGuidanceCompletionRate", "0.00");
        }
    }

    /**
     * 获取个性化中医指导完成率指标数据（传入日期参数）
     *
     * @param data 存储个性化中医指导完成率指标数据的Map对象
     * @param date 查询日期
     */
    private void getTraditionalChineseMedicineGuidanceMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取个性化中医指导完成率指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDateTime
            LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // 计算指定月份的个性化中医指导完成率及环比增长率
            Map<String, Object> guidanceCompletionResult = traditionalChineseMedicineGuidanceService.calculateGuidanceCompletionRateWithGrowth(localDateTime);
            String currentGuidanceCompletionRate = (String) guidanceCompletionResult.get("currentRate");
            String guidanceCompletionGrowthRate = (String) guidanceCompletionResult.get("growthRate");

            // 存储结果
            data.put("traditionalChineseMedicineGuidanceCompletionRate", currentGuidanceCompletionRate);
            data.put("traditionalChineseMedicineGuidanceCompletionRateGrowth", guidanceCompletionGrowthRate);

            System.out.println("个性化中医指导完成率指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取个性化中医指导完成率指标数据失败: " + e.getMessage());
            data.put("traditionalChineseMedicineGuidanceCompletionRate", "0.00");
            data.put("traditionalChineseMedicineGuidanceCompletionRateGrowth", "0.00");
        }
    }

    /**
     * 获取舌苔照片提交比例指标数据
     *
     * @param data 存储舌苔照片提交比例指标数据的Map对象
     */
    private void getTonguePhotoSubmissionRateMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取舌苔照片提交比例指标数据");

            // 舌苔照片提交比例
            double rate = userFirstFeedbackService.calculateTonguePhotoSubmissionRate();
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("tonguePhotoSubmissionRate", String.format("%.2f", rate * 100));

            System.out.println("舌苔照片提交比例指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取舌苔照片提交比例指标数据失败: " + e.getMessage());
            data.put("tonguePhotoSubmissionRate", "0.00");
        }
    }

    /**
     * 获取舌苔照片提交比例指标数据（传入日期参数）
     *
     * @param data 存储舌苔照片提交比例指标数据的Map对象
     * @param date 查询日期
     */
    private void getTonguePhotoSubmissionRateMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取舌苔照片提交比例指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDateTime
            LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // 计算指定月份的舌苔照片提交率及环比增长率
            Map<String, Object> result = userFirstFeedbackService.calculateTonguePhotoSubmissionRateWithGrowth(localDateTime);
            String currentRate = (String) result.get("currentRate");
            String growthRate = (String) result.get("growthRate");

            // 存储结果
            data.put("tonguePhotoSubmissionRate", currentRate);
            data.put("tonguePhotoSubmissionRateGrowth", growthRate);

            System.out.println("舌苔照片提交比例指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取舌苔照片提交比例指标数据失败: " + e.getMessage());
            data.put("tonguePhotoSubmissionRate", "0.00");
            data.put("tonguePhotoSubmissionRateGrowth", "0.00");
        }
    }

    /**
     * 获取体型照片提交比例指标数据
     *
     * @param data 存储体型照片提交比例指标数据的Map对象
     */
    private void getBodyTypePhotoSubmissionRateMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取体型照片提交比例指标数据");

            // 体型照片提交比例
            double rate = userFirstFeedbackService.calculateBodyTypePhotoSubmissionRate();
            // 统一格式为保留小数点后两位，不带%的百分比
            data.put("bodyTypePhotoSubmissionRate", String.format("%.2f", rate * 100));

            System.out.println("体型照片提交比例指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取体型照片提交比例指标数据失败: " + e.getMessage());
            data.put("bodyTypePhotoSubmissionRate", "0.00");
        }
    }

    /**
     * 获取体型照片提交比例指标数据（传入日期参数）
     *
     * @param data 存储体型照片提交比例指标数据的Map对象
     * @param date 查询日期
     */
    private void getBodyTypePhotoSubmissionRateMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取体型照片提交比例指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDateTime
            LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // 计算指定月份的体型照片提交率及环比增长率
            Map<String, Object> result = userFirstFeedbackService.calculateBodyTypePhotoSubmissionRateWithGrowth(localDateTime);
            String currentRate = (String) result.get("currentRate");
            String growthRate = (String) result.get("growthRate");

            // 存储结果
            data.put("bodyTypePhotoSubmissionRate", currentRate);
            data.put("bodyTypePhotoSubmissionRateGrowth", growthRate);

            System.out.println("体型照片提交比例指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取体型照片提交比例指标数据失败: " + e.getMessage());
            data.put("bodyTypePhotoSubmissionRate", "0.00");
            data.put("bodyTypePhotoSubmissionRateGrowth", "0.00");
        }
    }

    /**
     * 获取餐食打卡完成率指标数据
     *
     * @param data 存储餐食打卡完成率指标数据的Map对象
     */
    private void getMealCheckinCompletionRateMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取餐食打卡完成率指标数据");

            // 餐食打卡完成率
            List<MealCheckinCompletionRate> rates = mealCheckinCompletionRateService.getMealCheckinCompletionRates();
            
            // 将三个时段的打卡完成率分别添加到返回结果中，统一格式为保留小数点后两位，不带%的百分比
            if (rates != null && !rates.isEmpty()) {
                data.put("tmp", rates);
                for (MealCheckinCompletionRate rate : rates) {
                    String rangeName = rate.getRangeName();
                    Double completionRate = rate.getCompletionRate();
                    
                    if ("前3天".equals(rangeName)) {
                        data.put("firstThreeDaysCompletionRate", String.format("%.2f", completionRate * 100));
                    } else if ("4～6天".equals(rangeName)) {
                        data.put("fourToSixDaysCompletionRate", String.format("%.2f", completionRate * 100));
                    } else if ("7～10天".equals(rangeName)) {
                        data.put("sevenToTenDaysCompletionRate", String.format("%.2f", completionRate * 100));
                    }
                }
            }

            System.out.println("餐食打卡完成率指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取餐食打卡完成率指标数据失败: " + e.getMessage());
            data.put("firstThreeDaysCompletionRate", "0.00");
            data.put("fourToSixDaysCompletionRate", "0.00");
            data.put("sevenToTenDaysCompletionRate", "0.00");
        }
    }
    
    /**
     * 获取餐食打卡完成率指标数据（传入日期参数）
     *
     * @param data 存储餐食打卡完成率指标数据的Map对象
     * @param date 查询日期
     */
    private void getMealCheckinCompletionRateMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取餐食打卡完成率指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDateTime
            LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // 计算指定月份的餐食打卡完成率及环比增长率
            Map<String, Object> result = mealCheckinCompletionRateService.calculateMealCheckinCompletionRateWithGrowth(localDateTime);
            
            // 将三个时段的打卡完成率分别添加到返回结果中，统一格式为保留小数点后两位，不带%的百分比
            for (Map.Entry<String, Object> entry : result.entrySet()) {
                String rangeName = entry.getKey();
                Map<String, Object> rangeData = (Map<String, Object>) entry.getValue();
                String currentRate = (String) rangeData.get("currentRate");
                String growthRate = (String) rangeData.get("growthRate");
                
                if ("前3天".equals(rangeName)) {
                    data.put("firstThreeDaysCompletionRate", currentRate);
                    data.put("firstThreeDaysCompletionRateGrowth", growthRate);
                } else if ("4～6天".equals(rangeName)) {
                    data.put("fourToSixDaysCompletionRate", currentRate);
                    data.put("fourToSixDaysCompletionRateGrowth", growthRate);
                } else if ("7～10天".equals(rangeName)) {
                    data.put("sevenToTenDaysCompletionRate", currentRate);
                    data.put("sevenToTenDaysCompletionRateGrowth", growthRate);
                }
            }

            System.out.println("餐食打卡完成率指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取餐食打卡完成率指标数据失败: " + e.getMessage());
            data.put("firstThreeDaysCompletionRate", "0.00");
            data.put("firstThreeDaysCompletionRateGrowth", "0.00");
            data.put("fourToSixDaysCompletionRate", "0.00");
            data.put("fourToSixDaysCompletionRateGrowth", "0.00");
            data.put("sevenToTenDaysCompletionRate", "0.00");
            data.put("sevenToTenDaysCompletionRateGrowth", "0.00");
        }
    }
    
    /**
     * 获取推单成交率指标数据
     *
     * @param data 存储推单成交率指标数据的Map对象
     */
    private void getPushOrderConversionRateMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取推单成交率指标数据");

            // 推单成交率
            double conversionRate = clientServiceStatsService.calculatePushOrderConversionRate();
            
            // 添加到返回结果中，统一格式为保留小数点后两位，不带%的百分比
            data.put("pushOrderConversionRate", String.format("%.2f", conversionRate * 100));

            System.out.println("推单成交率指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取推单成交率指标数据失败: " + e.getMessage());
            data.put("pushOrderConversionRate", String.format("%.2f", 0 * 100));
        }
    }
    
    /**
     * 获取推单成交率指标数据（传入日期参数）
     *
     * @param data 存储推单成交率指标数据的Map对象
     * @param date 查询日期
     */
    private void getPushOrderConversionRateMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取推单成交率指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDateTime
            LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // 计算指定月份的推单成交率及环比增长率
            Map<String, Object> result = clientServiceStatsService.calculatePushOrderConversionRateWithGrowth(localDateTime);
            String currentRate = (String) result.get("currentRate");
            String growthRate = (String) result.get("growthRate");

            // 存储结果
            data.put("pushOrderConversionRate", currentRate);
            data.put("pushOrderConversionRateGrowth", growthRate);

            System.out.println("推单成交率指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取推单成交率指标数据失败: " + e.getMessage());
            data.put("pushOrderConversionRate", String.format("%.2f", 0.0));
            data.put("pushOrderConversionRateGrowth", "0.00");
        }
    }
    
    /**
     * 获取推单后留存率指标数据
     *
     * @param data 存储推单后留存率指标数据的Map对象
     */
    private void getOrderRetentionRateMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取推单后留存率指标数据");

            // 推单后留存率
            OrderRetentionRate retentionRate = serverTimeService.calculateOrderRetentionRate();
            
            // 添加到返回结果中，统一格式为保留小数点后两位，不带%的百分比
//            Map<String, Object> retentionRateData = new HashMap<>();
//            retentionRateData.put("over10DaysUsers", retentionRate.getOver10DaysUsers());
//            retentionRateData.put("over13DaysUsers", retentionRate.getOver13DaysUsers());
//            retentionRateData.put("retentionRate", String.format("%.2f", retentionRate.getRetentionRate() * 100));
//            retentionRateData.put("percentage", String.format("%.2f", retentionRate.getRetentionRate() * 100));
            data.put("orderRetentionRate", String.format("%.2f", retentionRate.getRetentionRate() * 100));

            System.out.println("推单后留存率指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取推单后留存率指标数据失败: " + e.getMessage());
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("over10DaysUsers", 0L);
            errorData.put("over13DaysUsers", 0L);
            errorData.put("retentionRate", "0.00");
            errorData.put("percentage", "0.00");
            data.put("orderRetentionRate", errorData);
        }
    }
    
    /**
     * 获取推单后留存率指标数据（传入日期参数）
     *
     * @param data 存储推单后留存率指标数据的Map对象
     * @param date 查询日期
     */
    private void getOrderRetentionRateMetrics(Map<String, Object> data, Date date) {
        try {
            System.out.println("开始获取推单后留存率指标数据，日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 将Date转换为LocalDateTime
            LocalDateTime localDateTime = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

            // 计算指定月份的推单后留存率及环比增长率
            Map<String, Object> result = serverTimeService.calculateOrderRetentionRateWithGrowth(localDateTime);
            String currentRate = (String) result.get("currentRate");
            String growthRate = (String) result.get("growthRate");

            // 存储结果
            data.put("orderRetentionRate", currentRate);
            data.put("orderRetentionRateGrowth", growthRate);

            System.out.println("推单后留存率指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取推单后留存率指标数据失败: " + e.getMessage());
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("over10DaysUsers", 0L);
            errorData.put("over13DaysUsers", 0L);
            errorData.put("retentionRate", "0.00");
            errorData.put("percentage", "0.00");
            data.put("orderRetentionRate", errorData);
            data.put("orderRetentionRateGrowth", "0.00");
        }
    }
}