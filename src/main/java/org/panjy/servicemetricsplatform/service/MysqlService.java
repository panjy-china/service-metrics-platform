package org.panjy.servicemetricsplatform.service;

import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.Message;
import org.panjy.servicemetricsplatform.mapper.mysql.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MysqlService {
    @Autowired
    private MessageMapper messageMapper;

    /*
    *查询模糊地址，使用模型分辨出真实地址
    */
    public List<Message> findMessagesLikeAddress(@Param("date") Date date) {
        List<Message> messages = messageMapper.findMessagesLikeAddress(date);
        return messages;
    }
}
