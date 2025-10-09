package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.service.UserMealCheckinService;
import org.panjy.servicemetricsplatform.service.MealCheckinRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 饮食三餐打卡率控制器
 * 提供计算用户饮食打卡率的API接口
 */
@RestController
@RequestMapping("/api/meal-checkin-rate")
public class MealCheckinRateController {

    @Autowired
    private UserMealCheckinService userMealCheckinService;
    
    @Autowired
    private MealCheckinRateService mealCheckinRateService;

    /**
     * 计算单个用户的饮食三餐打卡率
     * 打卡率 = 总实际打卡餐数 ÷ (总服务天数 × 3) × 100%
     *
     * @param wechatId 用户微信ID
     * @return 打卡率信息
     */
    @GetMapping("/user/{wechatId}")
    public Map<String, Object> calculateUserCheckinRate(@PathVariable String wechatId) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 获取指定用户的总实际打卡餐数
            int totalCheckinCount = userMealCheckinService.calculateUserTotalCheckinCount(wechatId);

            // 获取指定用户的总服务天数
            int totalServiceDays = userMealCheckinService.calculateTotalServiceDaysByWechatId(wechatId);

            // 计算打卡率
            double checkinRate = 0.0;
            if (totalServiceDays > 0) {
                checkinRate = (double) totalCheckinCount / (totalServiceDays * 3) * 100;
            }

            result.put("success", true);
            result.put("wechatId", wechatId);
            result.put("totalCheckinCount", totalCheckinCount);
            result.put("totalServiceDays", totalServiceDays);
            result.put("expectedTotalMeals", totalServiceDays * 3);
            result.put("checkinRate", String.format("%.2f%%", checkinRate));

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "计算打卡率时发生错误: " + e.getMessage());
        }

        return result;
    }

    /**
     * 计算所有用户的饮食三餐打卡率（通过数据库查询所有用户ID）
     * 打卡率 = 总实际打卡餐数 ÷ (总服务天数 × 3) × 100%
     * 示例: 客户A服务5天打卡12次，客户B服务3天打卡8次
     * 打卡率 = (12+8) / (3*5+3*3) * 100%
     *
     * @return 各用户打卡率信息及总体统计
     */
    @GetMapping("/user/all")
    public Map<String, Object> calculateBatchCheckinRate() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> usersData = new ArrayList<>();

        try {
            // 从数据库查询所有用户的微信ID
            List<String> allWechatIds = mealCheckinRateService.getAllWechatIds();
            
            int totalCheckinCountAllUsers = 0;
            int totalExpectedMealsAllUsers = 0;

            // 计算每个用户的打卡数据
            for (String wechatId : allWechatIds) {
                // 获取指定用户的总实际打卡餐数
                int userTotalCheckinCount = userMealCheckinService.calculateUserTotalCheckinCount(wechatId);

                // 获取指定用户的总服务天数
                int userServiceDays = userMealCheckinService.calculateTotalServiceDaysByWechatId(wechatId);

                // 计算该用户的打卡率
                double userCheckinRate = 0.0;
                int userExpectedMeals = userServiceDays * 3;
                if (userServiceDays > 0) {
                    userCheckinRate = (double) userTotalCheckinCount / userExpectedMeals * 100;
                }

                // 累计总数据
                totalCheckinCountAllUsers += userTotalCheckinCount;
                totalExpectedMealsAllUsers += userExpectedMeals;

                // 保存每个用户的数据
                Map<String, Object> userData = new HashMap<>();
                userData.put("wechatId", wechatId);
                userData.put("totalCheckinCount", userTotalCheckinCount);
                userData.put("serviceDays", userServiceDays);
                userData.put("expectedMeals", userExpectedMeals);
                userData.put("checkinRate", String.format("%.2f%%", userCheckinRate));
                usersData.add(userData);
            }

            // 计算总体打卡率
            double overallCheckinRate = 0.0;
            if (totalExpectedMealsAllUsers > 0) {
                overallCheckinRate = (double) totalCheckinCountAllUsers / totalExpectedMealsAllUsers * 100;
            }

            result.put("success", true);
            result.put("users", usersData);
            result.put("totalCheckinCount", totalCheckinCountAllUsers);
            result.put("totalExpectedMeals", totalExpectedMealsAllUsers);
            result.put("overallCheckinRate", String.format("%.2f%%", overallCheckinRate));

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "计算打卡率时发生错误: " + e.getMessage());
        }

        return result;
    }
    
    /**
     * 查询所有用户的微信ID并直接获取所有用户的三餐打卡率
     * 结合查询所有用户和计算打卡率的功能
     *
     * @return 所有用户的三餐打卡率信息
     */
    @GetMapping("/all")
    public Map<String, Object> calculateAllUsersCheckinRate() {
        return mealCheckinRateService.calculateAllUsersCheckinRate();
    }
}