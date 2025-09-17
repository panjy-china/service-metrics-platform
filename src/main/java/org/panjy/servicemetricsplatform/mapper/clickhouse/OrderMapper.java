package org.panjy.servicemetricsplatform.mapper.clickhouse;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.Order;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单Mapper接口
 * 用于ClickHouse数据库操作
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 查询指定时间段的总成交额
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总成交额
     */
    BigDecimal getTotalSalesAmount(@Param("startTime") String startTime, 
                                   @Param("endTime") String endTime);
    
    /**
     * 查询指定时间段的总成交客户数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 成交客户数
     */
    Long getTotalCustomerCount(@Param("startTime") String startTime, 
                               @Param("endTime") String endTime);
    
    /**
     * 查询指定时间段的总订单数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总订单数
     */
    Long getTotalOrderCount(@Param("startTime") String startTime, 
                            @Param("endTime") String endTime);
}