package org.panjy.servicemetricsplatform.mapper.mysql;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.Conversation;
import org.panjy.servicemetricsplatform.entity.Message;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface MessageMapper {
    public List<Conversation> findConversationsByDate(@Param("date") Date date);

    public List<Message> findMessagesLikeAddress(@Param("date") Date date);
}
