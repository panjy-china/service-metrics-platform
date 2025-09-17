package org.panjy.servicemetricsplatform.mapper.clickhouse;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.Order;

import java.util.List;

/**
 * 订单Mapper接口
 * 用于ClickHouse数据库操作
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 根据地址模糊查询返回客户ID列表
     * @param addressKeyword 地址关键词
     * @return 客户ID列表
     */
    List<String> findCustomerIdsByAddressLike(@Param("addressKeyword") String addressKeyword);
    
    /**
     * 根据地址模糊查询返回订单信息（包含客户ID）
     * @param addressKeyword 地址关键词
     * @return 订单列表
     */
    List<Order> findOrdersByAddressLike(@Param("addressKeyword") String addressKeyword);
    
    /**
     * 根据地址模糊查询返回不重复的客户ID列表
     * @param addressKeyword 地址关键词
     * @return 不重复的客户ID列表
     */
    List<String> findDistinctCustomerIdsByAddressLike(@Param("addressKeyword") String addressKeyword);
}