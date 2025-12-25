package org.panjy.servicemetricsplatform.mapper.call;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.panjy.servicemetricsplatform.entity.call.OrderCallTimeDiff;
import org.panjy.servicemetricsplatform.entity.PersonalizedGuidanceCompletionRate;

import java.util.List;

@Mapper
public interface OrderCallTimeDiffMapper {
    
    /**
     * 查询超过特定时长的通话次数统计
     * 对应SQL:
     * SELECT
     *     countIf(dateDiff('second', DNANSTIME, DNOUTTIME) > 60) AS over_1_min_count,
     *     countIf(dateDiff('second', DNANSTIME, DNOUTTIME) > 900) AS over_15_min_count
     * FROM aikang.Tbl_Tj_OutCall
     * WHERE DNISANS = 1
     *   AND DNANSTIME IS NOT NULL
     *   AND DNOUTTIME IS NOT NULL
     *   AND toYYYYMM(DNINTIME) = toYYYYMM(today());
     * 
     * @return 通话时长差异统计结果
     */
    @Select({
        "SELECT",
        "    countIf(dateDiff('second', DNANSTIME, DNOUTTIME) > 60) AS over1MinCount,",
        "    countIf(dateDiff('second', DNANSTIME, DNOUTTIME) > 900) AS over15MinCount",
        "FROM aikang.Tbl_Tj_OutCall",
        "WHERE DNISANS = 1",
        "  AND DNANSTIME IS NOT NULL",
        "  AND DNOUTTIME IS NOT NULL",
        "  AND toYYYYMM(DNINTIME) = toYYYYMM(today())"
    })
    PersonalizedGuidanceCompletionRate selectOrderCallTimeDiff();

    /**
     * 查询所有客户的订单与首电时间差信息
     * 对应SQL:
     * SELECT
     *     o.colCltID,
     *     MIN(o.colOdrTim) AS first_order_time,
     *     f.first_call_date,
     *     dateDiff('second', MIN(o.colOdrTim), f.first_call_date) AS diff_days
     * FROM aikang.tbl_Order AS o
     * INNER JOIN aikang.tbl_first_call_summary AS f
     *     ON o.colCltID = f.colCltID
     * GROUP BY
     *     o.colCltID,
     *     f.first_call_date
     * ORDER BY
     *     diff_days ASC;
     *
     * @return 订单与首电时间差信息列表
     */
    List<OrderCallTimeDiff> selectAllOrderCallTimeDiffs();
    
    /**
     * 根据指定月份查询客户的订单与首电时间差信息
     * 
     * @param yearMonth 指定的年月 (格式: yyyy-MM)
     * @return 订单与首电时间差信息列表
     */
    List<OrderCallTimeDiff> selectOrderCallTimeDiffsByMonth(@Param("yearMonth") String yearMonth);
}