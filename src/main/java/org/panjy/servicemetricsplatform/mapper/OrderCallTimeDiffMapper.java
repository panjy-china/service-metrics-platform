package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.panjy.servicemetricsplatform.entity.OrderCallTimeDiff;

import java.util.List;

/**
 * OrderCallTimeDiff数据访问接口
 * 用于查询订单与首电时间差信息
 */
@Mapper
public interface OrderCallTimeDiffMapper {
    
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
}