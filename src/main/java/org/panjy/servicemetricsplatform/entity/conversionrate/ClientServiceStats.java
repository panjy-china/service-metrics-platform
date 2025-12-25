package org.panjy.servicemetricsplatform.entity.conversionrate;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 客户服务统计实体类
 * 用于存储客户的服务统计数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientServiceStats {
    
    /** 客户ID */
    private String colCltID;
    
    /** 服务员工列表 */
    private String[] empList;
    
    /** 服务员工数量 */
    private Integer empCount;
    
    /** 最大服务时间（秒） */
    private Long serviceSeconds;
    
    /** 服务天数 */
    private Double serviceDays;
}