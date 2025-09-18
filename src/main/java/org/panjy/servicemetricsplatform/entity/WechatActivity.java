package org.panjy.servicemetricsplatform.entity;

import jakarta.persistence.Column;
import lombok.Data;
import java.util.Date;

@Data
public class WechatActivity {
    private String wechatId;
    private Date firstActivityTime;
    private Date lastActivityTime;

    // 手动添加getter方法以确保编译通过
    public String getWechatId() {
        return wechatId;
    }
    
    public Date getFirstActivityTime() {
        return firstActivityTime;
    }
    
    public Date getLastActivityTime() {
        return lastActivityTime;
    }

    /**
     * 计算该用户的服务时长（毫秒）
     */
    public long getServiceDurationMillis() {
        if (firstActivityTime == null || lastActivityTime == null) {
            return 0;
        }
        return lastActivityTime.getTime() - firstActivityTime.getTime();
    }
}
