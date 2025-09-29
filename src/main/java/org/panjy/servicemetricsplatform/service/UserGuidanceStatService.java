package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.UserGuidanceStat;
import org.panjy.servicemetricsplatform.mapper.UserGuidanceStatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserGuidanceStat服务类
 * 提供对用户指导统计记录的业务逻辑处理
 */
@Service
public class UserGuidanceStatService {
    
    @Autowired
    private UserGuidanceStatMapper userGuidanceStatMapper;
    
    /**
     * 批量查询用户指导统计记录
     * 
     * @param userGuidanceStats 查询条件列表
     * @return 查询结果列表
     */
    public List<UserGuidanceStat> batchSelect(List<UserGuidanceStat> userGuidanceStats) {
        return userGuidanceStatMapper.batchSelect(userGuidanceStats);
    }
    
    /**
     * 批量插入用户指导统计记录
     * 
     * @param userGuidanceStats 用户指导统计记录列表
     * @return 受影响的行数
     */
    public int batchInsert(List<UserGuidanceStat> userGuidanceStats) {
        return userGuidanceStatMapper.batchInsert(userGuidanceStats);
    }
}