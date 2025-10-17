package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 订单统计Mapper接口
 * 用于查询日、周、月订单数量
 */
@Mapper
public interface OrderStatisticsMapper {
    
    /**
     * 查询指定日期的日订单数
     * 
     * @param date 日期字符串，格式为 'YYYY-MM-DD'
     * @return 日订单数
     */
    @Select({
        "SELECT",
        "    COUNT(*) AS order_count",
        "FROM aikang.tbl_Order o",
        "INNER JOIN aikang.tbl_server_account a",
        "    ON o.colEmpID = a.account_code",
        "WHERE toDate(o.colOdrTim) = toDate(#{date})"
    })
    Integer selectDailyOrderCount(@Param("date") String date);

    /**
     * 查询指定日期前一天的日订单数
     * 
     * @param date 日期字符串，格式为 'YYYY-MM-DD'
     * @return 前一天日订单数
     */
    @Select({
        "SELECT",
        "    COUNT(*) AS order_count",
        "FROM aikang.tbl_Order o",
        "INNER JOIN aikang.tbl_server_account a",
        "    ON o.colEmpID = a.account_code",
        "WHERE toDate(o.colOdrTim) = toDate(#{date}) - INTERVAL 1 DAY"
    })
    Integer selectPreviousDayOrderCount(@Param("date") String date);

    /**
     * 查询指定日期所在周的订单数
     * 
     * @param date 日期字符串，格式为 'YYYY-MM-DD'
     * @return 周订单数
     */
    @Select({
        "SELECT",
        "    COUNT(*) AS order_count",
        "FROM aikang.tbl_Order o",
        "INNER JOIN aikang.tbl_server_account a",
        "    ON o.colEmpID = a.account_code",
        "WHERE o.colOdrTim >= toStartOfWeek(toDate(#{date}))",
        "  AND o.colOdrTim < addWeeks(toStartOfWeek(toDate(#{date})), 1)"
    })
    Integer selectWeeklyOrderCount(@Param("date") String date);

    /**
     * 查询指定日期前一周的订单数
     * 
     * @param date 日期字符串，格式为 'YYYY-MM-DD'
     * @return 前一周订单数
     */
    @Select({
        "SELECT",
        "    COUNT(*) AS order_count",
        "FROM aikang.tbl_Order o",
        "INNER JOIN aikang.tbl_server_account a",
        "    ON o.colEmpID = a.account_code",
        "WHERE o.colOdrTim >= toStartOfWeek(toDate(#{date}) - INTERVAL 7 DAY)",
        "  AND o.colOdrTim < addWeeks(toStartOfWeek(toDate(#{date}) - INTERVAL 7 DAY), 1)"
    })
    Integer selectPreviousWeekOrderCount(@Param("date") String date);

    /**
     * 查询指定日期所在月的订单数
     * 
     * @param date 日期字符串，格式为 'YYYY-MM-DD'
     * @return 月订单数
     */
    @Select({
        "SELECT",
        "    COUNT(*) AS order_count",
        "FROM aikang.tbl_Order o",
        "INNER JOIN aikang.tbl_server_account a",
        "    ON o.colEmpID = a.account_code",
        "WHERE o.colOdrTim >= toStartOfMonth(toDate(#{date}))",
        "  AND o.colOdrTim < addMonths(toStartOfMonth(toDate(#{date})), 1)"
    })
    Integer selectMonthlyOrderCount(@Param("date") String date);

    /**
     * 查询指定日期前一个月的订单数
     * 
     * @param date 日期字符串，格式为 'YYYY-MM-DD'
     * @return 前一个月订单数
     */
    @Select({
        "SELECT",
        "    COUNT(*) AS order_count",
        "FROM aikang.tbl_Order o",
        "INNER JOIN aikang.tbl_server_account a",
        "    ON o.colEmpID = a.account_code",
        "WHERE o.colOdrTim >= toStartOfMonth(toDate(#{date}) - INTERVAL 1 MONTH)",
        "  AND o.colOdrTim < addMonths(toStartOfMonth(toDate(#{date}) - INTERVAL 1 MONTH), 1)"
    })
    Integer selectPreviousMonthOrderCount(@Param("date") String date);
}