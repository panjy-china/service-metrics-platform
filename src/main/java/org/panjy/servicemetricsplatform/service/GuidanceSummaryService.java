package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.GuidanceSummary;
import org.panjy.servicemetricsplatform.entity.UserGuidanceStat;
import org.panjy.servicemetricsplatform.mapper.UserGuidanceStatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 指导次数汇总服务类
 */
@Service
public class GuidanceSummaryService {
    
    @Autowired
    private UserGuidanceStatMapper userGuidanceStatMapper;
    
    /**
     * 获取总指导次数和总个性化指导次数
     * 
     * @return 指导次数汇总对象
     */
    public GuidanceSummary getTotalGuidanceCounts() {
        UserGuidanceStat userGuidanceStat = userGuidanceStatMapper.getTotalGuidanceCounts();
        if (userGuidanceStat != null) {
            return new GuidanceSummary(
                userGuidanceStat.getGuidanceCount(),
                userGuidanceStat.getPersonalizedGuidanceCount()
            );
        }
        return new GuidanceSummary(0, 0);
    }
}