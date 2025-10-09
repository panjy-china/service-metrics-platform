package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.FirstCallRecord;
import org.panjy.servicemetricsplatform.entity.FirstCallSummary;
import org.panjy.servicemetricsplatform.mapper.FirstCallSummaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * FirstCallSummary服务类
 * 提供对首通电话摘要记录的业务逻辑处理
 */
@Service
public class FirstCallSummaryService {
    
    @Autowired
    private FirstCallSummaryMapper firstCallSummaryMapper;
    
    /**
     * 查询所有首通电话摘要记录
     * 
     * @return 所有首通电话摘要记录列表
     */
    public List<FirstCallSummary> selectAll() {
        return firstCallSummaryMapper.selectAll();
    }
    
    /**
     * 根据微信ID获取首通记录
     * 
     * @param wechatId 微信ID
     * @return 首通记录列表
     */
    public List<FirstCallRecord> getFirstCallRecordsByWechatId(String wechatId) {
        if (wechatId == null || wechatId.isEmpty()) {
            return new ArrayList<>();
        }
        return firstCallSummaryMapper.selectFirstCallRecordsByWechatId(wechatId);
    }
    
    /**
     * 计算时长达标电话比例
     * 对于通话时长超过十分钟(600秒)的认为时长达标
     * 
     * @return 时长达标电话总数/电话总数的比例
     */
    public double calculateQualifiedRate() {
        List<FirstCallSummary> allRecords = selectAll();
        
        if (allRecords.isEmpty()) {
            return 0.0;
        }
        
        long totalCalls = allRecords.size();
        long qualifiedCalls = allRecords.stream()
                .filter(record -> record.getCallDuration() != null && record.getCallDuration() > 600)
                .count();
        
        return (double) qualifiedCalls / totalCalls;
    }
    
    /**
     * 计算所有首通电话的平均通话时长
     * 
     * @return 平均通话时长（秒）
     */
    public double calculateAverageCallDuration() {
        List<FirstCallSummary> allRecords = selectAll();
        
        if (allRecords.isEmpty()) {
            return 0.0;
        }
        
        // 过滤掉通话时长为空的记录，然后计算平均值
        return allRecords.stream()
                .filter(record -> record.getCallDuration() != null)
                .mapToLong(FirstCallSummary::getCallDuration)
                .average()
                .orElse(0.0);
    }
    
    /**
     * 获取时长达标电话总数和电话总数
     * 
     * @return 包含达标电话数和总电话数的数组，[达标数, 总数]
     */
    public long[] getQualifiedAndTotalCount() {
        List<FirstCallSummary> allRecords = selectAll();
        
        long totalCalls = allRecords.size();
        long qualifiedCalls = allRecords.stream()
                .filter(record -> record.getCallDuration() != null && record.getCallDuration() > 600)
                .count();
        
        return new long[]{qualifiedCalls, totalCalls};
    }
}