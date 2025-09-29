package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.UserFirstFeedback;
import org.panjy.servicemetricsplatform.mapper.UserFirstFeedbackMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFirstFeedbackService {
    
    @Autowired
    private UserFirstFeedbackMapper userFirstFeedbackMapper;
    
    /**
     * 存储用户首次反馈
     * @param feedback 用户首次反馈
     * @return 存储的记录数
     */
    public int saveFeedback(UserFirstFeedback feedback) {
        return userFirstFeedbackMapper.insert(feedback);
    }
    
    /**
     * 查询用户首次反馈
     * @param wechatId 微信ID，可为空
     * @return 用户首次反馈列表
     */
    public List<UserFirstFeedback> getFeedbacks(String wechatId) {
        return userFirstFeedbackMapper.selectBatch(wechatId);
    }
}