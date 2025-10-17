package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.SalesRanking;

import java.time.LocalDate;
import java.util.List;

/**
 * 销售排行榜Mapper接口
 * 用于ClickHouse数据库操作
 */
@Mapper
public interface SalesRankingMapper {
    
    /**
     * 查询指定日期所在自然周的销售额排行榜
     * @param date 指定日期
     * @param limit 返回记录数限制
     * @return 销售额排行榜列表
     */
    List<SalesRanking> getWeeklySalesRanking(@Param("date") LocalDate date, @Param("limit") int limit);
    
    /**
     * 查询指定日期所在自然月的销售额排行榜
     * @param date 指定日期
     * @param limit 返回记录数限制
     * @return 销售额排行榜列表
     */
    List<SalesRanking> getMonthlySalesRanking(@Param("date") LocalDate date, @Param("limit") int limit);
}