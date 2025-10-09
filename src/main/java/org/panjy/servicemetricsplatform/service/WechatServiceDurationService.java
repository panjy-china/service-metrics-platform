package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.Message;
import org.panjy.servicemetricsplatform.mapper.WechatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 微信服务时长服务类
 * 用于计算用户总服务天数
 */
@Service
public class WechatServiceDurationService {
    
    @Autowired
    private WechatMessageMapper wechatMessageMapper;
    
    /**
     * 根据微信ID计算用户的总服务天数
     * 总服务天数 = 最晚消息日期 - 最早消息日期
     * 
     * @param wechatId 用户微信ID
     * @return 总服务天数，如果计算失败则返回-1
     */
    public long calculateTotalServiceDays(String wechatId) {
        if (wechatId == null || wechatId.isEmpty()) {
            throw new IllegalArgumentException("微信ID不能为空");
        }
        
        try {
            // 获取用户最早的消息记录
            Message earliestMessage = wechatMessageMapper.findEarliestMessageByWechatId(wechatId);
            if (earliestMessage == null) {
                System.err.println("未找到用户最早的微信消息记录: " + wechatId);
                return -1;
            }
            
            // 获取用户最晚的消息记录
            Message latestMessage = wechatMessageMapper.findLatestMessageByWechatId(wechatId);
            if (latestMessage == null) {
                System.err.println("未找到用户最晚的微信消息记录: " + wechatId);
                return -1;
            }
            
            // 获取消息时间
            LocalDateTime earliestTime = earliestMessage.getChatTime();
            LocalDateTime latestTime = latestMessage.getChatTime();
            
            if (earliestTime == null || latestTime == null) {
                System.err.println("消息时间为空，无法计算服务天数");
                return -1;
            }
            
            // 计算两个日期之间的天数差
            return ChronoUnit.DAYS.between(earliestTime, latestTime);
            
        } catch (Exception e) {
            System.err.println("计算用户总服务天数时发生错误: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * 根据微信ID计算用户的总服务时长（精确到小时）
     * 
     * @param wechatId 用户微信ID
     * @return 总服务时长（小时），如果计算失败则返回-1
     */
    public long calculateTotalServiceHours(String wechatId) {
        if (wechatId == null || wechatId.isEmpty()) {
            throw new IllegalArgumentException("微信ID不能为空");
        }
        
        try {
            // 获取用户最早的消息记录
            Message earliestMessage = wechatMessageMapper.findEarliestMessageByWechatId(wechatId);
            if (earliestMessage == null) {
                System.err.println("未找到用户最早的微信消息记录: " + wechatId);
                return -1;
            }
            
            // 获取用户最晚的消息记录
            Message latestMessage = wechatMessageMapper.findLatestMessageByWechatId(wechatId);
            if (latestMessage == null) {
                System.err.println("未找到用户最晚的微信消息记录: " + wechatId);
                return -1;
            }
            
            // 获取消息时间
            LocalDateTime earliestTime = earliestMessage.getChatTime();
            LocalDateTime latestTime = latestMessage.getChatTime();
            
            if (earliestTime == null || latestTime == null) {
                System.err.println("消息时间为空，无法计算服务时长");
                return -1;
            }
            
            // 计算两个日期之间的小时差
            return ChronoUnit.HOURS.between(earliestTime, latestTime);
            
        } catch (Exception e) {
            System.err.println("计算用户总服务时长时发生错误: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
}