package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.ServerTime;
import org.panjy.servicemetricsplatform.entity.OrderRetentionRate;

import java.time.LocalDate;
import java.util.List;

/**
 * 服务时间Mapper接口
 * 用于ClickHouse数据库操作
 */
@Mapper
public interface ServerTimeMapper {
    
    /**
     * 插入或更新服务时间记录
     * @param serverTime 服务时间对象
     * @return 影响的行数
     */
    int insertOrUpdate(ServerTime serverTime);
    
    /**
     * 批量插入或更新服务时间记录
     * @param serverTimes 服务时间对象列表
     * @return 影响的行数
     */
    int batchInsertOrUpdate(@Param("serverTimes") List<ServerTime> serverTimes);
    
    /**
     * 查询指定日期之后的所有记录
     * @param date 指定日期
     * @return 服务时间记录列表
     */
    List<ServerTime> getServerTimesAfterDate(@Param("date") LocalDate date);
    
    /**
     * 查询所有客户的服务时间记录
     * @return 服务时间记录列表
     */
    List<ServerTime> getAllServerTimes();
    
    /**
     * 根据客户ID查询服务时间
     * @param clientId 客户ID
     * @return 服务时间记录
     */
    ServerTime getByClientId(@Param("clientId") String clientId);
    
    /**
     * 计算推单三日留存率数据
     * @return 留存率数据
     */
    OrderRetentionRate calculateOrderRetentionRate();
}