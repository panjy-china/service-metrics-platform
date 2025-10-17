-- 测试查询语句，检查每个时间段的数据
SELECT 
    min(checkin_date) as min_date,
    max(checkin_date) as max_date,
    count() as total_records
FROM aikang.user_meal_checkin;

-- 检查每个时间段的记录数
SELECT 
    CASE
        WHEN checkin_date >= toDate((SELECT max(checkin_date) FROM aikang.user_meal_checkin) - 2)
            THEN '前3天'
        WHEN checkin_date >= toDate((SELECT max(checkin_date) FROM aikang.user_meal_checkin) - 5)
            AND checkin_date < toDate((SELECT max(checkin_date) FROM aikang.user_meal_checkin) - 2)
            THEN '4～6天'
        WHEN checkin_date >= toDate((SELECT max(checkin_date) FROM aikang.user_meal_checkin) - 9)
            AND checkin_date < toDate((SELECT max(checkin_date) FROM aikang.user_meal_checkin) - 5)
            THEN '7～10天'
        ELSE '其他'
    END AS range_name,
    count() as record_count
FROM aikang.user_meal_checkin
WHERE checkin_date >= toDate((SELECT max(checkin_date) FROM aikang.user_meal_checkin) - 9)
    AND checkin_date <= (SELECT max(checkin_date) FROM aikang.user_meal_checkin)
GROUP BY range_name
ORDER BY range_name;