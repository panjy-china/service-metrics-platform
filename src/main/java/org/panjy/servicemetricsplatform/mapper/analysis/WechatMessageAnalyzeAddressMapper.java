package org.panjy.servicemetricsplatform.mapper.analysis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.analysis.WechatMessageAnalyzeAddress;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 微信消息地址分析结果数据访问接口
 */
@Mapper
public interface WechatMessageAnalyzeAddressMapper {
    
    /**
     * 插入单条记录
     * @param record 要插入的记录
     * @return 受影响的行数
     */
    int insert(WechatMessageAnalyzeAddress record);
    
    /**
     * 批量插入记录
     * @param records 要批量插入的记录列表
     * @return 受影响的行数
     */
    int batchInsert(@Param("records") List<WechatMessageAnalyzeAddress> records);
    
    /**
     * 根据微信ID和时间查询记录
     * @param wechatId 微信ID
     * @param wechatTime 微信消息时间
     * @return 查询结果
     */
    WechatMessageAnalyzeAddress selectByWechatIdAndTime(@Param("wechatId") String wechatId, @Param("wechatTime") Long wechatTime);
    
    /**
     * 根据微信ID查询记录
     * @param wechatId 微信号
     * @return 查询结果列表
     */
    List<WechatMessageAnalyzeAddress> selectByWechatId(@Param("wechatId") String wechatId);
    
    /**
     * 根据时间范围查询记录
     * @param startTime 开始时间戳（毫秒）
     * @param endTime 结束时间戳（毫秒）
     * @return 查询结果列表
     */
    List<WechatMessageAnalyzeAddress> selectByTimeRange(@Param("startTime") Long startTime, 
                                                        @Param("endTime") Long endTime);
    
    /**
     * 根据微信ID和时间范围查询记录
     * @param wechatId 微信号
     * @param startTime 开始时间戳（毫秒）
     * @param endTime 结束时间戳（毫秒）
     * @return 查询结果列表
     */
    List<WechatMessageAnalyzeAddress> selectByWechatIdAndTimeRange(@Param("wechatId") String wechatId,
                                                                   @Param("startTime") Long startTime,
                                                                   @Param("endTime") Long endTime);
    
    /**
     * 查询昨天新增的记录
     * @param yesterday 昨天的日期
     * @return 查询结果列表
     */
    List<WechatMessageAnalyzeAddress> selectByYesterday(@Param("yesterday") Date yesterday);
    
    /**
     * 根据微信ID和时间更新记录
     * @param record 要更新的记录
     * @return 受影响的行数
     */
    int updateByWechatIdAndTime(WechatMessageAnalyzeAddress record);
    
    /**
     * 根据微信ID和时间删除记录
     * @param wechatId 微信ID
     * @param wechatTime 微信消息时间
     * @return 受影响的行数
     */
    int deleteByWechatIdAndTime(@Param("wechatId") String wechatId, @Param("wechatTime") Long wechatTime);
    
    /**
     * 检查记录是否已存在
     * @param wechatId 微信ID
     * @param wechatTime 微信消息时间
     * @return 存在的记录数量
     */
    int countByWechatIdAndTime(@Param("wechatId") String wechatId, @Param("wechatTime") Long wechatTime);
    
    /**
     * 查询所有记录（分页）
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 查询结果列表
     */
    List<WechatMessageAnalyzeAddress> selectAll(@Param("offset") Integer offset, 
                                                @Param("limit") Integer limit);
    
    /**
     * 查询记录总数
     * @return 记录总数
     */
    Long countAll();
    
    /**
     * 查询每个用户的最新地址（排除北京大兴）
     * @return 用户地址列表
     */
    List<String> getUserLatestAddresses();
    
    /**
     * 获取所有处理后的地址
     * @return 处理后的地址列表
     */
    List<String> getAllProcessedAddresses();
    
    /**
     * 获取所有处理后的地址及其对应的微信ID
     * @return 包含微信ID和处理后地址的映射列表
     */
    List<Map<String, String>> getAllProcessedAddressWithWechatId();
    
    /**
     * 获取在指定日期首次出现的微信用户数（新增人数）
     * @param date 指定日期
     * @return 新增用户数
     */
    int countNewUsersByDate(@Param("date") String date);
    
    /**
     * 获取在指定日期范围内发送包含地址信息消息的微信用户数（成交人数）
     * @param startDate 开始日期
     * @param days 天数范围
     * @return 成交用户数
     */
    int countDealUsersByDateRange(@Param("startDate") String startDate, @Param("days") int days);

}