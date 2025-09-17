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
     * 查询指定日期的新增用户（包含同比增长率）
     *
     * @param date 查询日期 (格式: yyyy-MM-dd)
     * @return 新增用户统计信息和同比增长率
     */
    @GetMapping("/new-users/daily-growth/{date}")
    public ResponseEntity<?> getNewUsersByDayWithGrowth(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        
        try {
            if (date == null) {
                return ResponseEntity.badRequest().body(
                    createErrorResponse("日期参数不能为空", "INVALID_DATE")
                );
            }
            
            System.out.println("查询日期新增用户（含同比增长）: " + new SimpleDateFormat("yyyy-MM-dd").format(date));
            
            // 获取包含同比增长率的统计数据
            Map<String, Object> growthStats = strategicLayerService.calculateDailyNewUsersWithGrowth(date);
            
            Map<String, Object> data = new HashMap<>();
            data.put("date", new SimpleDateFormat("yyyy-MM-dd").format(date));
            data.put("currentCount", growthStats.get("currentValue"));
            data.put("previousDayCount", growthStats.get("previousDayValue"));
            data.put("growthRate", growthStats.get("growthRate"));
            data.put("growthRatePercent", growthStats.get("growthRate") + "%");
            data.put("previousDayDate", new SimpleDateFormat("yyyy-MM-dd").format((Date) growthStats.get("previousDayDate")));
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", data));
            
        } catch (Exception e) {
            System.err.println("查询日新增用户同比增长失败: " + e.getMessage());
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
            
            // 获取包含周环比增长率的统计数据
            Map<String, Object> growthStats = strategicLayerService.calculateWeeklyNewUsersWithGrowth(date);
            
            Map<String, Object> data = new HashMap<>();
            data.put("weekDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
            data.put("currentWeekCount", growthStats.get("currentValue"));
            data.put("previousWeekCount", growthStats.get("previousWeekValue"));
            data.put("growthRate", growthStats.get("growthRate"));
            data.put("growthRatePercent", growthStats.get("growthRate") + "%");
            data.put("previousWeekDate", new SimpleDateFormat("yyyy-MM-dd").format((Date) growthStats.get("previousWeekDate")));
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", data));
            
        } catch (Exception e) {
            System.err.println("查询周新增用户周环比增长失败: " + e.getMessage());
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
            
            // 获取包含月环比增长率的统计数据
            Map<String, Object> growthStats = strategicLayerService.calculateMonthlyNewUsersWithGrowth(date);
            
            Map<String, Object> data = new HashMap<>();
            data.put("monthDate", new SimpleDateFormat("yyyy-MM-dd").format(date));
            data.put("currentMonthCount", growthStats.get("currentValue"));
            data.put("previousMonthCount", growthStats.get("previousMonthValue"));
            data.put("growthRate", growthStats.get("growthRate"));
            data.put("growthRatePercent", growthStats.get("growthRate") + "%");
            data.put("previousMonthDate", new SimpleDateFormat("yyyy-MM-dd").format((Date) growthStats.get("previousMonthDate")));
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", data));
            
        } catch (Exception e) {
            System.err.println("查询月新增用户月环比增长失败: " + e.getMessage());
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
            
            // 获取包含同比增长率的统计数据
            Map<String, Object> growthStats = strategicLayerService.calculateActiveUsersWithGrowth(currentTime);
            
            Map<String, Object> data = new HashMap<>();
            data.put("currentTime", new SimpleDateFormat("yyyy-MM-dd").format(currentTime));
            data.put("currentActiveUserCount", growthStats.get("currentValue"));
            data.put("previousYearActiveUserCount", growthStats.get("previousYearValue"));
            data.put("growthRate", growthStats.get("growthRate"));
            data.put("growthRatePercent", growthStats.get("growthRate") + "%");
            data.put("previousYearDate", new SimpleDateFormat("yyyy-MM-dd").format((Date) growthStats.get("previousYearDate")));
            
            return ResponseEntity.ok(createSuccessResponse("查询成功", data));
            
        } catch (Exception e) {
            System.err.println("查询活跃用户数同比增长失败: " + e.getMessage());
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
    public ResponseEntity<?> getChurnRate(
            @PathVariable("currentTime") 
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date currentTime) {
        
        try {
            if (currentTime == null) {
                System.err.println("流失率计算失败: 时间参数为空");
                return ResponseEntity.badRequest().body(
                    createErrorResponse("时间参数不能为空", "INVALID_TIME")
                );
            }
            
            String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(currentTime);
            System.out.println("开始计算流失率: 日期=" + dateStr);
            
            // 使用BigDecimal进行精确计算
            double churnRateDouble = strategicLayerService.getChurnRate(currentTime);
            BigDecimal churnRate = BigDecimal.valueOf(churnRateDouble)
                    .setScale(2, RoundingMode.HALF_UP);
            
            System.out.println("流失率计算完成: 日期=" + dateStr + ", 流失率=" + churnRate + "%");
            
            Map<String, Object> data = new HashMap<>();
            data.put("currentTime", dateStr);
            data.put("churnRate", churnRate);
            data.put("churnRatePercent", churnRate + "%");
            
            return ResponseEntity.ok(createSuccessResponse("计算成功", data));
            
        } catch (Exception e) {
            String dateStr = currentTime != null ? new SimpleDateFormat("yyyy-MM-dd").format(currentTime) : "null";
            System.err.println("计算流失率失败: 日期=" + dateStr + ", 错误信息=" + e.getMessage());
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
            overview.put("averageServiceTimeFormatted", formatServiceTime(currentServiceTime.doubleValue()));
            overview.put("serviceTimeGrowthRate", serviceTimeGrowth.get("growthRate"));
            overview.put("previousYearAverageServiceTimeDays", previousServiceTime);
            overview.put("previousYearAverageServiceTimeFormatted", formatServiceTime(previousServiceTime.doubleValue()));
            
            // 上年同期日期
            overview.put("previousYearDate", new SimpleDateFormat("yyyy-MM-dd").format((Date) newUsersGrowth.get("previousYearDate")));
            // 前一天日期
            overview.put("previousDayDate", new SimpleDateFormat("yyyy-MM-dd").format((Date) newUsersGrowth.get("previousDayDate")));
            
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