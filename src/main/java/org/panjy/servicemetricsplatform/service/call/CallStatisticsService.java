package org.panjy.servicemetricsplatform.service.call;

import org.panjy.servicemetricsplatform.entity.call.CallStatistics;
import org.panjy.servicemetricsplatform.mapper.call.TblTjInCallMapper;
import org.panjy.servicemetricsplatform.mapper.call.TblTjOutCallStatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通话统计服务类
 * 用于处理通话统计数据并计算个性化指导率
 */
@Service
public class CallStatisticsService {
    
    @Autowired
    private TblTjInCallMapper tblTjInCallMapper;
    
    @Autowired
    private TblTjOutCallStatisticsMapper tblTjOutCallStatisticsMapper;
    
    /**
     * 获取所有通话统计数据并计算个性化指导率
     * 
     * @return 通话统计信息列表，包含个性化指导率
     */
    public List<CallStatistics> getAllCallStatistics() {
        // 获取InCall表的统计数据
        List<CallStatistics> inCallStatistics = tblTjInCallMapper.selectInCallStatistics();
        
        // 获取OutCall表的统计数据
        List<CallStatistics> outCallStatistics = tblTjOutCallStatisticsMapper.selectOutCallStatistics();
        
        // 合并两个表的数据
        Map<String, CallStatistics> mergedStatistics = new HashMap<>();
        
        // 处理InCall统计数据
        for (CallStatistics stat : inCallStatistics) {
            mergedStatistics.put(stat.getWechatId(), new CallStatistics(
                stat.getWechatId(),
                stat.getTotalCalls(),
                stat.getLongCalls(),
                0.0 // 初始化个性化指导率
            ));
        }
        
        // 处理OutCall统计数据，合并到已有的数据中
        for (CallStatistics stat : outCallStatistics) {
            String wechatId = stat.getWechatId();
            if (mergedStatistics.containsKey(wechatId)) {
                // 如果已存在该微信ID的记录，则合并数据
                CallStatistics existingStat = mergedStatistics.get(wechatId);
                existingStat.setTotalCalls(existingStat.getTotalCalls() + stat.getTotalCalls());
                existingStat.setLongCalls(existingStat.getLongCalls() + stat.getLongCalls());
            } else {
                // 如果不存在该微信ID的记录，则直接添加
                mergedStatistics.put(wechatId, new CallStatistics(
                    stat.getWechatId(),
                    stat.getTotalCalls(),
                    stat.getLongCalls(),
                    0.0 // 初始化个性化指导率
                ));
            }
        }
        
        // 计算个性化指导率
        for (CallStatistics stat : mergedStatistics.values()) {
            int totalCalls = stat.getTotalCalls() != null ? stat.getTotalCalls() : 0;
            int longCalls = stat.getLongCalls() != null ? stat.getLongCalls() : 0;
            
            // 计算个性化指导率（长通话次数/总通话次数）
            double personalizedGuidanceRate = totalCalls > 0 ? (double) longCalls / totalCalls : 0.0;
            stat.setPersonalizedGuidanceRate(personalizedGuidanceRate);
        }
        
        // 转换为列表并按总通话次数降序排序
        List<CallStatistics> result = new ArrayList<>(mergedStatistics.values());
        result.sort((s1, s2) -> {
            int calls1 = s1.getTotalCalls() != null ? s1.getTotalCalls() : 0;
            int calls2 = s2.getTotalCalls() != null ? s2.getTotalCalls() : 0;
            return Integer.compare(calls2, calls1); // 降序排序
        });
        
        return result;
    }
    
    /**
     * 根据微信ID获取通话统计数据
     * 
     * @param wechatId 微信ID
     * @return 通话统计信息
     */
    public CallStatistics getCallStatisticsByWechatId(String wechatId) {
        List<CallStatistics> allStatistics = getAllCallStatistics();
        
        // 查找指定微信ID的统计数据
        for (CallStatistics stat : allStatistics) {
            if (wechatId.equals(stat.getWechatId())) {
                return stat;
            }
        }
        
        // 如果未找到，返回默认值
        return new CallStatistics(wechatId, 0, 0, 0.0);
    }
    
