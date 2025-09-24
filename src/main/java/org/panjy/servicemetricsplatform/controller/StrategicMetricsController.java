package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.service.StrategicLayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * 策略层指标控制器
 * 提供用户新增、活跃度、留存率、流失率、平均服务时间等核心业务指标的API
 */
@RestController
@RequestMapping("/api/strategic")
public class StrategicMetricsController {
    
    @Autowired
    private StrategicLayerService strategicLayerService;

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
     * 查询指定日期的新增用户
     *
     * @param date 查询日期 (格式: yyyy-MM-dd)
     * @return 新增用户列表和统计信息
     */
    @GetMapping("/new-users/daily/{date}")
    public ResponseEntity<?> getNewUsersByDay(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        
        try {
            if (date == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("日期参数不能为空", "INVALID_DATE")
                );
            }
            
            System.out.println("查询日期新增用户: " + new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            List<String> newUsers = strategicLayerService.findNewUserByDay(date);
            
            Map<String, Object> data = new HashMap<>();
            data.put("date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            data.put("newUsers", newUsers);
            data.put("count", newUsers != null ? newUsers.size() : 0);
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", data));
            
        } catch (Exception e) {
            System.err.println("查询日新增用户失败: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("查询失败: " + e.getMessage(), "QUERY_ERROR")
            );
        }
    }

    /**
     * 查询指定周的新增用户
     *
     * @param date 周内任意一天 (格式: yyyy-MM-dd)
     * @return 新增用户列表和统计信息
     */
    @GetMapping("/new-users/weekly/{date}")
    public ResponseEntity<?> getNewUsersByWeek(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        
        try {
            if (date == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("日期参数不能为空", "INVALID_DATE")
                );
            }
            
            System.out.println("查询周新增用户: " + new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            List<String> newUsers = strategicLayerService.findNewUserByWeek(date);
            
            Map<String, Object> data = new HashMap<>();
            data.put("weekDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
            data.put("newUsers", newUsers);
            data.put("count", newUsers != null ? newUsers.size() : 0);
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", data));
            
        } catch (Exception e) {
            System.err.println("查询周新增用户失败: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("查询失败: " + e.getMessage(), "QUERY_ERROR")
            );
        }
    }

    /**
     * 查询指定月份的新增用户
     *
     * @param date 月份第一天 (格式: yyyy-MM-dd)
     * @return 新增用户列表和统计信息
     */
    @GetMapping("/new-users/monthly/{date}")
    public ResponseEntity<?> getNewUsersByMonth(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        
        try {
            if (date == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("日期参数不能为空", "INVALID_DATE")
                );
            }
            
            System.out.println("查询月新增用户: " + new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            List<String> newUsers = strategicLayerService.findNewUserByMonth(date);
            
            Map<String, Object> data = new HashMap<>();
            data.put("monthDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
            data.put("newUsers", newUsers);
            data.put("count", newUsers != null ? newUsers.size() : 0);
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", data));
            
        } catch (Exception e) {
            System.err.println("查询月新增用户失败: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("查询失败: " + e.getMessage(), "QUERY_ERROR")
            );
        }
    }

    /**
     * 查询指定日期的新增用户（包含日环比增长率）
     *
     * @param date 指定日期 (格式: yyyy-MM-dd)
     * @return 日新增用户统计信息和日环比增长率
     */
    @GetMapping("/new-users/daily-growth/{date}")
    public ResponseEntity<?> getNewUsersByDayWithGrowth(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        
        try {
            if (date == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("日期参数不能为空", "INVALID_DATE")
                );
            }
            
            System.out.println("查询日新增用户（含日环比增长）: " + new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            // 添加详细的日志记录
            System.out.println("日新增用户计算详细日志:");
            System.out.println("  1. 输入参数检查通过，开始计算");
            
            try {
                // 获取包含日环比增长率的统计数据
                System.out.println("  2. 调用strategicLayerService.calculateDailyNewUsersWithGrowth方法");
                Map<String, Object> growthStats = strategicLayerService.calculateDailyNewUsersWithGrowth(date);
                System.out.println("  3. calculateDailyNewUsersWithGrowth方法返回结果: " + growthStats);
                
                Map<String, Object> data = new HashMap<>();
                data.put("date", new SimpleDateFormat("yyyy-MM-dd").format(date));
                data.put("currentDayCount", growthStats.get("currentValue"));
                data.put("previousDayCount", growthStats.get("previousDayValue"));
                data.put("growthRate", growthStats.get("growthRate"));
                data.put("growthRatePercent", growthStats.get("growthRate") + "%");
                data.put("previousDayDate", new SimpleDateFormat("yyyy-MM-dd").format((Date) growthStats.get("previousDayDate")));
                
                System.out.println("日新增用户计算完成: 当前值=" + growthStats.get("currentValue") + 
                                 ", 前一天值=" + growthStats.get("previousDayValue") + 
                                 ", 增长率=" + growthStats.get("growthRate") + "%");
                
                return ResponseEntity.ok(createSuccessResponse("查询成功", data));
                
            } catch (Exception serviceException) {
                System.err.println("  错误: 在strategicLayerService.calculateDailyNewUsersWithGrowth方法中发生异常");
                System.err.println("  异常类型: " + serviceException.getClass().getName());
                System.err.println("  异常信息: " + serviceException.getMessage());
                serviceException.printStackTrace();
                throw serviceException; // 重新抛出异常以被外层catch捕获
            }
            
        } catch (Exception e) {
            System.err.println("查询日新增用户同比增长失败: " + e.getMessage());
            System.err.println("异常堆栈跟踪:");
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("查询失败: " + e.getMessage(), "QUERY_ERROR")
            );
        }
    }

    /**
     * 查询指定周的新增用户（包含周环比增长率）
     *
     * @param date 周内任意一天 (格式: yyyy-MM-dd)
     * @return 周新增用户统计信息和周环比增长率
     */
    @GetMapping("/new-users/weekly-growth/{date}")
    public ResponseEntity<?> getNewUsersByWeekWithGrowth(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        
        try {
            if (date == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("日期参数不能为空", "INVALID_DATE")
                );
            }
            
            System.out.println("查询周新增用户（含周环比增长）: " + new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            // 添加详细的日志记录
            System.out.println("周新增用户计算详细日志:");
            System.out.println("  1. 输入参数检查通过，开始计算");
            
            try {
                // 获取包含周环比增长率的统计数据
                System.out.println("  2. 调用strategicLayerService.calculateWeeklyNewUsersWithGrowth方法");
                Map<String, Object> growthStats = strategicLayerService.calculateWeeklyNewUsersWithGrowth(date);
                System.out.println("  3. calculateWeeklyNewUsersWithGrowth方法返回结果: " + growthStats);
                
                Map<String, Object> data = new HashMap<>();
                data.put("weekDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
                data.put("currentWeekCount", growthStats.get("currentValue"));
                data.put("previousWeekCount", growthStats.get("previousWeekValue"));
                data.put("growthRate", growthStats.get("growthRate"));
                data.put("growthRatePercent", growthStats.get("growthRate") + "%");
                data.put("previousWeekDate", new SimpleDateFormat("yyyy-MM-dd").format((Date) growthStats.get("previousWeekDate")));
                
                System.out.println("周新增用户计算完成: 当前值=" + growthStats.get("currentValue") + 
                                 ", 上周值=" + growthStats.get("previousWeekValue") + 
                                 ", 增长率=" + growthStats.get("growthRate") + "%");
                
                return ResponseEntity.ok(createSuccessResponse("查询成功", data));
                
            } catch (Exception serviceException) {
                System.err.println("  错误: 在strategicLayerService.calculateWeeklyNewUsersWithGrowth方法中发生异常");
                System.err.println("  异常类型: " + serviceException.getClass().getName());
                System.err.println("  异常信息: " + serviceException.getMessage());
                serviceException.printStackTrace();
                throw serviceException; // 重新抛出异常以被外层catch捕获
            }
            
        } catch (Exception e) {
            System.err.println("查询周新增用户周环比增长失败: " + e.getMessage());
            System.err.println("异常堆栈跟踪:");
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("查询失败: " + e.getMessage(), "QUERY_ERROR")
            );
        }
    }

    /**
     * 查询指定月的新增用户（包含月环比增长率）
     *
     * @param date 月内任意一天 (格式: yyyy-MM-dd)
     * @return 月新增用户统计信息和月环比增长率
     */
    @GetMapping("/new-users/monthly-growth/{date}")
    public ResponseEntity<?> getNewUsersByMonthWithGrowth(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        
        try {
            if (date == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("日期参数不能为空", "INVALID_DATE")
                );
            }
            
            System.out.println("查询月新增用户（含月环比增长）: " + new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            // 添加详细的日志记录
            System.out.println("月新增用户计算详细日志:");
            System.out.println("  1. 输入参数检查通过，开始计算");
            
            try {
                // 获取包含月环比增长率的统计数据
                System.out.println("  2. 调用strategicLayerService.calculateMonthlyNewUsersWithGrowth方法");
                Map<String, Object> growthStats = strategicLayerService.calculateMonthlyNewUsersWithGrowth(date);
                System.out.println("  3. calculateMonthlyNewUsersWithGrowth方法返回结果: " + growthStats);
                
                Map<String, Object> data = new HashMap<>();
                data.put("monthDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
                data.put("currentMonthCount", growthStats.get("currentValue"));
                data.put("previousMonthCount", growthStats.get("previousMonthValue"));
                data.put("growthRate", growthStats.get("growthRate"));
                data.put("growthRatePercent", growthStats.get("growthRate") + "%");
                data.put("previousMonthDate", new SimpleDateFormat("yyyy-MM-dd").format((Date) growthStats.get("previousMonthDate")));
                
                System.out.println("月新增用户计算完成: 当前值=" + growthStats.get("currentValue") + 
                                 ", 上月值=" + growthStats.get("previousMonthValue") + 
                                 ", 增长率=" + growthStats.get("growthRate") + "%");
                
                return ResponseEntity.ok(createSuccessResponse("查询成功", data));
                
            } catch (Exception serviceException) {
                System.err.println("  错误: 在strategicLayerService.calculateMonthlyNewUsersWithGrowth方法中发生异常");
                System.err.println("  异常类型: " + serviceException.getClass().getName());
                System.err.println("  异常信息: " + serviceException.getMessage());
                serviceException.printStackTrace();
                throw serviceException; // 重新抛出异常以被外层catch捕获
            }
            
        } catch (Exception e) {
            System.err.println("查询月新增用户月环比增长失败: " + e.getMessage());
            System.err.println("异常堆栈跟踪:");
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("查询失败: " + e.getMessage(), "QUERY_ERROR")
            );
        }
    }

    /**
     * 查询指定时间的活跃用户数（包含同比增长率）
     *
     * @param currentTime 当前时间 (格式: yyyy-MM-dd)
     * @return 活跃用户数统计和同比增长率
     */
    @GetMapping("/active-users-growth/{currentTime}")
    public ResponseEntity<?> getActiveUserCountWithGrowth(@PathVariable("currentTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date currentTime) {
        
        try {
            if (currentTime == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("时间参数不能为空", "INVALID_TIME")
                );
            }
            
            System.out.println("查询活跃用户数（含同比增长）: " + new SimpleDateFormat("yyyy-MM-dd").format(currentTime));
            
            // 添加详细的日志记录
            System.out.println("活跃用户数计算详细日志:");
            System.out.println("  1. 输入参数检查通过，开始计算");
            
            try {
                // 获取包含同比增长率的统计数据
                System.out.println("  2. 调用strategicLayerService.calculateActiveUsersWithGrowth方法");
                Map<String, Object> growthStats = strategicLayerService.calculateActiveUsersWithGrowth(currentTime);
                System.out.println("  3. calculateActiveUsersWithGrowth方法返回结果: " + growthStats);
                
                Map<String, Object> data = new HashMap<>();
                data.put("currentTime", new SimpleDateFormat("yyyy-MM-dd").format(currentTime));
                data.put("currentActiveUserCount", growthStats.get("currentValue"));
                data.put("previousYearActiveUserCount", growthStats.get("previousYearValue"));
                data.put("growthRate", growthStats.get("growthRate"));
                data.put("growthRatePercent", growthStats.get("growthRate") + "%");
                data.put("previousYearDate", new SimpleDateFormat("yyyy-MM-dd").format((Date) growthStats.get("previousYearDate")));
                
                System.out.println("活跃用户数计算完成: 当前值=" + growthStats.get("currentValue") + 
                                 ", 上年同期值=" + growthStats.get("previousYearValue") + 
                                 ", 增长率=" + growthStats.get("growthRate") + "%");
                
                return ResponseEntity.ok(createSuccessResponse("查询成功", data));
                
            } catch (Exception serviceException) {
                System.err.println("  错误: 在strategicLayerService.calculateActiveUsersWithGrowth方法中发生异常");
                System.err.println("  异常类型: " + serviceException.getClass().getName());
                System.err.println("  异常信息: " + serviceException.getMessage());
                serviceException.printStackTrace();
                throw serviceException; // 重新抛出异常以被外层catch捕获
            }
            
        } catch (Exception e) {
            System.err.println("查询活跃用户数同比增长失败: " + e.getMessage());
            System.err.println("异常堆栈跟踪:");
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("查询失败: " + e.getMessage(), "QUERY_ERROR")
            );
        }
    }



    /**
     * 计算用户流失率
     *
     * @param currentTime 指定日期 (格式: yyyy-MM-dd)
     * @return 流失率统计
     */
    @GetMapping("/churn-rate/{currentTime}")
    public ResponseEntity<?> getChurnRate(@PathVariable("currentTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date currentTime) {
        try {
            if (currentTime == null) {
                System.err.println("流失率计算失败: 时间参数为空");
                return ResponseEntity.badRequest().body(
                    createErrorResponse("时间参数不能为空", "INVALID_TIME")
                );
            }
            
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
            System.out.println("开始计算流失率: 日期=" + dateStr);
            
            // 添加详细的日志记录
            System.out.println("流失率计算详细日志:");
            System.out.println("  1. 输入参数检查通过，开始计算");
            
            try {
                // 使用BigDecimal进行精确计算
                System.out.println("  2. 调用strategicLayerService.getChurnRate方法");
                double churnRateDouble = strategicLayerService.getChurnRate(currentTime);
                System.out.println("  3. getChurnRate方法返回原始值: " + churnRateDouble);
                
                BigDecimal churnRate = BigDecimal.valueOf(churnRateDouble)
                        .setScale(2, RoundingMode.HALF_UP);
                System.out.println("  4. 转换为BigDecimal并四舍五入: " + churnRate);
                
                System.out.println("流失率计算完成: 日期=" + dateStr + ", 流失率=" + churnRate + "%");
                
                Map<String, Object> data = new HashMap<>();
                data.put("currentTime", dateStr);
                data.put("churnRate", churnRate);
                data.put("churnRatePercent", churnRate + "%");
                
                return ResponseEntity.ok(createSuccessResponse("计算成功", data));
                
            } catch (Exception serviceException) {
                System.err.println("  错误: 在strategicLayerService.getChurnRate方法中发生异常");
                System.err.println("  异常类型: " + serviceException.getClass().getName());
                System.err.println("  异常信息: " + serviceException.getMessage());
                serviceException.printStackTrace();
                throw serviceException; // 重新抛出异常以被外层catch捕获
            }
            
        } catch (Exception e) {
            String dateStr = currentTime != null ? new SimpleDateFormat("yyyy-MM-dd").format(currentTime) : "null";
            System.err.println("计算流失率失败: 日期=" + dateStr + ", 错误信息=" + e.getMessage());
            System.err.println("异常堆栈跟踪:");
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("计算失败: " + e.getMessage(), "CALCULATION_ERROR")
            );
        }
    }



    /**
     * 计算平均服务时间（包含同比增长率）
     *
     * @param checkTime 检查日期 (格式: yyyy-MM-dd)
     * @return 平均服务时间统计和同比增长率
     */
    @GetMapping("/average-service-time-growth/{checkTime}")
    public ResponseEntity<?> getAverageServiceTimeWithGrowth(@PathVariable("checkTime") @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkTime) {
        
        try {
            if (checkTime == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("时间参数不能为空", "INVALID_TIME")
                );
            }
            
            System.out.println("计算平均服务时间同比增长: " + new SimpleDateFormat("yyyy-MM-dd").format(checkTime));
            
            // 获取包含同比增长率的统计数据
            Map<String, Object> growthStats = strategicLayerService.calculateAverageServiceTimeWithGrowth(checkTime);
            
            BigDecimal currentTime = (BigDecimal) growthStats.get("currentValue");
            BigDecimal previousTime = (BigDecimal) growthStats.get("previousYearValue");
            
            Map<String, Object> data = new HashMap<>();
            data.put("checkTime", new SimpleDateFormat("yyyy-MM-dd").format(checkTime));
            data.put("currentAverageServiceTimeDays", currentTime);
            data.put("previousYearAverageServiceTimeDays", previousTime);
            data.put("currentAverageServiceTimeFormatted", formatServiceTime(currentTime.doubleValue()));
            data.put("previousYearAverageServiceTimeFormatted", formatServiceTime(previousTime.doubleValue()));
            data.put("growthRate", growthStats.get("growthRate"));
            data.put("growthRatePercent", growthStats.get("growthRate") + "%");
            data.put("previousYearDate", new SimpleDateFormat("yyyy-MM-dd").format((Date) growthStats.get("previousYearDate")));
            
            return ResponseEntity.ok(createSuccessResponse("计算成功", data));
            
        } catch (Exception e) {
            System.err.println("计算平均服务时间同比增长失败: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("计算失败: " + e.getMessage(), "CALCULATION_ERROR")
            );
        }
    }

