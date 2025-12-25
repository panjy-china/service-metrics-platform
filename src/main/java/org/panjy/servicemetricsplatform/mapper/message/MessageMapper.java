package org.panjy.servicemetricsplatform.mapper.message;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.message.Conversation;
import org.panjy.servicemetricsplatform.entity.message.Message;

import java.util.Date;
import java.util.List;

@Mapper
public interface MessageMapper {
    public List<Conversation> findConversationsByDate(@Param("date") Date date);

    public List<Message> findMessagesLikeAddress(@Param("date") Date date);
}