package org.panjy.servicemetricsplatform.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 服务器账户实体类
 * 对应ClickHouse中的tbl_server_account表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerAccount {
    
    /** ID */
    private Long id;
    
    /** 账户代码 */
    private String accountCode;
    
    /** 账户名称 */
    private String accountName;
}