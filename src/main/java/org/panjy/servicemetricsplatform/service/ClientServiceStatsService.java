package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.ClientServiceStats;
import org.panjy.servicemetricsplatform.mapper.ClientServiceStatsMapper;
import org.panjy.servicemetricsplatform.mapper.WechatMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户服务统计服务类
 */
@Service
public class ClientServiceStatsService {
    
    @Autowired
    private ClientServiceStatsMapper clientServiceStatsMapper;
    
    @Autowired
    private WechatMemberMapper wechatMemberMapper;
    
    /**
     * 获取满足条件的客户服务统计数据
     * @return 客户服务统计数据列表
     */
    public List<ClientServiceStats> getClientServiceStats() {
        return clientServiceStatsMapper.getClientServiceStats();
    }
    
    /**
     * 计算推单成交率
     * 推单成交率 = 满足条件的客户数 / 客户总数
     * @return 推单成交率
     */
    public double calculatePushOrderConversionRate() {
        // 获取满足条件的客户数（即ClientServiceStats结果的数量）
        int qualifiedClientCount = getClientServiceStats().size();
        
        // 获取客户总数
        int totalClientCount = wechatMemberMapper.countDistinctColCltID();
        
        // 避免除零错误
        if (totalClientCount == 0) {
            return 0.0;
        }
        
        // 计算并返回推单成交率
        return (double) qualifiedClientCount / totalClientCount;
    }
}