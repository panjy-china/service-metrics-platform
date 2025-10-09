package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.CallStatistics;
import org.panjy.servicemetricsplatform.mapper.TblTjInCallMapper;
import org.panjy.servicemetricsplatform.mapper.TblTjOutCallStatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 电话时长达标率服务类
 * 用于计算电话时长达标率：calls_over_300s/calls_over_60s
 */
@Service
public class CallDurationComplianceRateService {
    
    @Autowired
    private TblTjInCallMapper tblTjInCallMapper;
    
    @Autowired
    private TblTjOutCallStatisticsMapper tblTjOutCallStatisticsMapper;
    
    /**
     * 计算电话时长达标率
     * 达标率 = calls_over_300s / calls_over_60s
     * 
     * @return 电话时长达标率统计列表
     */
    public List<CallStatistics> calculateCallDurationComplianceRate() {
        // 获取InCall表的统计数据
        List<CallStatistics> inCallStatistics = tblTjInCallMapper.selectInCallStatisticsOver60sAnd300s();
        
        // 获取OutCall表的统计数据
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
     * 计算总电话时长达标率
     * 
     * @return 总电话时长达标率统计
     */
    public CallStatistics calculateTotalCallDurationComplianceRate() {
        // 获取InCall表的统计数据
        List<CallStatistics> inCallStatistics = tblTjInCallMapper.selectInCallStatisticsOver60sAnd300s();
        
        // 获取OutCall表的统计数据
        List<CallStatistics> outCallStatistics = tblTjOutCallStatisticsMapper.selectOutCallStatisticsOver60sAnd300s();
        
        // 计算总calls_over_60s和calls_over_300s
        int totalCallsOver60s = 0;
        int totalCallsOver300s = 0;
        
        // 统计InCall数据
        for (CallStatistics stat : inCallStatistics) {
            totalCallsOver60s += (stat.getTotalCalls() != null ? stat.getTotalCalls() : 0);
            totalCallsOver300s += (stat.getLongCalls() != null ? stat.getLongCalls() : 0);
        }
        
        // 统计OutCall数据
        for (CallStatistics stat : outCallStatistics) {
            totalCallsOver60s += (stat.getTotalCalls() != null ? stat.getTotalCalls() : 0);
            totalCallsOver300s += (stat.getLongCalls() != null ? stat.getLongCalls() : 0);
        }
        
        // 计算总达标率
        double totalComplianceRate = totalCallsOver60s > 0 ? (double) totalCallsOver300s / totalCallsOver60s : 0.0;
        
        // 返回总统计结果
        return new CallStatistics("TOTAL", totalCallsOver60s, totalCallsOver300s, totalComplianceRate);
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