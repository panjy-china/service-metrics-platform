package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.ServerAccount;
import org.panjy.servicemetricsplatform.mapper.ServerAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务器账户业务服务类
 * 提供服务器账户的批量操作功能
 */
@Service
public class ServerAccountService {
    
    @Autowired
    private ServerAccountMapper serverAccountMapper;
    
    /**
     * 批量插入服务器账户记录
     * @param records 要插入的服务器账户记录列表
     * @return 成功插入的记录数
     */
    @Transactional
    public int batchInsertServerAccounts(List<ServerAccount> records) {
        if (records == null || records.isEmpty()) {
            System.out.println("输入记录列表为空，无需插入");
            return 0;
        }
        
        try {
            int insertedCount = serverAccountMapper.batchInsert(records);
            System.out.println("成功批量插入服务器账户记录，插入数量: " + insertedCount);
            return insertedCount;
        } catch (Exception e) {
            System.err.println("批量插入服务器账户记录时发生错误: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * 批量查询所有服务器账户记录
     * @return 服务器账户记录列表
     */
    public List<ServerAccount> batchSelectAllServerAccounts() {
        try {
            List<ServerAccount> accounts = serverAccountMapper.batchSelectAll();
            System.out.println("成功批量查询服务器账户记录，查询数量: " + (accounts != null ? accounts.size() : 0));
            return accounts;
        } catch (Exception e) {
            System.err.println("批量查询服务器账户记录时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 获取服务器账户总数
     * @return 服务器账户总数
     */
    public long countServerAccounts() {
        try {
            List<ServerAccount> accounts = serverAccountMapper.batchSelectAll();
            long count = accounts != null ? accounts.size() : 0;
            System.out.println("服务器账户总数: " + count);
            return count;
        } catch (Exception e) {
            System.err.println("查询服务器账户总数时发生错误: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
}