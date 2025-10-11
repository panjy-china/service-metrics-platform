package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.CallDurationStatistics;
import org.panjy.servicemetricsplatform.mapper.CallDurationStatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通话时长统计服务类
 */
@Service
public class CallDurationStatisticsService {
    
    @Autowired
    private CallDurationStatisticsMapper callDurationStatisticsMapper;
    
    /**
     * 获取不同通话时长区间的记录数量
     * 
     * @return 通话时长统计列表
     */
    public List<CallDurationStatistics> getCallDurationStatistics() {
        return callDurationStatisticsMapper.selectCallDurationStatistics();
    }
}