    /**
     * 获取综合指标概览（包含同比增长率）
     *
     * @param date 查询日期 (格式: yyyy-MM-dd)
     * @return 包含多个指标的综合概览和同比增长率
     */
    @GetMapping("/overview-growth/{date}")
    public ResponseEntity<?> getMetricsOverviewWithGrowth(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        
        try {
            if (date == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("日期参数不能为空", "INVALID_DATE")
                );
            }
            
            System.out.println("获取综合指标概览（含同比增长）: " + new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            // 获取各项指标的同比增长统计
            Map<String, Object> newUsersGrowth = strategicLayerService.calculateDailyNewUsersWithGrowth(date);
            Map<String, Object> activeUsersGrowth = strategicLayerService.calculateActiveUsersWithGrowth(date);
            Map<String, Object> serviceTimeGrowth = strategicLayerService.calculateAverageServiceTimeWithGrowth(date);
            
            // 构建综合概览数据
            Map<String, Object> overview = new HashMap<>();
            overview.put("date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            // 新增用户
            overview.put("newUsersCount", newUsersGrowth.get("currentValue"));
            overview.put("newUsersGrowthRate", newUsersGrowth.get("growthRate"));
            overview.put("previousDayNewUsersCount", newUsersGrowth.get("previousDayValue"));
            
            // 活跃用户
            overview.put("activeUserCount", activeUsersGrowth.get("currentValue"));
            overview.put("activeUsersGrowthRate", activeUsersGrowth.get("growthRate"));
            overview.put("previousYearActiveUserCount", activeUsersGrowth.get("previousYearValue"));
            
            // 平均服务时间
            BigDecimal currentServiceTime = (BigDecimal) serviceTimeGrowth.get("currentValue");
            BigDecimal previousServiceTime = (BigDecimal) serviceTimeGrowth.get("previousYearValue");
            overview.put("averageServiceTimeDays", currentServiceTime);
            overview.put("averageServiceTimeFormatted", formatServiceTime(currentServiceTime != null ? currentServiceTime.doubleValue() : 0.0));
            overview.put("serviceTimeGrowthRate", serviceTimeGrowth.get("growthRate"));
            overview.put("previousYearAverageServiceTimeDays", previousServiceTime);
            overview.put("previousYearAverageServiceTimeFormatted", formatServiceTime(previousServiceTime != null ? previousServiceTime.doubleValue() : 0.0));
            
            // 安全地处理日期字段
            handleDateField(overview, newUsersGrowth, "previousYearDate", "previousYearDate");
            handleDateField(overview, newUsersGrowth, "previousDayDate", "previousDayDate");
            
            return ResponseEntity.ok(createSuccessResponse("获取概览成功", overview));
            
        } catch (Exception e) {
            System.err.println("获取综合指标概览同比增长失败: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                createErrorResponse("获取概览失败: " + e.getMessage(), "OVERVIEW_ERROR")
            );
        }
    }
    
    /**
     * 安全地处理日期字段
     * @param targetMap 目标Map
     * @param sourceMap 源Map
     * @param sourceKey 源键名
     * @param targetKey 目标键名
     */
    private void handleDateField(Map<String, Object> targetMap, Map<String, Object> sourceMap, String sourceKey, String targetKey) {
        Object dateObj = sourceMap.get(sourceKey);
        if (dateObj instanceof Date) {
            targetMap.put(targetKey, new SimpleDateFormat("yyyy-MM-dd").format((Date) dateObj));
        } else {
            targetMap.put(targetKey, null);
        }
    }
    
    /**
     * 格式化服务时间显示
     *
     * @param days 天数
     * @return 格式化的时间字符串
     */
    private String formatServiceTime(double days) {
        if (days < 1.0) {
            double hours = days * 24;
            if (hours < 1.0) {
                int minutes = (int) Math.round(hours * 60);
                return minutes + "分钟";
            } else {
                int wholeHours = (int) hours;
                int minutes = (int) Math.round((hours - wholeHours) * 60);
                if (minutes == 0) {
                    return wholeHours + "小时";
                } else {
                    return wholeHours + "小时" + minutes + "分钟";
                }
            }
        } else {
            int wholeDays = (int) days;
            double remainingHours = (days - wholeDays) * 24;
            int hours = (int) Math.round(remainingHours);
            if (hours == 0) {
                return wholeDays + "天";
            } else {
                return wholeDays + "天" + hours + "小时";
            }
        }
    }
}