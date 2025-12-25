package org.panjy.servicemetricsplatform.service.order;

import org.panjy.servicemetricsplatform.entity.order.OrderStatistics;
import org.panjy.servicemetricsplatform.mapper.order.OrderStatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * 订单统计服务类
 * 用于处理订单统计相关的业务逻辑
 */
@Service
public class OrderStatisticsService {

    private static final Logger logger = Logger.getLogger(OrderStatisticsService.class.getName());

    @Autowired
    private OrderStatisticsMapper orderStatisticsMapper;

    /**
     * 根据指定日期获取日、周、月订单数统计
     * 
     * @param date 日期字符串，格式为 'YYYY-MM-DD'
     * @return 订单统计信息，包含日、周、月订单数
     */
    public OrderStatistics getOrderStatisticsByDate(String date) {
        try {
            logger.info("开始获取日期[" + date + "]的订单统计信息");
            
            // 查询日订单数
            Integer dailyCount = orderStatisticsMapper.selectDailyOrderCount(date);
            logger.info("日期[" + date + "]的日订单数: " + dailyCount);
            
            // 查询周订单数
            Integer weeklyCount = orderStatisticsMapper.selectWeeklyOrderCount(date);
            logger.info("日期[" + date + "]所在周的订单数: " + weeklyCount);
            
            // 查询月订单数
            Integer monthlyCount = orderStatisticsMapper.selectMonthlyOrderCount(date);
            logger.info("日期[" + date + "]所在月的订单数: " + monthlyCount);
            
            OrderStatistics orderStatistics = new OrderStatistics(dailyCount, weeklyCount, monthlyCount);
            logger.info("订单统计信息获取完成: " + orderStatistics.toString());
            
            return orderStatistics;
        } catch (Exception e) {
            logger.severe("获取订单统计信息时发生异常: " + e.getMessage());
            // 返回默认值
            return new OrderStatistics(0, 0, 0);
        }
    }
    
    /**
     * 根据指定日期获取前一天、前一周、前一个月的订单数统计
     * 
     * @param date 日期字符串，格式为 'YYYY-MM-DD'
     * @return 订单统计信息，包含前一天、前一周、前一个月订单数
     */
    public OrderStatistics getPreviousOrderStatisticsByDate(String date) {
        try {
            logger.info("开始获取日期[" + date + "]的前序订单统计信息");
            
            // 查询前一天订单数
            Integer previousDayCount = orderStatisticsMapper.selectPreviousDayOrderCount(date);
            logger.info("日期[" + date + "]的前一天订单数: " + previousDayCount);
            
            // 查询前一周订单数
            Integer previousWeekCount = orderStatisticsMapper.selectPreviousWeekOrderCount(date);
            logger.info("日期[" + date + "]的前一周订单数: " + previousWeekCount);
            
            // 查询前一个月订单数
            Integer previousMonthCount = orderStatisticsMapper.selectPreviousMonthOrderCount(date);
            logger.info("日期[" + date + "]的前一个月订单数: " + previousMonthCount);
            
            OrderStatistics orderStatistics = new OrderStatistics(previousDayCount, previousWeekCount, previousMonthCount);
            logger.info("前序订单统计信息获取完成: " + orderStatistics.toString());
            
            return orderStatistics;
        } catch (Exception e) {
            logger.severe("获取前序订单统计信息时发生异常: " + e.getMessage());
            // 返回默认值
            return new OrderStatistics(0, 0, 0);
        }
    }
}