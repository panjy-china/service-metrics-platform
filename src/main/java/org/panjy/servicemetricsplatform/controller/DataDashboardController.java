package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.CallDurationStatistics;
import org.panjy.servicemetricsplatform.entity.MealCheckinCompletionRate;
import org.panjy.servicemetricsplatform.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

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

            // 1. 获取首电相关指标数据
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
     * 获取首电相关指标数据
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
     * 获取用户基础资料提交相关指标数据
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
     * 获取用户三餐打卡相关指标数据
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
     * 获取饮食指导相关指标数据
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
     * 获取个性化中医指导完成率指标数据
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
     * 获取推单后留存率指标数据
     *
     * @param data 存储推单后留存率指标数据的Map对象
     */
    private void getOrderRetentionRateMetrics(Map<String, Object> data) {
        try {
            System.out.println("开始获取推单后留存率指标数据");

            // 推单后留存率
            org.panjy.servicemetricsplatform.entity.OrderRetentionRate retentionRate = serverTimeService.calculateOrderRetentionRate();
            
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
}