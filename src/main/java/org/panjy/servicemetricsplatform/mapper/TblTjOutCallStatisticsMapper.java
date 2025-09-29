package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.panjy.servicemetricsplatform.entity.CallStatistics;

import java.util.List;

/**
 * Tbl_Tj_OutCall统计数据访问接口
 * 用于处理OutCall表的通话统计查询
 */
@Mapper
public interface TblTjOutCallStatisticsMapper {
    
    /**
     * 查询OutCall表中的通话统计信息
     * 对应SQL:
     * SELECT
     *     m.wechat_id,
     *     countIf(dateDiff('second', o.DNINTIME, o.DNOUTTIME) >= 60) AS total_calls,
     *     countIf(dateDiff('second', o.DNINTIME, o.DNOUTTIME) >= 900) AS long_calls
     * FROM aikang.Tbl_Tj_OutCall o
     * INNER JOIN aikang.tbl_wechat_member m
     *     ON o.colCltID = m.colCltID
     * WHERE o.DNOUTTIME IS NOT NULL
     *   AND o.DNINTIME IS NOT NULL
     * GROUP BY m.wechat_id
     * ORDER BY total_calls DESC;
     * 
     * @return 通话统计信息列表
     */
    @Select({
        "SELECT",
        "    m.wechat_id,",
        "    countIf(dateDiff('second', o.DNINTIME, o.DNOUTTIME) >= 60) AS total_calls,",
        "    countIf(dateDiff('second', o.DNINTIME, o.DNOUTTIME) >= 900) AS long_calls",
        "FROM aikang.Tbl_Tj_OutCall o",
        "INNER JOIN aikang.tbl_wechat_member m",
        "    ON o.colCltID = m.colCltID",
        "WHERE o.DNOUTTIME IS NOT NULL",
        "  AND o.DNINTIME IS NOT NULL",
        "GROUP BY m.wechat_id",
        "ORDER BY total_calls DESC"
    })
    List<CallStatistics> selectOutCallStatistics();
}