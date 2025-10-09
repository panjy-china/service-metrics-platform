package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.CallStatistics;
import org.panjy.servicemetricsplatform.mapper.TblTjInCallMapper;
import org.panjy.servicemetricsplatform.mapper.TblTjOutCallStatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 个性化指导率服务类
 * 用于计算和处理个性化指导触达率（长通话次数/总通话次数）
 */
@Service
public class PersonalizedGuidanceRateService {

    @Autowired
    private TblTjInCallMapper tblTjInCallMapper;

    @Autowired
    private TblTjOutCallStatisticsMapper tblTjOutCallStatisticsMapper;

    /**
     * 计算所有用户的个性化指导触达率
     * 个性化指导触达率 = 长通话次数 / 总通话次数
     *
     * @return 包含个性化指导触达率的通话统计信息列表
     */
    public List<CallStatistics> calculatePersonalizedGuidanceRates() {
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
                int newTotalCalls = (existingStat.getTotalCalls() != null ? existingStat.getTotalCalls() : 0) +
                        (stat.getTotalCalls() != null ? stat.getTotalCalls() : 0);
                int newLongCalls = (existingStat.getLongCalls() != null ? existingStat.getLongCalls() : 0) +
                        (stat.getLongCalls() != null ? stat.getLongCalls() : 0);

                existingStat.setTotalCalls(newTotalCalls);
                existingStat.setLongCalls(newLongCalls);
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

        // 计算个性化指导触达率
        for (CallStatistics stat : mergedStatistics.values()) {
            int totalCalls = stat.getTotalCalls() != null ? stat.getTotalCalls() : 0;
            int longCalls = stat.getLongCalls() != null ? stat.getLongCalls() : 0;

            // 计算个性化指导触达率（长通话次数/总通话次数）
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
     * 根据微信ID获取个性化指导触达率
     *
     * @param wechatId 微信ID
     * @return 个性化指导触达率
     */
    public Double getPersonalizedGuidanceRateByWechatId(String wechatId) {
        List<CallStatistics> allStatistics = calculatePersonalizedGuidanceRates();

        // 查找指定微信ID的统计数据
        for (CallStatistics stat : allStatistics) {
            if (wechatId.equals(stat.getWechatId())) {
                return stat.getPersonalizedGuidanceRate();
            }
        }

        // 如果未找到，返回默认值
        return 0.0;
    }

    /**
     * 获取所有用户的个性化指导触达率，仅返回触达率信息
     *
     * @return 微信ID和对应的个性化指导触达率映射
     */
    public Map<String, Double> getAllPersonalizedGuidanceRates() {
        List<CallStatistics> statistics = calculatePersonalizedGuidanceRates();
        Map<String, Double> rateMap = new HashMap<>();

        for (CallStatistics stat : statistics) {
            rateMap.put(stat.getWechatId(), stat.getPersonalizedGuidanceRate());
        }

        return rateMap;
    }
}