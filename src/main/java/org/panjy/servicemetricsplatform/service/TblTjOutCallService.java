package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.TblTjOutCall;
import org.panjy.servicemetricsplatform.entity.FirstCallSummary;
import org.panjy.servicemetricsplatform.mapper.TblTjOutCallMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Tbl_Tj_OutCall服务类
 */
@Service
public class TblTjOutCallService {
    
    @Autowired
    private TblTjOutCallMapper tblTjOutCallMapper;
    
    /**
     * 根据用户ID查询符合条件的第一条记录
     * 
     * @param userId 用户ID
     * @return 查询结果
     */
    public TblTjOutCall getFirstRecordByUserId(String userId) {
        return tblTjOutCallMapper.selectFirstByUserId(userId);
    }
    
    /**
     * 查询所有符合条件的客户ID（去重）
     * 
     * @return 客户ID列表
     */
    public List<String> getDistinctClientIds() {
        return tblTjOutCallMapper.selectDistinctClientIds();
    }
    
    /**
     * 处理首通电话数据并插入到汇总表
     * 
     * @return 处理的记录数
     */
    public int processAndInsertFirstCallSummary() {
        // 获取所有客户ID
        List<String> clientIds = getDistinctClientIds();
        
        // 存储处理后的首通电话摘要记录
        List<FirstCallSummary> summaries = new ArrayList<>();
        
        // 遍历每个客户ID，获取其首通电话记录
        for (String clientId : clientIds) {
            TblTjOutCall firstCall = getFirstRecordByUserId(clientId);
            
            // 如果找到了符合条件的首通电话记录，则创建摘要记录
            if (firstCall != null) {
                // 计算通话时长（秒）
                Duration duration = Duration.between(firstCall.getDNANSTIME(), firstCall.getDNOUTTIME());
                long callDuration = duration.getSeconds();
                
                // 创建首通电话摘要记录
                FirstCallSummary summary = new FirstCallSummary(
                    firstCall.getColCltID(),
                    firstCall.getDNINTIME(), // 使用接听时间作为首通日期
                    callDuration
                );
                
                summaries.add(summary);
            }
        }
        
        // 批量插入到数据库
        if (!summaries.isEmpty()) {
            return tblTjOutCallMapper.batchInsertFirstCallSummary(summaries);
        }
        
        return 0;
    }
    
    /**
     * 处理单个客户的首通电话数据并插入到汇总表
     * 
     * @param clientId 客户ID
     * @return 是否处理成功
     */
    public boolean processAndInsertSingleFirstCallSummary(String clientId) {
        TblTjOutCall firstCall = getFirstRecordByUserId(clientId);
        
        // 如果找到了符合条件的首通电话记录，则创建摘要记录并插入
        if (firstCall != null) {
            // 计算通话时长（秒）
            Duration duration = Duration.between(firstCall.getDNANSTIME(), firstCall.getDNOUTTIME());
            long callDuration = duration.getSeconds();
            
            // 创建首通电话摘要记录
            FirstCallSummary summary = new FirstCallSummary(
                firstCall.getColCltID(),
                firstCall.getDNINTIME(), // 使用接听时间作为首通日期
                callDuration
            );
            
            // 插入到数据库
            int result = tblTjOutCallMapper.insertFirstCallSummary(summary);
            return result > 0;
        }
        
        return false;
    }
    
    /**
     * 根据用户ID查询通话次数
     * 
     * @param userId 用户ID
     * @return 通话次数
     */
    public Integer getCallCountByUserId(String userId) {
        return tblTjOutCallMapper.selectCallCountByUserId(userId);
    }
    
    /**
     * 处理所有用户的通话次数并插入到统计表
     * 
     * @return 处理的记录数
     */
    public int processAndInsertUserCallCount() {
        // 获取所有客户ID
        List<String> clientIds = getDistinctClientIds();
        
        // 存储用户通话次数统计
        List<Map<String, Object>> userCallCounts = new ArrayList<>();
        
        // 遍历每个客户ID，获取其通话次数
        for (String clientId : clientIds) {
            Integer callCount = getCallCountByUserId(clientId);
            
            // 如果查询到了通话次数，则添加到列表中
            if (callCount != null) {
                Map<String, Object> userCallCount = new HashMap<>();
                userCallCount.put("colCltID", clientId);
                userCallCount.put("call_count", callCount);
                userCallCounts.add(userCallCount);
            }
        }
        
        // 清空原有数据
        tblTjOutCallMapper.truncateUserCallCount();
        
        // 批量插入到数据库
        if (!userCallCounts.isEmpty()) {
            return tblTjOutCallMapper.batchInsertUserCallCount(userCallCounts);
        }
        
        return 0;
    }
}