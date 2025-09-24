package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.Conversation;

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
}