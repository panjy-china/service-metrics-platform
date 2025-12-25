package org.panjy.servicemetricsplatform.mapper.call;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.panjy.servicemetricsplatform.entity.call.FirstCallRecord;
import org.panjy.servicemetricsplatform.entity.call.FirstCallSummary;

import java.time.LocalDateTime;
import java.util.List;

/**
 * FirstCallSummary数据访问接口
 * 提供对aikang.tbl_first_call_summary表的批量查询操作
 */
@Mapper
public interface FirstCallSummaryMapper {
    
    /**
     * 批量查询首通电话摘要记录
     * 
     * @param firstCallSummaries 首通电话摘要记录列表
     * @return 查询结果列表
     */
    List<FirstCallSummary> batchSelect(@Param("firstCallSummaries") List<FirstCallSummary> firstCallSummaries);
    
    /**
     * 查询所有首通电话摘要记录
     * 
     * @return 所有首通电话摘要记录列表
     */
    List<FirstCallSummary> selectAll();
    
    /**
     * 根据微信ID查询首通记录
     * 
     * @param wechatID 微信ID
     * @return 首通记录列表
     */
    @Select({
        "SELECT",
        "    w.wechat_id AS wechatId,",
        "    f.colCltID,",
        "    f.first_call_date AS firstCallDate,",
        "    f.call_duration AS callDuration",
        "FROM aikang.tbl_wechat_member w",
        "INNER JOIN aikang.tbl_first_call_summary f",
        "    ON w.colCltID = f.colCltID",
        "WHERE w.wechat_id = #{wechatID}"
    })
    List<FirstCallRecord> selectFirstCallRecordsByWechatId(@Param("wechatID") String wechatID);
    
    /**
     * 查询指定日期所在自然月的首通记录
     * 
     * @param date 指定日期
     * @return 首通记录列表
     */
    List<FirstCallSummary> selectByMonth(@Param("date") LocalDateTime date);
}