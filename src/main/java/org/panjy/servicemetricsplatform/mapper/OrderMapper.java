package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    
    /**
     * 查询指定客户ID的最早下单时间
     * @param clientId 客户ID
     * @return 最早下单时间
     */
    LocalDateTime getEarliestOrderTimeByClientId(@Param("clientId") String clientId);
    
    /**
     * 查询指定客户ID的最晚下单时间
     * @param clientId 客户ID
     * @return 最晚下单时间
     */
    LocalDateTime getLatestOrderTimeByClientId(@Param("clientId") String clientId);
    
    /**
     * 查询所有客户的最早下单时间
     * @return 最早下单时间
     */
    LocalDateTime getEarliestOrderTimeForAll();
    
    /**
     * 查询所有客户的最晚下单时间
     * @return 最晚下单时间
     */
    LocalDateTime getLatestOrderTimeForAll();
    
    /**
     * 获取所有唯一的客户ID列表
     * @return 客户ID列表
     */
    List<String> getAllUniqueClientIds();
    
    /**
     * 查询指定日期之后首次出现在表中的用户ID
     * @param date 指定日期
     * @return 在指定日期之后首次下单的用户ID列表
     */
    List<String> getNewClientIdsAfterDate(@Param("date") LocalDateTime date);
    
    /**
     * 根据处理后的地址串进行模糊查询，返回匹配到的第一条记录的客户ID
     * @param processedAddress 处理后的地址串
     * @return 匹配到的第一条记录的客户ID
     */
    String getCltIdByProcessedAddress(@Param("processedAddress") String processedAddress);
}