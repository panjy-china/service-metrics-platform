package org.panjy.servicemetricsplatform.service.serverTime;

import org.panjy.servicemetricsplatform.entity.serverTime.ServerTime;
import org.panjy.servicemetricsplatform.entity.order.OrderRetentionRate;
import org.panjy.servicemetricsplatform.mapper.serverTime.ServerTimeMapper;
import org.panjy.servicemetricsplatform.mapper.order.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 服务时间服务类
 * 提供服务时间相关的业务逻辑
 */
@Service
public class ServerTimeService {
    
    private static final Logger logger = LoggerFactory.getLogger(ServerTimeService.class);
    
    @Autowired
    private ServerTimeMapper serverTimeMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    /**
     * 处理指定日期之后的所有记录，查询指定日期后出现的用户id，之后查询该用户id的最早一条记录以及最晚一条记录，将两者之差作为服务时间
     * 
     * @param date 指定日期
     * @return 处理结果
     */
    public boolean processServerTimeAfterDate(LocalDate date) {
        try {
            logger.info("开始处理指定日期之后的所有记录，日期: {}", date);
            
            // 1. 获取指定日期之后存在下单记录的客户ID列表
            LocalDateTime dateTime = date.atStartOfDay();
            List<String> clientIds = orderMapper.getClientIdsWithOrdersAfterDate(dateTime);
            
            if (clientIds == null || clientIds.isEmpty()) {
                logger.warn("指定日期之后没有客户下单记录");
                return false;
            }
            
            logger.info("找到 {} 个在指定日期之后存在下单记录的客户", clientIds.size());
            
            // 2. 准备插入数据
            List<ServerTime> serverTimes = new ArrayList<>();
            
            // 3. 对每个客户计算服务时间（最早订单时间到最早销售订单时间的差值）
            for (String clientId : clientIds) {
                // 检查客户是否存在colEmpID LIKE 'E%'的销售订单
                if (!orderMapper.hasSalesOrders(clientId)) {
                    logger.debug("客户 {} 没有销售订单，跳过处理", clientId);
                    continue;
                }
                
                // 获取客户的最早订单时间
                LocalDateTime earliestTime = orderMapper.getEarliestOrderTimeByClientId(clientId);
                // 获取客户的最早销售订单时间（colEmpID以'E'为前缀）
                LocalDateTime latestTime = orderMapper.getEarliestSalesOrderTimeByClientId(clientId);
                
                // 检查时间是否有效
                if (earliestTime != null && latestTime != null) {
                    // 计算服务时间（最早销售订单时间 - 最早订单时间）
                    Duration duration = Duration.between(earliestTime, latestTime);
                    long serviceTimeInSeconds = duration.getSeconds();
                    
                    // 只有当服务时间大于0时才添加记录
                    if (serviceTimeInSeconds > 0) {
                        // 添加到列表中（每个用户的服务时间）
                        ServerTime serverTime = new ServerTime();
                        serverTime.setColCltID(clientId);
                        // 将服务时间（秒数）存储到colSerTi字段
                        serverTime.setColSerTi(serviceTimeInSeconds);
                        // 设置创建时间和更新时间（不包含微秒）
                        LocalDateTime now = LocalDateTime.now().withNano(0);
                        serverTime.setCreateTime(earliestTime
                        );
                        serverTime.setUpdateTime(now);
                        serverTimes.add(serverTime);
                    }
                }
            }
            
            // 4. 批量插入到tbl_ServerTime表中
            if (!serverTimes.isEmpty()) {
                int result = serverTimeMapper.batchInsertOrUpdate(serverTimes);
                logger.info("成功处理 {} 条服务时间记录", result);
                return true;
            } else {
                logger.warn("没有有效的服务时间记录需要处理");
                return false;
            }
            
        } catch (Exception e) {
            logger.error("处理服务时间记录失败，日期: {}", date, e);
            return false;
        }
    }
    
    /**
     * 查询指定日期之后的所有服务时间记录
     * 
     * @param date 指定日期
     * @return 服务时间记录列表
     */
    public List<ServerTime> getServerTimesAfterDate(LocalDate date) {
        try {
            logger.info("查询指定日期之后的所有服务时间记录，日期: {}", date);
            return serverTimeMapper.getServerTimesAfterDate(date);
        } catch (Exception e) {
            logger.error("查询服务时间记录失败，日期: {}", date, e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 查询所有客户的服务时间记录
     * 
     * @return 服务时间记录列表
     */
    public List<ServerTime> getAllServerTimes() {
        try {
            logger.info("查询所有客户的服务时间记录");
            return serverTimeMapper.getAllServerTimes();
        } catch (Exception e) {
            logger.error("查询所有客户的服务时间记录失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 计算推单三日留存率
     * 
     * @return 留存率数据
     */
    public OrderRetentionRate calculateOrderRetentionRate() {
        try {
            logger.info("开始计算推单三日留存率");
            OrderRetentionRate retentionRate = serverTimeMapper.calculateOrderRetentionRate();
            logger.info("推单三日留存率计算完成: {}", retentionRate);
            return retentionRate;
        } catch (Exception e) {
            logger.error("计算推单三日留存率失败", e);
            return new OrderRetentionRate(0L, 0L, 0.0);
        }
    }
    
    /**
     * 计算指定月份的推单三日留存率
     * 
     * @param targetDate 目标日期
     * @return 留存率数据
     */
    public OrderRetentionRate calculateOrderRetentionRateByMonth(LocalDateTime targetDate) {
        try {
            logger.info("开始计算指定月份的推单三日留存率，日期: {}", targetDate);
            OrderRetentionRate retentionRate = serverTimeMapper.calculateOrderRetentionRateByMonth(targetDate);
            logger.info("指定月份的推单三日留存率计算完成: {}", retentionRate);
            return retentionRate;
        } catch (Exception e) {
            logger.error("计算指定月份的推单三日留存率失败，日期: {}", targetDate, e);
            return new OrderRetentionRate(0L, 0L, 0.0);
        }
    }
    
    /**
     * 计算指定月份的推单三日留存率及环比增长率
     * 
     * @param targetDate 目标日期
     * @return 包含当月留存率和环比增长率的Map
     */
    public java.util.Map<String, Object> calculateOrderRetentionRateWithGrowth(LocalDateTime targetDate) {
        // 计算目标月份的留存率
        OrderRetentionRate currentRate = calculateOrderRetentionRateByMonth(targetDate);
        double currentRetentionRate = currentRate.getRetentionRate();
        
        // 计算上个月的留存率
        LocalDateTime previousMonth = targetDate.minusMonths(1);
        OrderRetentionRate previousRate = calculateOrderRetentionRateByMonth(previousMonth);
        double previousRetentionRate = previousRate.getRetentionRate();
        
        // 计算环比增长率
        double growthRate = 0.0;
        if (previousRetentionRate != 0) {
            growthRate = (currentRetentionRate - previousRetentionRate) / previousRetentionRate;
        }
        
        // 构造返回结果
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("currentRate", String.format("%.2f", currentRetentionRate * 100));  // 不带%的百分比
        result.put("growthRate", String.format("%.2f", growthRate * 100));  // 环比增长率，带%的百分比
        
        return result;
    }
}