package org.panjy.servicemetricsplatform.service.message;

import org.panjy.servicemetricsplatform.entity.message.UserGuidanceStat;
import org.panjy.servicemetricsplatform.mapper.message.UserGuidanceStatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    
    /**
     * 保存单个用户指导统计记录
     * 
     * @param userGuidanceStat 用户指导统计记录
     * @return 受影响的行数
     */
    public int save(UserGuidanceStat userGuidanceStat) {
        // 检查是否已存在同一天的记录
        List<UserGuidanceStat> existingRecords = userGuidanceStatMapper.selectByWechatIdAndDate(
            userGuidanceStat.getWechatId(), userGuidanceStat.getCreateTime());
        
        if (existingRecords != null && !existingRecords.isEmpty()) {
            // 如果存在，更新现有记录
            UserGuidanceStat existing = existingRecords.get(0);
            existing.setGuidanceCount(userGuidanceStat.getGuidanceCount());
            existing.setPersonalizedGuidanceCount(userGuidanceStat.getPersonalizedGuidanceCount());
            return userGuidanceStatMapper.update(existing);
        } else {
            // 如果不存在，插入新记录
            return userGuidanceStatMapper.insert(userGuidanceStat);
        }
    }
    
    /**
     * 计算总的饮食指导触达率
     * 公式：(个性化指导总次数 / 总指导次数) × 100%
     * 
     * @return 饮食指导触达率
     */
    public BigDecimal calculateTotalGuidanceReachRate() {
        // 获取总指导次数和总个性化指导次数
        UserGuidanceStat totalStats = userGuidanceStatMapper.getTotalGuidanceCounts();
        
        if (totalStats != null) {
            Integer totalCount = totalStats.getGuidanceCount();
            Integer personalizedCount = totalStats.getPersonalizedGuidanceCount();
            
            // 避免除零错误
            if (totalCount != null && totalCount > 0) {
                // 计算触达率并保留两位小数
                return new BigDecimal(personalizedCount * 100)
                    .divide(new BigDecimal(totalCount), 2, BigDecimal.ROUND_HALF_UP);
            }
        }
        
        // 如果没有数据或总次数为0，返回0
        return BigDecimal.ZERO;
    }
    
    /**
     * 计算指定月份的饮食指导触达率
     * 公式：(个性化指导总次数 / 总指导次数) × 100%
     * 
     * @param targetDate 目标日期
     * @return 指定月份的饮食指导触达率
     */
    public BigDecimal calculateTotalGuidanceReachRateByMonth(LocalDateTime targetDate) {
        // 获取指定月份的总指导次数和总个性化指导次数
        UserGuidanceStat totalStats = userGuidanceStatMapper.getTotalGuidanceCountsByMonth(targetDate);
        
        if (totalStats != null) {
            Integer totalCount = totalStats.getGuidanceCount();
            Integer personalizedCount = totalStats.getPersonalizedGuidanceCount();
            
            // 避免除零错误
            if (totalCount != null && totalCount > 0) {
                // 计算触达率并保留两位小数
                return new BigDecimal(personalizedCount * 100)
                    .divide(new BigDecimal(totalCount), 2, BigDecimal.ROUND_HALF_UP);
            }
        }
        
        // 如果没有数据或总次数为0，返回0
        return BigDecimal.ZERO;
    }
    
    /**
     * 计算指定月份的饮食指导触达率及环比增长率
     * 
     * @param targetDate 目标日期
     * @return 包含当月饮食指导触达率和环比增长率的Map
     */
    public Map<String, Object> calculateGuidanceReachRateWithGrowth(LocalDateTime targetDate) {
        // 计算目标月份的饮食指导触达率
        BigDecimal currentRate = calculateTotalGuidanceReachRateByMonth(targetDate);
        
        // 计算上个月的饮食指导触达率
        LocalDateTime previousMonth = targetDate.minusMonths(1);
        BigDecimal previousRate = calculateTotalGuidanceReachRateByMonth(previousMonth);
        
        // 计算环比增长率
        BigDecimal growthRate = BigDecimal.ZERO;
        if (previousRate.compareTo(BigDecimal.ZERO) != 0) {
            growthRate = currentRate.subtract(previousRate)
                .multiply(BigDecimal.valueOf(100))
                .divide(previousRate, 2, BigDecimal.ROUND_HALF_UP);
        }
        
        // 构造返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("currentRate", currentRate.toString());  // 不带%的百分比
        result.put("growthRate", growthRate.toString());  // 环比增长率，不带%的百分比
        
        return result;
    }
}