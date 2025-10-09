package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.UserGuidanceStat;

import java.time.LocalDate;
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
    
    /**
     * 根据微信ID和日期查询用户指导统计记录
     * 
     * @param wechatId 微信ID
     * @param createTime 日期
     * @return 查询结果列表
     */
    List<UserGuidanceStat> selectByWechatIdAndDate(@Param("wechatId") String wechatId, @Param("createTime") LocalDate createTime);
    
    /**
     * 插入用户指导统计记录
     * 
     * @param userGuidanceStat 用户指导统计记录
     * @return 受影响的行数
     */
    int insert(UserGuidanceStat userGuidanceStat);
    
    /**
     * 更新用户指导统计记录
     * 
     * @param userGuidanceStat 用户指导统计记录
     * @return 受影响的行数
     */
    int update(UserGuidanceStat userGuidanceStat);
    
    /**
     * 获取总指导次数和总个性化指导次数
     * 
     * @return UserGuidanceStat对象，包含总次数信息
     */
    UserGuidanceStat getTotalGuidanceCounts();
}