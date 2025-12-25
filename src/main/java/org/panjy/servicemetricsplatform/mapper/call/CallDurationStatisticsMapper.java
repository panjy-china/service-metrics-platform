package org.panjy.servicemetricsplatform.mapper.call;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.panjy.servicemetricsplatform.entity.call.CallDurationStatistics;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CallDurationStatisticsMapper {
    
    /**
     * 查询不同通话时长区间的记录数量
     * 
     * @return 通话时长统计列表
     */
    @Select({
        "SELECT duration_range AS durationRange, count(*) AS recordCount",
        "FROM (",
        "    SELECT",
        "        CASE",
        "            WHEN dateDiff('second', DNANSTIME, DNOUTTIME) < 300 THEN '<5分钟'",
        "            WHEN dateDiff('second', DNANSTIME, DNOUTTIME) BETWEEN 300 AND 599 THEN '5-10分钟'",
        "            WHEN dateDiff('second', DNANSTIME, DNOUTTIME) BETWEEN 600 AND 899 THEN '10-15分钟'",
        "            WHEN dateDiff('second', DNANSTIME, DNOUTTIME) BETWEEN 900 AND 1199 THEN '15-20分钟'",
        "            ELSE '20分钟以上'",
        "        END AS duration_range",
        "    FROM aikang.Tbl_Tj_OutCall",
        "    WHERE colEmpID LIKE 'E%'" ,
        "      AND DNISANS = 1",
        "      AND DNANSTIME IS NOT NULL",
        "      AND DNOUTTIME IS NOT NULL",
        ")",
        "GROUP BY duration_range",
        "ORDER BY duration_range"
    })
    List<CallDurationStatistics> selectCallDurationStatistics();
    
    /**
     * 查询指定月份不同通话时长区间的记录数量
     * 
     * @param date 指定日期（用于确定月份）
     * @return 通话时长统计列表
     */
    @Select({
        "SELECT duration_range AS durationRange, count(*) AS recordCount",
        "FROM (",
        "    SELECT",
        "        CASE",
        "            WHEN dateDiff('second', DNANSTIME, DNOUTTIME) < 300 THEN '<5分钟'",
        "            WHEN dateDiff('second', DNANSTIME, DNOUTTIME) BETWEEN 300 AND 599 THEN '5-10分钟'",
        "            WHEN dateDiff('second', DNANSTIME, DNOUTTIME) BETWEEN 600 AND 899 THEN '10-15分钟'",
        "            WHEN dateDiff('second', DNANSTIME, DNOUTTIME) BETWEEN 900 AND 1199 THEN '15-20分钟'",
        "            ELSE '20分钟以上'",
        "        END AS duration_range",
        "    FROM aikang.Tbl_Tj_OutCall",
        "    WHERE colEmpID LIKE 'E%'" ,
        "      AND DNISANS = 1",
        "      AND DNANSTIME IS NOT NULL",
        "      AND DNOUTTIME IS NOT NULL",
        "      AND toMonth(DNINTIME) = toMonth(toDate(#{date}))",
        "      AND toYear(DNINTIME) = toYear(toDate(#{date}))",
        ")",
        "GROUP BY duration_range",
        "ORDER BY duration_range"
    })
    List<CallDurationStatistics> selectCallDurationStatisticsByMonth(@Param("date") LocalDateTime date);
}