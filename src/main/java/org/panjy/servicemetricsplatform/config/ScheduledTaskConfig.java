package org.panjy.servicemetricsplatform.config;

import org.panjy.servicemetricsplatform.util.UnifiedScheduledTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

/**
 * 定时任务配置类
 */
@Configuration
public class ScheduledTaskConfig {

    @Autowired
    private UnifiedScheduledTask unifiedScheduledTask;

    /**
     * 在应用启动后初始化ScheduledExecutorService定时任务
     */
    @EventListener(ContextRefreshedEvent.class)
    public void initScheduledTasks() {
        // 启动ScheduledExecutorService定时任务
        unifiedScheduledTask.scheduledAddressAnalysisWithExecutor();
    }
}