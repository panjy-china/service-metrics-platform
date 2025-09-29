package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.TblTjOutCall;
import org.panjy.servicemetricsplatform.entity.FirstCallSummary;

import java.util.List;
import java.util.Map;

/**
 * Tbl_Tj_OutCall数据访问接口
 */
@Mapper
public interface TblTjOutCallMapper {
    
    /**
     * 根据用户ID查询符合条件的第一条记录
     * 对应SQL:
     * SELECT *
     * FROM aikang.Tbl_Tj_OutCall
     * WHERE aikang.Tbl_Tj_OutCall.colCltID = #{userId}
     *   AND DNISANS = 1
     *   AND dateDiff('second', DNANSTIME, DNOUTTIME) > 60
     * ORDER BY DNINTIME ASC
     * LIMIT 1;
     * 
     * @param userId 用户ID
     * @return 查询结果
     */
    TblTjOutCall selectFirstByUserId(@Param("userId") String userId);
    
    /**
     * 查询所有符合条件的客户ID（去重）
     * 对应SQL:
     * SELECT DISTINCT twm.colCltID 
     * FROM aikang.wechat_member wm
     * INNER JOIN aikang.tbl_wechat_member twm
     *     ON wm.wechat_id = twm.wechat_id;
     * 
     * @return 客户ID列表
     */
    List<String> selectDistinctClientIds();
    
    /**
     * 插入首通电话摘要记录
     * 
     * @param firstCallSummary 首通电话摘要记录
     * @return 受影响的行数
     */
    int insertFirstCallSummary(@Param("firstCallSummary") FirstCallSummary firstCallSummary);
    
    /**
     * 批量插入首通电话摘要记录
     * 
     * @param firstCallSummaries 首通电话摘要记录列表
     * @return 受影响的行数
     */
    int batchInsertFirstCallSummary(@Param("firstCallSummaries") List<FirstCallSummary> firstCallSummaries);
    
    /**
     * 根据用户ID查询通话次数
     * 对应SQL:
     * SELECT 
     *     count(*) AS call_count
     * FROM aikang.Tbl_Tj_OutCall
     * WHERE colCltID = '{userId}'               
     *   AND DNISANS = 1                         
     *   AND (DNOUTTIME - DNANSTIME) > 60;
     * 
     * @param userId 用户ID
     * @return 通话次数
     */
    Integer selectCallCountByUserId(@Param("userId") String userId);
    
    /**
     * 批量插入用户通话次数统计
     * 
     * @param userCallCounts 用户通话次数统计列表
     * @return 受影响的行数
     */
    int batchInsertUserCallCount(@Param("userCallCounts") List<Map<String, Object>> userCallCounts);
    
    /**
     * 清空用户通话次数统计表
     * 
     * @return 受影响的行数
     */
    int truncateUserCallCount();
}