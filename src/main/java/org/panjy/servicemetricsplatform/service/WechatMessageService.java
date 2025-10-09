package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.Message;
import org.panjy.servicemetricsplatform.mapper.WechatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WechatMessageService {
    
    @Autowired
    private WechatMessageMapper wechatMessageMapper;
    
    /**
     * 根据微信ID查询用户最早的一条消息记录
     * 
     * @param wechatId 用户微信ID
     * @return 最早的消息记录，如果未找到则返回null
     */
    public Message findEarliestMessageByWechatId(String wechatId) {
        if (wechatId == null || wechatId.isEmpty()) {
            throw new IllegalArgumentException("微信ID不能为空");
        }
        
        try {
            return wechatMessageMapper.findEarliestMessageByWechatId(wechatId);
        } catch (Exception e) {
            System.err.println("根据微信ID查询用户最早消息记录时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 根据微信ID查询用户最晚的一条消息记录
     * 
     * @param wechatId 用户微信ID
     * @return 最晚的消息记录，如果未找到则返回null
     */
    public Message findLatestMessageByWechatId(String wechatId) {
        if (wechatId == null || wechatId.isEmpty()) {
            throw new IllegalArgumentException("微信ID不能为空");
        }
        
        try {
            return wechatMessageMapper.findLatestMessageByWechatId(wechatId);
        } catch (Exception e) {
            System.err.println("根据微信ID查询用户最晚消息记录时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}