package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.ServerAccount;

import java.util.List;

/**
 * 服务器账户Mapper接口
 * 用于ClickHouse数据库操作
 */
@Mapper
public interface ServerAccountMapper {
    
    /**
     * 批量插入服务器账户记录
     * @param records 要插入的服务器账户记录列表
     * @return 受影响的行数
     */
    int batchInsert(@Param("records") List<ServerAccount> records);
    
    /**
     * 批量查询所有服务器账户记录
     * @return 服务器账户记录列表
     */
    List<ServerAccount> batchSelectAll();
}