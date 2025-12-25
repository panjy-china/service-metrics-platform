package org.panjy.servicemetricsplatform.task;

import org.panjy.servicemetricsplatform.entity.message.Conversation;
import org.panjy.servicemetricsplatform.entity.label.WechatUserLabel;
import org.panjy.servicemetricsplatform.service.message.WechatMessageService;
import org.panjy.servicemetricsplatform.service.analysis.HealthProfileAnalysisService;
import org.panjy.servicemetricsplatform.mapper.label.WechatUserLabelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * 健康画像分析定时任务
 * 每日凌晨3点执行，分析昨日对话记录中的健康相关信息
 */
@Component
public class HealthProfileAnalysisScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(HealthProfileAnalysisScheduledTask.class);

    @Autowired
    private WechatMessageService wechatMessageService;

    @Autowired
    private HealthProfileAnalysisService healthProfileAnalysisService;

    @Autowired
    private WechatUserLabelMapper wechatUserLabelMapper;

    /**
     * 每日凌晨3点执行健康画像分析任务
     * 查询昨日的对话记录，分析健康相关标签并保存到数据库
     */
    @Scheduled(cron = "0 0 3 * * ?") // 每天凌晨3点执行
    public void executeHealthProfileAnalysisTask() {
        logger.info("开始执行健康画像分析定时任务");

        try {
            // 计算昨日日期
            LocalDate yesterday = LocalDate.now().minusDays(1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String yesterdayStr = yesterday.format(formatter);
            Date yesterdayDate = java.sql.Date.valueOf(yesterday);

            logger.info("查询日期: {}", yesterdayStr);

            // 查询昨日包含地址信息的对话记录
            List<Conversation> conversations = wechatMessageService.findConversationsWithAddressInfoByDate(yesterdayDate);

            logger.info("查询到 {} 条对话记录", conversations.size());

            // 遍历对话记录，分析健康画像标签并插入数据库
            for (Conversation conversation : conversations) {
                try {
                    logger.debug("正在分析用户 {} 的对话记录", conversation.getWechatId());

                    // 使用HealthProfileAnalysisService分析健康画像标签
                    WechatUserLabel userLabel = healthProfileAnalysisService.extractHealthProfileTags(conversation);

                    if (userLabel != null) {
                        // 如果WechatUserLabel中没有设置销售账号ID，尝试从对话中获取
                        // 这里假设conversation中可能包含销售账号ID的信息
                        // 如果conversation对象结构中包含销售账号ID，可以从那里获取
                        // 否则，保持现有的空字符串或设置默认值
                        
                        // 插入数据库
                        int result = wechatUserLabelMapper.insert(userLabel);
                        
                        if (result > 0) {
                            logger.debug("成功插入用户 {} 的健康画像标签", conversation.getWechatId());
                        } else {
                            logger.warn("插入用户 {} 的健康画像标签失败", conversation.getWechatId());
                        }
                    } else {
                        logger.warn("用户 {} 的健康画像分析结果为空", conversation.getWechatId());
                    }
                } catch (Exception e) {
                    logger.error("分析用户 {} 的对话记录时发生错误: {}", conversation.getWechatId(), e.getMessage(), e);
                    // 继续处理下一个对话记录
                }
            }

            logger.info("健康画像分析定时任务执行完成，共处理 {} 条对话记录", conversations.size());
        } catch (Exception e) {
            logger.error("执行健康画像分析定时任务时发生错误: {}", e.getMessage(), e);
        }
    }
}