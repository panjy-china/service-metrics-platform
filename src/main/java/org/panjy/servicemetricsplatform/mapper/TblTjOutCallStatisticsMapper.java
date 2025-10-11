package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.panjy.servicemetricsplatform.entity.CallStatistics;
//import org.panjy.servicemetricsplatform.entity.CallDurationStatistics;

import java.util.List;
import java.util.Map;

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
     * m.wechat_id,
     * countIf(dateDiff('second', o.DNINTIME, o.DNOUTTIME) >= 60) AS total_calls,
     * countIf(dateDiff('second', o.DNINTIME, o.DNOUTTIME) >= 900) AS long_calls
     * FROM aikang.Tbl_Tj_OutCall o
     * INNER JOIN aikang.tbl_wechat_member m
     * ON o.colCltID = m.colCltID
     * WHERE o.DNOUTTIME IS NOT NULL
     * AND o.DNINTIME IS NOT NULL
     * GROUP BY m.wechat_id
     * ORDER BY total_calls DESC;
     *
     * @return 通话统计信息列表
     */
    @Select({
            "SELECT",
            "    m.wechat_id AS wechatId,",
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

    /**
     * 查询OutCall表中时长超过60秒和超过5分钟的通话统计
     * 对应SQL:
     * SELECT
     * m.wechat_id,
     * countIf(dateDiff('second', o.DNINTIME, o.DNOUTTIME) >= 60) AS calls_over_60s,
     * countIf(dateDiff('second', o.DNINTIME, o.DNOUTTIME) >= 300) AS calls_over_300s
     * FROM aikang.Tbl_Tj_OutCall o
     * INNER JOIN aikang.tbl_wechat_member m
     * ON o.colCltID = m.colCltID
     * WHERE o.DNOUTTIME IS NOT NULL
     * AND o.DNINTIME IS NOT NULL
     * GROUP BY m.wechat_id
     * ORDER BY calls_over_60s DESC;
     *
     * @return 通话统计信息列表
     */
    @Select({
            "SELECT",
            "    m.wechat_id AS wechatId,",
            "    countIf(dateDiff('second', o.DNINTIME, o.DNOUTTIME) >= 60) AS total_calls,",
            "    countIf(dateDiff('second', o.DNINTIME, o.DNOUTTIME) >= 300) AS long_calls",
            "FROM aikang.Tbl_Tj_OutCall o",
            "INNER JOIN aikang.tbl_wechat_member m",
            "    ON o.colCltID = m.colCltID",
            "WHERE o.DNOUTTIME IS NOT NULL",
            "  AND o.DNINTIME IS NOT NULL",
            "GROUP BY m.wechat_id",
            "ORDER BY total_calls DESC"
    })
    List<CallStatistics> selectOutCallStatisticsOver60sAnd300s();

    /**
     * 查询所有用户的OutCall通话次数统计
     * 对应SQL:
     * SELECT
     * m.wechat_id,
     * count(*) AS total_calls
     * FROM aikang.Tbl_Tj_OutCall o
     * INNER JOIN aikang.tbl_wechat_member m
     * ON o.colCltID = m.colCltID
     * WHERE o.DNOUTTIME IS NOT NULL
     * AND o.DNINTIME IS NOT NULL
     * GROUP BY m.wechat_id
     * ORDER BY total_calls DESC;
     *
     * @return 用户通话次数统计列表
     */
    @Select({
            "SELECT",
            "    m.wechat_id AS wechatId,",
            "    count(*) AS total_calls",
            "FROM aikang.Tbl_Tj_OutCall o",
            "INNER JOIN aikang.tbl_wechat_member m",
            "    ON o.colCltID = m.colCltID",
            "WHERE o.DNOUTTIME IS NOT NULL",
            "  AND o.DNINTIME IS NOT NULL",
            "GROUP BY m.wechat_id",
            "ORDER BY total_calls DESC"
    })
    List<Map<String, Object>> selectAllUserOutCallCounts();
}