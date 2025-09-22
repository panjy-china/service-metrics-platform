package org.panjy.servicemetricsplatform.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 服务时间实体类
 * 对应ClickHouse中的aikang.tbl_ServerTime表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerTime {
    
    /** 客户ID */
    private String colCltID;
    
    /** 服务时长（秒） */
    private Long colSerTi;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
}