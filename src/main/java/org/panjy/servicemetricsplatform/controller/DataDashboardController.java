package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.CallDurationStatistics;
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
     * 获取数据看板所有指标数据总接口
     * 整合所有数据看板的指标数据
     *
     * @return 数据看板所有指标数据
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getAllDataDashboardMetrics() {
        try {
            System.out.println("开始获取数据看板所有指标数据");

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

            System.out.println("数据看板所有指标数据获取完成");
            return ResponseEntity.ok(createSuccessResponse("查询成功", dashboardData));

        } catch (Exception e) {
            System.err.println("获取数据看板所有指标数据失败: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(500).body(
                createErrorResponse("查询失败: " + e.getMessage(), "QUERY_ERROR")
            );
        }
    }

    /**
     * 获取数据看板所有指标数据总接口（包含通话时长统计）
     * 整合所有数据看板的指标数据，包括通话时长统计
     *
     * @return 数据看板所有指标数据
     */
    @GetMapping("/metrics_2")
    public ResponseEntity<Map<String, Object>> getAllDataDashboardMetricsWithCallDuration() {
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
            Map<String, Object> averageCallDurationMap = new HashMap<>();
            averageCallDurationMap.put("averageCallDuration", averageCallDuration);
            averageCallDurationMap.put("unit", "seconds");
            data.put("firstCallAverageDuration", averageCallDurationMap);

            System.out.println("首电相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取首电相关指标数据失败: " + e.getMessage());
            data.put("firstCallAverageDuration", new HashMap<>());
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
            data.put("userBasicInfoSubmissionRate", basicInfoRate);

            // 基础资料提交统计详情
            long[] basicInfoStats = userFirstFeedbackService.getBasicInfoSubmissionStats();
            Map<String, Object> statsData = new HashMap<>();
            statsData.put("feedbackNums", basicInfoStats[0]);
            statsData.put("totalRecords", basicInfoStats[1]);
            data.put("userBasicInfoSubmissionStats", statsData);

            System.out.println("用户基础资料提交相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取用户基础资料提交相关指标数据失败: " + e.getMessage());
            data.put("userBasicInfoSubmissionRate", "0.00%");
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
            data.put("allUsersMealCheckinRate", allUsersMealCheckinRate);

            // 体重反馈完成率
            String weightFeedbackCompletionRate = userMealCheckinService.calculateWeightFeedbackCompletionRate();
            data.put("weightFeedbackCompletionRate", weightFeedbackCompletionRate);

            System.out.println("用户三餐打卡相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取用户三餐打卡相关指标数据失败: " + e.getMessage());
            data.put("allUsersMealCheckinRate", "0.00%");
            data.put("weightFeedbackCompletionRate", "0.00%");
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
            data.put("dietaryGuidanceReachRate", guidanceReachRate);

            System.out.println("饮食指导相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取饮食指导相关指标数据失败: " + e.getMessage());
            data.put("dietaryGuidanceReachRate", BigDecimal.ZERO);
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
            data.put("fourCallComplianceRate", fourCallRate);

            // 六次通话达标率
            double sixCallRate = callCountComplianceRateService.calculateSixCallComplianceRate();
            data.put("sixCallComplianceRate", sixCallRate);

            // 通话次数统计详情
            long[] callCountStats = callCountComplianceRateService.getCallCountStatistics();
            Map<String, Object> statsData = new HashMap<>();
            statsData.put("totalCount", callCountStats[0]);
            statsData.put("fourCallCompliantCount", callCountStats[1]);
            statsData.put("sixCallCompliantCount", callCountStats[2]);
            statsData.put("fourCallComplianceRateDetail", callCountStats[0] > 0 ? (double) callCountStats[1] / callCountStats[0] * 100 : 0.0);
            statsData.put("sixCallComplianceRateDetail", callCountStats[0] > 0 ? (double) callCountStats[2] / callCountStats[0] * 100 : 0.0);
            data.put("callCountStatistics", statsData);

            System.out.println("通话次数相关指标数据获取完成");
        } catch (Exception e) {
            System.err.println("获取通话次数相关指标数据失败: " + e.getMessage());
            data.put("fourCallComplianceRate", 0.0);
            data.put("sixCallComplianceRate", 0.0);
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
            data.put("firstCallAverageCompletionTime", averageCallCompletionTime);

            // 首电完成时间差统计信息
            OrderCallTimeDiffService.TimeDiffStatistics timeDiffStats = orderCallTimeDiffService.getStatistics();
            Map<String, Object> statsData = new HashMap<>();
            statsData.put("totalCount", timeDiffStats.getTotalCount());
            statsData.put("filteredCount", timeDiffStats.getFilteredCount());
            statsData.put("averageTimeInDays", timeDiffStats.getAverageTime());
            data.put("firstCallTimeDiffStatistics", statsData);

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
}