    /**
     * 计算电话时长达标率
     * 达标率 = calls_over_300s / calls_over_60s
     * 
     * @return 电话时长达标率统计列表
     */
    public List<CallStatistics> calculateCallDurationComplianceRate() {
        // 获取InCall表的统计数据 (calls_over_60s and calls_over_300s)
        List<CallStatistics> inCallStatistics = tblTjInCallMapper.selectInCallStatisticsOver60sAnd300s();
        
        // 获取OutCall表的统计数据 (calls_over_60s and calls_over_300s)
        List<CallStatistics> outCallStatistics = tblTjOutCallStatisticsMapper.selectOutCallStatisticsOver60sAnd300s();
        
        // 合并两个表的数据
        Map<String, CallStatistics> mergedStatistics = new HashMap<>();
        
        // 处理InCall统计数据
        for (CallStatistics stat : inCallStatistics) {
            mergedStatistics.put(stat.getWechatId(), new CallStatistics(
                stat.getWechatId(),
                stat.getTotalCalls(), // 这里是calls_over_60s
                stat.getLongCalls(),  // 这里是calls_over_300s
                0.0 // 初始化达标率
            ));
        }
        
        // 处理OutCall统计数据，合并到已有的数据中
        for (CallStatistics stat : outCallStatistics) {
            String wechatId = stat.getWechatId();
            if (mergedStatistics.containsKey(wechatId)) {
                // 如果已存在该微信ID的记录，则合并数据
                CallStatistics existingStat = mergedStatistics.get(wechatId);
                int totalCalls60s = (existingStat.getTotalCalls() != null ? existingStat.getTotalCalls() : 0) + 
                                   (stat.getTotalCalls() != null ? stat.getTotalCalls() : 0); // calls_over_60s
                int longCalls300s = (existingStat.getLongCalls() != null ? existingStat.getLongCalls() : 0) + 
                                   (stat.getLongCalls() != null ? stat.getLongCalls() : 0); // calls_over_300s
                
                existingStat.setTotalCalls(totalCalls60s);
                existingStat.setLongCalls(longCalls300s);
            } else {
                // 如果不存在该微信ID的记录，则直接添加
                mergedStatistics.put(wechatId, new CallStatistics(
                    stat.getWechatId(),
                    stat.getTotalCalls(), // 这里是calls_over_60s
                    stat.getLongCalls(),  // 这里是calls_over_300s
                    0.0 // 初始化达标率
                ));
            }
        }
        
        // 计算时长达标率
        for (CallStatistics stat : mergedStatistics.values()) {
            int callsOver60s = stat.getTotalCalls() != null ? stat.getTotalCalls() : 0;
            int callsOver300s = stat.getLongCalls() != null ? stat.getLongCalls() : 0;
            
            // 计算时长达标率（calls_over_300s/calls_over_60s）
            double complianceRate = callsOver60s > 0 ? (double) callsOver300s / callsOver60s : 0.0;
            stat.setPersonalizedGuidanceRate(complianceRate);
        }
        
        // 转换为列表并按calls_over_60s降序排序
        List<CallStatistics> result = new ArrayList<>(mergedStatistics.values());
        result.sort((s1, s2) -> {
            int calls1 = s1.getTotalCalls() != null ? s1.getTotalCalls() : 0;
            int calls2 = s2.getTotalCalls() != null ? s2.getTotalCalls() : 0;
            return Integer.compare(calls2, calls1); // 降序排序
        });
        
        return result;
    }
    
    /**
     * 根据微信ID获取电话时长达标率
     * 
     * @param wechatId 微信ID
     * @return 电话时长达标率统计
     */
    public CallStatistics getCallDurationComplianceRateByWechatId(String wechatId) {
        List<CallStatistics> allStatistics = calculateCallDurationComplianceRate();
        
        // 查找指定微信ID的统计数据
        for (CallStatistics stat : allStatistics) {
            if (wechatId.equals(stat.getWechatId())) {
                return stat;
            }
        }
        
        // 如果未找到，返回默认值
        return new CallStatistics(wechatId, 0, 0, 0.0);
    }
}