package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.UserFirstFeedback;
import org.panjy.servicemetricsplatform.service.UserFirstFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user-first-feedback")
public class UserFirstFeedbackController {
    
    @Autowired
    private UserFirstFeedbackService userFirstFeedbackService;
    
    /**
     * 存储用户首次反馈
     * @param feedback 用户首次反馈
     * @return 存储结果
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> saveFeedback(@RequestBody UserFirstFeedback feedback) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int count = userFirstFeedbackService.saveFeedback(feedback);
            
            if (count > 0) {
                response.put("success", true);
                response.put("message", "存储成功");
                response.put("data", feedback);
            } else {
                response.put("success", false);
                response.put("message", "存储失败");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "存储过程中发生错误: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 查询用户首次反馈
     * @param wechatId 微信ID，可选
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getFeedbacks(@RequestParam(required = false) String wechatId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<UserFirstFeedback> feedbacks = userFirstFeedbackService.getFeedbacks(wechatId);
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", feedbacks);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "查询过程中发生错误: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}