package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.panjy.servicemetricsplatform.entity.CallStatistics;
//import org.panjy.servicemetricsplatform.entity.CallDurationStatistics;

import java.util.List;
import java.util.Map;

/**
 * Tbl_Tj_InCall数据访问接口
 * 用于处理InCall表的通话统计查询
 */
@Mapper
public interface TblTjInCallMapper {

    /**
     * 查询InCall表中的通话统计信息
     * 对应SQL:
     * SELECT
     * m.wechat_id,
     * countIf(dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 60) AS total_calls,
     * countIf(dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 900) AS long_calls
     * FROM aikang.Tbl_Tj_InCall c
     * INNER JOIN aikang.tbl_wechat_member m
     * ON c.colCltID = m.colCltID
     * WHERE c.CDNOUTTIME IS NOT NULL
     * AND c.CDNINTIME IS NOT NULL
     * AND dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 60
     * GROUP BY m.wechat_id
     * ORDER BY total_calls DESC;
     *
     * @return 通话统计信息列表
     */
    @Select({
            "SELECT",
            "    m.wechat_id AS wechatId,",
            "    countIf(dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 60) AS total_calls,",
            "    countIf(dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 900) AS long_calls",
            "FROM aikang.Tbl_Tj_InCall c",
            "INNER JOIN aikang.tbl_wechat_member m",
            "    ON c.colCltID = m.colCltID",
            "WHERE c.CDNOUTTIME IS NOT NULL",
            "  AND c.CDNINTIME IS NOT NULL",
            "  AND dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 60",
            "GROUP BY m.wechat_id",
            "ORDER BY total_calls DESC"
    })
    List<CallStatistics> selectInCallStatistics();

    /**
     * 查询InCall表中时长超过60秒和超过5分钟的通话统计
     * 对应SQL:
     * SELECT
     * m.wechat_id,
     * countIf(dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 60) AS calls_over_60s,
     * countIf(dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 300) AS calls_over_300s
     * FROM aikang.Tbl_Tj_InCall c
     * INNER JOIN aikang.tbl_wechat_member m
     * ON c.colCltID = m.colCltID
     * WHERE c.CDNOUTTIME IS NOT NULL
     * AND c.CDNINTIME IS NOT NULL
     * AND dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 60
     * GROUP BY m.wechat_id
     * ORDER BY calls_over_60s DESC;
     *
     * @return 通话统计信息列表
     */
    @Select({
            "SELECT",
            "    m.wechat_id AS wechatId,",
            "    countIf(dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 60) AS total_calls,",
            "    countIf(dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 300) AS long_calls",
            "FROM aikang.Tbl_Tj_InCall c",
            "INNER JOIN aikang.tbl_wechat_member m",
            "    ON c.colCltID = m.colCltID",
            "WHERE c.CDNOUTTIME IS NOT NULL",
            "  AND c.CDNINTIME IS NOT NULL",
            "  AND dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 60",
            "GROUP BY m.wechat_id",
            "ORDER BY total_calls DESC"
    })
    List<CallStatistics> selectInCallStatisticsOver60sAnd300s();

    /**
     * 查询所有用户的InCall通话次数统计
     * 对应SQL:
     * SELECT
     * m.wechat_id,
     * count(*) AS total_calls
     * FROM aikang.Tbl_Tj_InCall c
     * INNER JOIN aikang.tbl_wechat_member m
     * ON c.colCltID = m.colCltID
     * WHERE c.CDNOUTTIME IS NOT NULL
     * AND c.CDNINTIME IS NOT NULL
     * AND dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 60
     * GROUP BY m.wechat_id
     * ORDER BY total_calls DESC;
     *
     * @return 用户通话次数统计列表
     */
    @Select({
            "SELECT",
            "    m.wechat_id AS wechatId,",
            "    count(*) AS total_calls",
            "FROM aikang.Tbl_Tj_InCall c",
            "INNER JOIN aikang.tbl_wechat_member m",
            "    ON c.colCltID = m.colCltID",
            "WHERE c.CDNOUTTIME IS NOT NULL",
            "  AND c.CDNINTIME IS NOT NULL",
            "  AND dateDiff('second', c.DNANSTIME, c.DNOUTTIME) >= 60",
            "GROUP BY m.wechat_id",
            "ORDER BY total_calls DESC"
    })
    List<Map<String, Object>> selectAllUserInCallCounts();
}