package org.panjy.servicemetricsplatform.mapper.conversionrate;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.conversionrate.ClientServiceStats;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户服务统计Mapper接口
 * 用于ClickHouse数据库操作
 */
@Mapper
public interface ClientServiceStatsMapper {
    
    /**
     * 查询满足条件的客户服务统计数据
     * 条件：
     * 1. 服务时间超过10天（864000秒）
     * 2. 服务员工数量大于等于3人
     * 3. 按服务天数降序排列
     * 
     * @return 客户服务统计数据列表
     */
    List<ClientServiceStats> getClientServiceStats();
    
    /**
     * 查询指定月份满足条件的客户服务统计数据
     * 条件：
     * 1. 服务时间超过10天（864000秒）
     * 2. 服务员工数量大于等于3人
     * 3. 按服务天数降序排列
     * 
     * @param targetDate 目标日期
     * @return 客户服务统计数据列表
     */
    List<ClientServiceStats> getClientServiceStatsByMonth(@Param("targetDate") LocalDateTime targetDate);
}