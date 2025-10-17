package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.WechatMember;

import java.util.List;

/**
 * 微信会员数据访问接口
 */
@Mapper
public interface WechatMemberMapper {
    
    /**
     * 插入单条记录
     * @param record 要插入的记录
     * @return 受影响的行数
     */
    int insert(WechatMember record);
    
    /**
     * 批量插入记录
     * @param records 要批量插入的记录列表
     * @return 受影响的行数
     */
    int batchInsert(@Param("records") List<WechatMember> records);
    
    /**
     * 根据微信ID查询记录
     * @param wechatId 微信ID
     * @return 查询结果
     */
    WechatMember selectByWechatId(@Param("wechatId") String wechatId);
    
    /**
     * 根据客户ID查询记录
     * @param colCltID 客户ID
     * @return 查询结果列表
     */
    List<WechatMember> selectByColCltID(@Param("colCltID") String colCltID);
    
    /**
     * 查询所有记录
     * @return 查询结果列表
     */
    List<WechatMember> selectAll();
    
    /**
     * 根据微信ID更新记录
     * @param record 要更新的记录
     * @return 受影响的行数
     */
    int updateByWechatId(WechatMember record);
    
    /**
     * 根据微信ID删除记录
     * @param wechatId 微信ID
     * @return 受影响的行数
     */
    int deleteByWechatId(@Param("wechatId") String wechatId);
    
    /**
     * 检查记录是否已存在
     * @param wechatId 微信ID
     * @return 存在的记录数量
     */
    int countByWechatId(@Param("wechatId") String wechatId);
    
    /**
     * 统计不同客户ID的数量
     * @return 不同客户ID的数量
     */
    int countDistinctColCltID();
}