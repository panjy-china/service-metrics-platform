package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.panjy.servicemetricsplatform.entity.CallDurationStatistics;

import java.util.List;

/**
 * 通话时长统计数据访问接口
 */
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
        "    WHERE DNISANS = 1",
        "      AND DNANSTIME IS NOT NULL",
        "      AND DNOUTTIME IS NOT NULL",
        ")",
        "GROUP BY duration_range",
        "ORDER BY duration_range"
    })
    List<CallDurationStatistics> selectCallDurationStatistics();
}