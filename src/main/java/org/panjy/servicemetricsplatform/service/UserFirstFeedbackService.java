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
    
    /**
     * 计算客户按要求提交舌苔照片的比例
     * @return 提交舌苔照片的比例
     */
    public double calculateTonguePhotoSubmissionRate() {
        List<UserFirstFeedback> allFeedbacks = userFirstFeedbackMapper.selectAll();
        
        if (allFeedbacks.isEmpty()) {
            return 0.0;
        }
        
        long totalCount = allFeedbacks.size();
        long tonguePhotoCount = allFeedbacks.stream()
                .filter(feedback -> Boolean.TRUE.equals(feedback.getHasTonguePhoto()))
                .count();
        
        return (double) tonguePhotoCount / totalCount;
    }
    
    /**
     * 计算客户按要求提交体型照片的比例
     * @return 提交体型照片的比例
     */
    public double calculateBodyTypePhotoSubmissionRate() {
        List<UserFirstFeedback> allFeedbacks = userFirstFeedbackMapper.selectAll();
        
        if (allFeedbacks.isEmpty()) {
            return 0.0;
        }
        
        long totalCount = allFeedbacks.size();
        long bodyTypePhotoCount = allFeedbacks.stream()
                .filter(feedback -> Boolean.TRUE.equals(feedback.getHasBodyTypePhoto()))
                .count();
        
        return (double) bodyTypePhotoCount / totalCount;
    }
    
    /**
     * 获取舌苔照片提交统计信息
     * @return 包含提交数量和总数量的数组，[提交数, 总数]
     */
    public long[] getTonguePhotoSubmissionStats() {
        List<UserFirstFeedback> allFeedbacks = userFirstFeedbackMapper.selectAll();
        
        long totalCount = allFeedbacks.size();
        long tonguePhotoCount = allFeedbacks.stream()
                .filter(feedback -> Boolean.TRUE.equals(feedback.getHasTonguePhoto()))
                .count();
        
        return new long[]{tonguePhotoCount, totalCount};
    }
    
    /**
     * 获取体型照片提交统计信息
     * @return 包含提交数量和总数量的数组，[提交数, 总数]
     */
    public long[] getBodyTypePhotoSubmissionStats() {
        List<UserFirstFeedback> allFeedbacks = userFirstFeedbackMapper.selectAll();
        
        long totalCount = allFeedbacks.size();
        long bodyTypePhotoCount = allFeedbacks.stream()
                .filter(feedback -> Boolean.TRUE.equals(feedback.getHasBodyTypePhoto()))
                .count();
        
        return new long[]{bodyTypePhotoCount, totalCount};
    }
}