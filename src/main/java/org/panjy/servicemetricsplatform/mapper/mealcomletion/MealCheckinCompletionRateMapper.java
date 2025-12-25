package org.panjy.servicemetricsplatform.mapper.mealcomletion;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.panjy.servicemetricsplatform.entity.mealcomletion.MealCheckinCompletionRate;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MealCheckinCompletionRateMapper {
    
    /**
     * 查询不同时间段的餐食打卡完成率
     * 
     * @return 餐食打卡完成率列表
     */
    @Select({
        "WITH (SELECT min(checkin_date) FROM aikang.user_meal_checkin) AS min_date",
        "SELECT",
        "    range_name AS rangeName,",
        "    round(",
        "        (",
        "            sum(ifNull(breakfast_checked, 0))",
        "            + sum(ifNull(lunch_checked, 0))",
        "            + sum(ifNull(dinner_checked, 0))",
        "        ) / (count() * 3),",
        "        4",
        "    ) AS completionRate",
        "FROM",
        "(",
        "    SELECT",
        "        checkin_date,",
        "        breakfast_checked,",
        "        lunch_checked,",
        "        dinner_checked,",
        "        CASE",
        "            WHEN checkin_date BETWEEN min_date AND addDays(min_date, 2) THEN '前3天'",
        "            WHEN checkin_date BETWEEN addDays(min_date, 3) AND addDays(min_date, 5) THEN '4～6天'",
        "            WHEN checkin_date BETWEEN addDays(min_date, 6) AND addDays(min_date, 9) THEN '7～10天'",
        "            ELSE NULL",
        "        END AS range_name",
        "    FROM aikang.user_meal_checkin",
        "    WHERE checkin_date BETWEEN min_date AND addDays(min_date, 9)",
        ")",
        "WHERE range_name IS NOT NULL",
        "GROUP BY range_name",
        "ORDER BY",
        "    CASE range_name WHEN '前3天' THEN 1 WHEN '4～6天' THEN 2 WHEN '7～10天' THEN 3 ELSE 4 END"
    })
    List<MealCheckinCompletionRate> selectMealCheckinCompletionRates();
    
    /**
     * 查询指定月份不同时间段的餐食打卡完成率
     * 
     * @param targetDate 目标日期
     * @return 餐食打卡完成率列表
     */
    @Select({
        "WITH (SELECT min(checkin_date) FROM aikang.user_meal_checkin WHERE toYear(checkin_date) = toYear(toDate(#{targetDate})) AND toMonth(checkin_date) = toMonth(toDate(#{targetDate}))) AS min_date",
        "SELECT",
        "    range_name AS rangeName,",
        "    round(",
        "        (",
        "            sum(ifNull(breakfast_checked, 0))",
        "            + sum(ifNull(lunch_checked, 0))",
        "            + sum(ifNull(dinner_checked, 0))",
        "        ) / (count() * 3),",
        "        4",
        "    ) AS completionRate",
        "FROM",
        "(",
        "    SELECT",
        "        checkin_date,",
        "        breakfast_checked,",
        "        lunch_checked,",
        "        dinner_checked,",
        "        CASE",
        "            WHEN checkin_date BETWEEN min_date AND addDays(min_date, 2) THEN '前3天'",
        "            WHEN checkin_date BETWEEN addDays(min_date, 3) AND addDays(min_date, 5) THEN '4～6天'",
        "            WHEN checkin_date BETWEEN addDays(min_date, 6) AND addDays(min_date, 9) THEN '7～10天'",
        "            ELSE NULL",
        "        END AS range_name",
        "    FROM aikang.user_meal_checkin",
        "    WHERE checkin_date BETWEEN min_date AND addDays(min_date, 9)",
        "      AND toYear(checkin_date) = toYear(toDate(#{targetDate})) AND toMonth(checkin_date) = toMonth(toDate(#{targetDate}))",
        ")",
        "WHERE range_name IS NOT NULL",
        "GROUP BY range_name",
        "ORDER BY",
        "    CASE range_name WHEN '前3天' THEN 1 WHEN '4～6天' THEN 2 WHEN '7～10天' THEN 3 ELSE 4 END"
    })
    List<MealCheckinCompletionRate> selectMealCheckinCompletionRatesByMonth(@Param("targetDate") LocalDateTime targetDate);
}