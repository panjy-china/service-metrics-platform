package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.mapper.clickhouse.ClientMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 客户分析服务类
 * 
 * @author System Generated
 */
@Service
public class ClientAnalysisService {

    @Autowired
    private ClientMapper clientMapper;

    /**
     * 获取客户年龄分布数据
     * 
     * @return 年龄分布数据列表
     */
    public List<Map<String, Object>> getAgeDistribution() {
        return clientMapper.selectAgeDistribution();
    }
}