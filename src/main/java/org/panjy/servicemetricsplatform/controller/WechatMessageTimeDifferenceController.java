package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.FirstCallRecord;
import org.panjy.servicemetricsplatform.entity.Message;
import org.panjy.servicemetricsplatform.service.FirstCallSummaryService;
import org.panjy.servicemetricsplatform.service.WechatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 微信消息时间差控制器
 * 提供计算首通记录时间和最早消息记录时间差值的HTTP接口
 */
@RestController
@RequestMapping("/api/wechat-time-difference")
public class WechatMessageTimeDifferenceController {

    @Autowired
    private FirstCallSummaryService firstCallSummaryService;

    @Autowired
    private WechatMessageService wechatMessageService;

    /**
     * 计算指定微信ID的首通记录时间和最早消息记录时间的差值
     *
     * @param wechatId 微信ID
     * @return 时间差值（秒）
     */
    @GetMapping("/calculate")
    public TimeDifferenceResult calculateTimeDifference(@RequestParam String wechatId) {
        if (wechatId == null || wechatId.isEmpty()) {
            return new TimeDifferenceResult(wechatId, null, null, null, "微信ID不能为空");
        }

        try {
            // 获取首通记录
            List<FirstCallRecord> firstCallRecords = firstCallSummaryService.getFirstCallRecordsByWechatId(wechatId);
            if (firstCallRecords == null || firstCallRecords.isEmpty()) {
                return new TimeDifferenceResult(wechatId, null, null, null, "未找到该微信ID的首通记录");
            }

            // 获取最早的首通记录时间
            LocalDateTime firstCallTime = firstCallRecords.get(0).getFirstCallDate();
            if (firstCallTime == null) {
                return new TimeDifferenceResult(wechatId, null, null, null, "首通记录时间为空");
            }

            // 获取最早的消息记录
            Message earliestMessage = wechatMessageService.findEarliestMessageByWechatId(wechatId);
            if (earliestMessage == null) {
                return new TimeDifferenceResult(wechatId, firstCallTime, null, null, "未找到该微信ID的最早消息记录");
            }

            // 获取最早消息记录的时间
            LocalDateTime earliestMessageTime = earliestMessage.getChatTime();
            if (earliestMessageTime == null) {
                return new TimeDifferenceResult(wechatId, firstCallTime, null, null, "最早消息记录时间为空");
            }

            // 计算时间差值
            Duration duration = Duration.between(earliestMessageTime, firstCallTime);
            long differenceInSeconds = Math.abs(duration.getSeconds());

            return new TimeDifferenceResult(wechatId, firstCallTime, earliestMessageTime, differenceInSeconds, null);
        } catch (Exception e) {
            return new TimeDifferenceResult(wechatId, null, null, null, "计算时间差值时发生错误: " + e.getMessage());
        }
    }

    /**
     * 时间差值结果封装类
     */
    public static class TimeDifferenceResult {
        private String wechatId;
        private LocalDateTime firstCallTime;
        private LocalDateTime earliestMessageTime;
        private Long timeDifferenceInSeconds;
        private String errorMessage;

        public TimeDifferenceResult(String wechatId, LocalDateTime firstCallTime, LocalDateTime earliestMessageTime, Long timeDifferenceInSeconds, String errorMessage) {
            this.wechatId = wechatId;
            this.firstCallTime = firstCallTime;
            this.earliestMessageTime = earliestMessageTime;
            this.timeDifferenceInSeconds = timeDifferenceInSeconds;
            this.errorMessage = errorMessage;
        }

        // Getter方法
        public String getWechatId() {
            return wechatId;
        }

        public LocalDateTime getFirstCallTime() {
            return firstCallTime;
        }

        public LocalDateTime getEarliestMessageTime() {
            return earliestMessageTime;
        }

        public Long getTimeDifferenceInSeconds() {
            return timeDifferenceInSeconds;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public boolean isSuccess() {
            return errorMessage == null;
        }
    }
}