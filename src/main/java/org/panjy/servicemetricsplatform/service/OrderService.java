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
    

}