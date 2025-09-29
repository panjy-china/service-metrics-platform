package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.UserGuidanceStat;

import java.util.List;

/**
 * UserGuidanceStat数据访问接口
 * 提供对aikang.user_guidance_stat表的批量查询与批量插入操作
 */
@Mapper
public interface UserGuidanceStatMapper {
    
    /**
     * 批量查询用户指导统计记录
     * 
     * @param userGuidanceStats 用户指导统计记录列表
     * @return 查询结果列表
     */
    List<UserGuidanceStat> batchSelect(@Param("userGuidanceStats") List<UserGuidanceStat> userGuidanceStats);
    
    /**
     * 批量插入用户指导统计记录
     * 
     * @param userGuidanceStats 用户指导统计记录列表
     * @return 受影响的行数
     */
    int batchInsert(@Param("userGuidanceStats") List<UserGuidanceStat> userGuidanceStats);
}