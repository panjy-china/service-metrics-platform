package org.panjy.servicemetricsplatform.mapper.order;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 销售个数统计Mapper接口
 * 用于ClickHouse数据库操作
 */
@Mapper
public interface SalesCountMapper {
    
    /**
     * 动态查询符合条件的销售人员个数
     * @param empIdPrefix 员工ID前缀条件，默认为'E%'
     * @return 符合条件的销售人员个数
     */
    Long getSalesCount(@Param("empIdPrefix") String empIdPrefix);
}