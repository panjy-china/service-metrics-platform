package org.panjy.servicemetricsplatform.mapper.clickhouse;

import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.Client;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户信息Mapper接口
 * 对应ClickHouse中的aikang.tbl_Client表
 * 
 * @author System Generated
 */
public interface ClientMapper {

    /**
     * 查询备注信息有效的客户列表
     * 筛选条件：
     * 1. 备注不为空
     * 2. 备注长度>=20字符
     * 3. 备注不包含"成单"关键词
     * 4. 备注不包含"下单"关键词
     * 
     * @return 符合条件的客户列表
     */
    List<Client> selectClientsWithValidDemo();

    /**
     * 批量更新客户信息（性别、年龄、身高、体重、备注）
     * 
     * @param clients 客户列表
     * @return 更新的记录数
     */
    int batchUpdateClientAnalysisResult(@Param("clients") List<Client> clients);

    /**
     * 更新单个客户的分析结果
     * 
     * @param client 客户信息
     * @return 更新的记录数
     */
    int updateClientAnalysisResult(@Param("client") Client client);
}