package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.mapper.TblTjOutCallMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通话次数达标率服务类
 * 用于计算四次通话达标率和六次通话达标率
 */
@Service
public class CallCountComplianceRateService {
    
    @Autowired
    private TblTjOutCallMapper tblTjOutCallMapper;
    
    /**
     * 计算四次通话达标率
     * 达标率 = 通话次数>=4的记录数 / 总记录数 * 100%
     * 
     * @return 四次通话达标率（百分比）
     */
    public double calculateFourCallComplianceRate() {
        // 获取所有用户的通话次数列表
        List<Integer> callCounts = tblTjOutCallMapper.selectAllCallCounts();
        
        if (callCounts.isEmpty()) {
            return 0.0;
        }
        
        // 计算总记录数
        int totalCount = callCounts.size();
        
        // 计算通话次数>=4的记录数
        long compliantCount = callCounts.stream()
                .filter(count -> count >= 4)
                .count();
        
        // 计算达标率（百分比）
        return totalCount > 0 ? (double) compliantCount / totalCount * 100 : 0.0;
    }
    
    /**
     * 计算六次通话达标率
     * 达标率 = 通话次数>=6的记录数 / 总记录数 * 100%
     * 
     * @return 六次通话达标率（百分比）
     */
    public double calculateSixCallComplianceRate() {
        // 获取所有用户的通话次数列表
        List<Integer> callCounts = tblTjOutCallMapper.selectAllCallCounts();
        
        if (callCounts.isEmpty()) {
            return 0.0;
        }
        
        // 计算总记录数
        int totalCount = callCounts.size();
        
        // 计算通话次数>=6的记录数
        long compliantCount = callCounts.stream()
                .filter(count -> count >= 6)
                .count();
        
        // 计算达标率（百分比）
        return totalCount > 0 ? (double) compliantCount / totalCount * 100 : 0.0;
    }
    
    /**
     * 获取通话次数统计信息
     * 
     * @return 包含总记录数、四次通话达标数、六次通话达标数的数组
     */
    public long[] getCallCountStatistics() {
        // 获取所有用户的通话次数列表
        List<Integer> callCounts = tblTjOutCallMapper.selectAllCallCounts();
        
        if (callCounts.isEmpty()) {
            return new long[]{0, 0, 0}; // totalCount, fourCompliantCount, sixCompliantCount
        }
        
        // 计算总记录数
        long totalCount = callCounts.size();
        
        // 计算通话次数>=4的记录数
        long fourCompliantCount = callCounts.stream()
                .filter(count -> count >= 4)
                .count();
        
        // 计算通话次数>=6的记录数
        long sixCompliantCount = callCounts.stream()
                .filter(count -> count >= 6)
                .count();
        
        return new long[]{totalCount, fourCompliantCount, sixCompliantCount};
    }
}