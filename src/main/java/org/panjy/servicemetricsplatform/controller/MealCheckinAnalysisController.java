package org.panjy.servicemetricsplatform.controller;


import org.panjy.servicemetricsplatform.entity.mealcomletion.UserMealCheckin;
import org.panjy.servicemetricsplatform.entity.message.Conversation;
import org.panjy.servicemetricsplatform.service.UserMealCheckinService;
import org.panjy.servicemetricsplatform.service.analysis.LLMAnalysisService;
import org.panjy.servicemetricsplatform.service.newuser.StrategicLayerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 三餐打卡与体重反馈分析控制器
 * 提供对用户餐食打卡记录的分析和存储功能
 */
@RestController
@RequestMapping("/api/meal-checkin")
public class MealCheckinAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(MealCheckinAnalysisController.class);

    @Autowired
    private StrategicLayerService strategicLayerService;

    @Autowired
    private LLMAnalysisService llmAnalysisService;

    @Autowired
    private UserMealCheckinService userMealCheckinService;

    /**
     * 分析指定日期之后的用户对话记录，并将结果存储到数据库
     *
     * @param date 开始日期字符串 (格式: yyyy-MM-dd)
     * @return 分析结果统计信息
     */
    @PostMapping("/analyze-and-store/{date}")
    public ApiResponse analyzeAndStoreMealCheckin(@PathVariable String date) {
        logger.info("开始分析指定日期之后的用户对话记录，开始日期: {}", date);

        try {
            // 解析日期参数
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date startDate = sdf.parse(date);

            // 查询对话记录
            List<Conversation> conversations = strategicLayerService.findConversationsByDate(startDate);
            logger.info("查询到 {} 条对话记录", conversations.size());

            if (conversations.isEmpty()) {
                return ApiResponse.success("没有找到符合条件的对话记录", new AnalysisStats(0, 0, 0));
            }

            // 分析并存储结果
            AnalysisStats stats = analyzeAndStoreConversations(conversations);

            return ApiResponse.success("分析完成", stats);
        } catch (ParseException e) {
            logger.error("日期格式错误: {}", date, e);
            return ApiResponse.error("日期格式错误，请使用 yyyy-MM-dd 格式");
        } catch (Exception e) {
            logger.error("分析过程中发生错误", e);
            return ApiResponse.error("分析过程中发生错误: " + e.getMessage());
        }
    }

    /**
     * 分析对话记录并存储结果
     *
     * @param conversations 对话记录列表
     * @return 分析统计信息
     */
    private AnalysisStats analyzeAndStoreConversations(List<Conversation> conversations) {
        int successCount = 0;
        int failedCount = 0;
        int insertedCount = 0;

        for (Conversation conversation : conversations) {
            try {
                logger.info("开始分析用户对话记录，用户微信ID: {}", conversation.getWechatId());

                // 使用大模型分析三餐打卡与体重反馈情况
                UserMealCheckin mealCheckin = llmAnalysisService.analyzeMealCheckinAndWeightFeedback(conversation);

                if (mealCheckin != null) {
                    // 处理一次对话就执行一次插入
                    try {
                        List<UserMealCheckin> singleCheckinList = new ArrayList<>();
                        singleCheckinList.add(mealCheckin);
                        int inserted = userMealCheckinService.batchInsert(singleCheckinList);
                        insertedCount += inserted;
                        
                        successCount++;
                        logger.info("用户 {} 分析成功并插入数据库，早餐: {}，午餐: {}，晚餐: {}，体重反馈: {}",
                                conversation.getWechatId(),
                                mealCheckin.getBreakfastChecked(),
                                mealCheckin.getLunchChecked(),
                                mealCheckin.getDinnerChecked(),
                                mealCheckin.getHasWeightFeedback());
                    } catch (Exception insertException) {
                        logger.error("插入用户 {} 的分析结果到数据库时发生错误", conversation.getWechatId(), insertException);
                        failedCount++;
                    }
                } else {
                    failedCount++;
                    logger.warn("用户 {} 分析失败", conversation.getWechatId());
                }

                // 为了避免API限流，添加短暂延迟
                Thread.sleep(200);
            } catch (Exception e) {
                failedCount++;
                logger.error("分析用户 {} 时发生错误", conversation.getWechatId(), e);
            }
        }

        return new AnalysisStats(conversations.size(), successCount, failedCount, insertedCount);
    }

    /**
     * 分析统计信息类
     */
    public static class AnalysisStats {
        private int totalConversations;
        private int successCount;
        private int failedCount;
        private int insertedCount;

        public AnalysisStats() {
        }

        public AnalysisStats(int totalConversations, int successCount, int failedCount) {
            this.totalConversations = totalConversations;
            this.successCount = successCount;
            this.failedCount = failedCount;
        }

        public AnalysisStats(int totalConversations, int successCount, int failedCount, int insertedCount) {
            this.totalConversations = totalConversations;
            this.successCount = successCount;
            this.failedCount = failedCount;
            this.insertedCount = insertedCount;
        }

        // Getter和Setter方法
        public int getTotalConversations() {
            return totalConversations;
        }

        public void setTotalConversations(int totalConversations) {
            this.totalConversations = totalConversations;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public int getFailedCount() {
            return failedCount;
        }

        public void setFailedCount(int failedCount) {
            this.failedCount = failedCount;
        }

        public int getInsertedCount() {
            return insertedCount;
        }

        public void setInsertedCount(int insertedCount) {
            this.insertedCount = insertedCount;
        }

        @Override
        public String toString() {
            return "AnalysisStats{" +
                    "totalConversations=" + totalConversations +
                    ", successCount=" + successCount +
                    ", failedCount=" + failedCount +
                    ", insertedCount=" + insertedCount +
                    '}';
        }
    }

    /**
     * API响应封装类
     */
    public static class ApiResponse {
        private boolean success;
        private String message;
        private Object data;

        public ApiResponse() {
        }

        public ApiResponse(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public static ApiResponse success(String message, Object data) {
            return new ApiResponse(true, message, data);
        }

        public static ApiResponse error(String message) {
            return new ApiResponse(false, message, null);
        }

        // Getter和Setter方法
        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }
}