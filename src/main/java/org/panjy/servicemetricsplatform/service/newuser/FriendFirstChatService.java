package org.panjy.servicemetricsplatform.service.newuser;

import org.panjy.servicemetricsplatform.mapper.newuser.FriendFirstChatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * FriendFirstChat服务类
 * 提供对用户首次聊天记录的业务逻辑处理
 */
@Service
public class FriendFirstChatService {
    
    @Autowired
    private FriendFirstChatMapper friendFirstChatMapper;
    
    /**
     * 查询指定日期当天所有记录中的微信ID
     * 
     * @param date 指定日期
     * @return 用户微信ID列表
     */
    public List<String> getFriendWechatIdsByDate(Date date) {
        // 将日期转换为开始时间和结束时间字符串
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String startDate = String.format("%04d-%02d-%02d", 
            cal.get(Calendar.YEAR), 
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH));
        String startTime = startDate + " 00:00:00";
        String endTime = startDate + " 23:59:59";
        
        // 调用Mapper查询指定时间范围内的用户微信ID列表
        return friendFirstChatMapper.selectFriendWechatIdsByTimeRange(startTime, endTime);
    }

    /**
     * 查询指定日期所在周的所有记录中的微信ID（自然周，周一到周日）
     * 
     * @param date 指定日期
     * @return 用户微信ID列表
     */
    public List<String> getFriendWechatIdsByWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        // 设置为本周的第一天（周一）
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String startDate = String.format("%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
        String startTime = startDate + " 00:00:00";
        
        // 设置为本周的最后一天（周日）
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String endDate = String.format("%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
        String endTime = endDate + " 23:59:59";

        // 调用Mapper查询指定时间范围内的用户微信ID列表
        return friendFirstChatMapper.selectFriendWechatIdsByTimeRange(startTime, endTime);
    }
    
    /**
     * 查询指定日期所在月的所有记录中的微信ID（自然月）
     * 
     * @param date 指定日期
     * @return 用户微信ID列表
     */
    public List<String> getFriendWechatIdsByMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        
        // 设置为本月第一天
        cal.set(Calendar.DAY_OF_MONTH, 1);
        String startDate = String.format("%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
        String startTime = startDate + " 00:00:00";
        
        // 设置为本月最后一天
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String endDate = String.format("%04d-%02d-%02d",
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH));
        String endTime = endDate + " 23:59:59";

        // 调用Mapper查询指定时间范围内的用户微信ID列表
        return friendFirstChatMapper.selectFriendWechatIdsByTimeRange(startTime, endTime);
    }

    /**
     * 计算新增用户数的日环比增长率
     *
     * @param currentDate 当前查询日期
     * @return 包含当前值、前一天值和增长率的统计数据
     */
    public Map<String, Object> calculateDailyNewUsersWithGrowth(Date currentDate) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 获取当前日期的新增用户数
            List<String> currentNewUsers = getFriendWechatIdsByDate(currentDate);
            int currentCount = currentNewUsers != null ? currentNewUsers.size() : 0;

            // 2. 计算前一天日期
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            Date previousDayDate = cal.getTime();

            // 3. 获取前一天新增用户数
            List<String> previousNewUsers = getFriendWechatIdsByDate(previousDayDate);
            int previousCount = previousNewUsers != null ? previousNewUsers.size() : 0;

            // 4. 计算增长率
            BigDecimal growthRate = calculateGrowthRate(
                    BigDecimal.valueOf(currentCount),
                    BigDecimal.valueOf(previousCount)
            );

            // 5. 构建返回结果
            result.put("currentValue", currentCount);
            result.put("previousDayValue", previousCount);
            result.put("growthRate", growthRate);
            result.put("currentDate", currentDate);
            result.put("previousDayDate", previousDayDate);

            System.out.println("新增用户日环比增长计算完成: 当前值=" + currentCount +
                    ", 前一天=" + previousCount + ", 增长率=" + growthRate + "%");

        } catch (Exception e) {
            System.err.println("计算新增用户日环比增长失败: " + e.getMessage());
            throw e;
        }

        return result;
    }

    /**
     * 计算周新增用户数的周环比增长率
     *
     * @param currentDate 当前查询日期（周内任意一天）
     * @return 包含当前周值、上周值和增长率的统计数据
     */
    public Map<String, Object> calculateWeeklyNewUsersWithGrowth(Date currentDate) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 获取当前周的新增用户数
            List<String> currentWeekNewUsers = getFriendWechatIdsByWeek(currentDate);
            int currentCount = currentWeekNewUsers != null ? currentWeekNewUsers.size() : 0;

            // 2. 计算上周日期（往前推一7天）
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            Date previousWeekDate = cal.getTime();

            // 3. 获取上周新增用户数
            List<String> previousWeekNewUsers = getFriendWechatIdsByWeek(previousWeekDate);
            int previousCount = previousWeekNewUsers != null ? previousWeekNewUsers.size() : 0;

            // 4. 计算增长率
            BigDecimal growthRate = calculateGrowthRate(
                    BigDecimal.valueOf(currentCount),
                    BigDecimal.valueOf(previousCount)
            );

            // 5. 构建返回结果
            result.put("currentValue", currentCount);
            result.put("previousWeekValue", previousCount);
            result.put("growthRate", growthRate);
            result.put("currentDate", currentDate);
            result.put("previousWeekDate", previousWeekDate);

            System.out.println("周新增用户环比增长计算完成: 当前周=" + currentCount +
                    ", 上周=" + previousCount + ", 增长率=" + growthRate + "%");

        } catch (Exception e) {
            System.err.println("计算周新增用户环比增长失败: " + e.getMessage());
            throw e;
        }

        return result;
    }

    /**
     * 计算月新增用户数的月环比增长率
     *
     * @param currentDate 当前查询日期（月内任意一天）
     * @return 包含当前月值、上月值和增长率的统计数据
     */
    public Map<String, Object> calculateMonthlyNewUsersWithGrowth(Date currentDate) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 获取当前月的新增用户数
            List<String> currentMonthNewUsers = getFriendWechatIdsByMonth(currentDate);
            int currentCount = currentMonthNewUsers != null ? currentMonthNewUsers.size() : 0;

            // 2. 计算上月日期（往前推一1个月）
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.MONTH, -1);
            Date previousMonthDate = cal.getTime();

            // 3. 获取上月新增用户数
            List<String> previousMonthNewUsers = getFriendWechatIdsByMonth(previousMonthDate);
            int previousCount = previousMonthNewUsers != null ? previousMonthNewUsers.size() : 0;

            // 4. 计算增长率
            BigDecimal growthRate = calculateGrowthRate(
                    BigDecimal.valueOf(currentCount),
                    BigDecimal.valueOf(previousCount)
            );

            // 5. 构建返回结果
            result.put("currentValue", currentCount);
            result.put("previousMonthValue", previousCount);
            result.put("growthRate", growthRate);
            result.put("currentDate", currentDate);
            result.put("previousMonthDate", previousMonthDate);

            System.out.println("月新增用户环比增长计算完成: 当前月=" + currentCount +
                    ", 上月=" + previousCount + ", 增长率=" + growthRate + "%");

        } catch (Exception e) {
            System.err.println("计算月新增用户环比增长失败: " + e.getMessage());
            throw e;
        }

        return result;
    }
    /**
     * 计算增长率的通用方法
     * 公式: (当前值 - 上期值) / 上期值 × 100%
     *
     * @param currentValue 当前值
     * @param previousValue 上期值
     * @return 增长率（百分比），保留2位小数
     */
    private BigDecimal calculateGrowthRate(BigDecimal currentValue, BigDecimal previousValue) {
        if (previousValue.compareTo(BigDecimal.ZERO) == 0) {
            // 如果上期值为0，无法计算增长率
            if (currentValue.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO; // 0 → 0，增长率为0%
            } else {
                return BigDecimal.valueOf(100.0); // 0 → 正数，增长率为100%
            }
        }

        // 正常计算: (current - previous) / previous * 100
        return currentValue.subtract(previousValue)
                .divide(previousValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }


}