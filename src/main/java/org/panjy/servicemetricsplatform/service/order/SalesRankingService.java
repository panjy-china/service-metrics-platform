package org.panjy.servicemetricsplatform.service.order;

import org.panjy.servicemetricsplatform.entity.order.SalesRanking;
import org.panjy.servicemetricsplatform.mapper.order.SalesRankingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 销售排行榜业务服务类
 * 提供销售排行榜相关的业务逻辑实现
 */
@Service
public class SalesRankingService {
    
    @Autowired
    private SalesRankingMapper salesRankingMapper;
    
    // 默认返回排行榜前10名
    private static final int DEFAULT_LIMIT = 10;
    
    /**
     * 获取指定日期的销售额自然周、自然月排行榜
     * 
     * @param date 指定日期
     * @return 包含周排行榜和月排行榜的Map
     */
    public Map<String, Object> getSalesRankingsByDate(LocalDate date) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取周排行榜
            List<SalesRanking> weeklyRankings = salesRankingMapper.getWeeklySalesRanking(date, DEFAULT_LIMIT);
            
            // 获取月排行榜
            List<SalesRanking> monthlyRankings = salesRankingMapper.getMonthlySalesRanking(date, DEFAULT_LIMIT);
            
            // 组装结果
            result.put("date", date);
            result.put("weeklyRankings", weeklyRankings);
            result.put("monthlyRankings", monthlyRankings);
            
        } catch (Exception e) {
            result.put("error", "获取销售排行榜数据失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取指定日期的销售额自然周排行榜
     * 
     * @param date 指定日期
     * @param limit 返回记录数限制
     * @return 周排行榜列表
     */
    public List<SalesRanking> getWeeklySalesRanking(LocalDate date, int limit) {
        try {
            return salesRankingMapper.getWeeklySalesRanking(date, limit);
        } catch (Exception e) {
            throw new RuntimeException("获取周销售排行榜失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取指定日期的销售额自然月排行榜
     * 
     * @param date 指定日期
     * @param limit 返回记录数限制
     * @return 月排行榜列表
     */
    public List<SalesRanking> getMonthlySalesRanking(LocalDate date, int limit) {
        try {
            return salesRankingMapper.getMonthlySalesRanking(date, limit);
        } catch (Exception e) {
            throw new RuntimeException("获取月销售排行榜失败: " + e.getMessage(), e);
        }
    }
}