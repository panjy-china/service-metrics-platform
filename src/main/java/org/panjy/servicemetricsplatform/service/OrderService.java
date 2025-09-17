package org.panjy.servicemetricsplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.panjy.servicemetricsplatform.mapper.clickhouse.OrderMapper;
import org.panjy.servicemetricsplatform.entity.Order;

import java.util.List;

/**
 * 订单服务类
 * 提供订单相关的业务逻辑
 */
@Service
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 根据地址关键词查询客户ID列表
     * @param addressKeyword 地址关键词
     * @return 客户ID列表
     */
    public List<String> findCustomerIdsByAddress(String addressKeyword) {
        if (addressKeyword == null || addressKeyword.trim().isEmpty()) {
            throw new IllegalArgumentException("地址关键词不能为空");
        }
        return orderMapper.findCustomerIdsByAddressLike(addressKeyword.trim());
    }
    
    /**
     * 根据地址关键词查询不重复的客户ID列表
     * @param addressKeyword 地址关键词
     * @return 不重复的客户ID列表
     */
    public List<String> findDistinctCustomerIdsByAddress(String addressKeyword) {
        if (addressKeyword == null || addressKeyword.trim().isEmpty()) {
            throw new IllegalArgumentException("地址关键词不能为空");
        }
        return orderMapper.findDistinctCustomerIdsByAddressLike(addressKeyword.trim());
    }
    
    /**
     * 根据地址关键词查询订单信息
     * @param addressKeyword 地址关键词
     * @return 订单列表
     */
    public List<Order> findOrdersByAddress(String addressKeyword) {
        if (addressKeyword == null || addressKeyword.trim().isEmpty()) {
            throw new IllegalArgumentException("地址关键词不能为空");
        }
        return orderMapper.findOrdersByAddressLike(addressKeyword.trim());
    }
}