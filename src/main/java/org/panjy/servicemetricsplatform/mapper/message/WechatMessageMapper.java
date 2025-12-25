package org.panjy.servicemetricsplatform.mapper.message;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.message.Conversation;
import org.panjy.servicemetricsplatform.entity.message.Message;

import java.util.Date;
import java.util.List;

@Mapper
public interface WechatMessageMapper {

    List<String> findMessageByContent(@Param("content") String content, @Param("begin")Date begin, @Param("end")Date end);

    List<String> findActiveUsers(@Param("checkTime")Date checkTime);

    List<String> findUserServived(@Param("checkTime")Date checkTime, @Param("days") int days);

    List<String> findInactiveUsers(@Param("startTime")Date startTime);

    List<Conversation> findConversationsByDate(@Param("begin")Date begin, @Param("end")Date end);

    /**
     * 查询每个用户在给定日期之后首次聊天起 1 小时内的对话内容
     */
    List<Conversation> findFirstHourConversationsAfterDate(@Param("date") Date date);

    /**
     * 查询每个用户首次聊天两天内的对话记录
     */
    List<Conversation> findConversationsWithinTwoDaysOfFirstChat();

    /**
     * 查询每个用户首次聊天两天内的对话记录，限定首次聊天的日期在传入日期之后
     * @param date 限定日期
     * @return 对话记录列表
     */
    List<Conversation> findConversationsWithinTwoDaysOfFirstChatAfterDate(@Param("date") Date date);

    /**
     * 查询指定用户首次聊天两天内的对话记录
     * @param wechatId 用户微信ID
     * @return 对话记录
     */
    Conversation findConversationsWithinTwoDaysOfFirstChatByWechatId(@Param("wechatId") String wechatId);
    
    /**
     * 根据微信ID查询用户最早的一条消息记录
     * @param wechatId 用户微信ID
     * @return 最早的消息记录
     */
    Message findEarliestMessageByWechatId(@Param("wechatId") String wechatId);
    
    /**
     * 根据微信ID查询用户最晚的一条消息记录
     * @param wechatId 用户微信ID
     * @return 最晚的消息记录
     */
    Message findLatestMessageByWechatId(@Param("wechatId") String wechatId);
    
    /**
     * 查找包含地址信息的微信用户ID
     * @param startDate 开始日期
     * @return 包含地址信息的微信用户ID列表
     */
    List<String> findWechatUserIdsWithAddressInfo(@Param("startDate") Date startDate);
    
    /**
     * 根据微信ID查询用户的对话记录
     * @param wechatId 用户微信ID
     * @return 对话记录
     */
    Conversation findConversationByWechatId(@Param("wechatId") String wechatId);
}