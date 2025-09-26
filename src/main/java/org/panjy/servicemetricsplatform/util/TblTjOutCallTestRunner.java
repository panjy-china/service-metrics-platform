package org.panjy.servicemetricsplatform.util;

import org.panjy.servicemetricsplatform.entity.TblTjOutCall;
import org.panjy.servicemetricsplatform.service.TblTjOutCallService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * TblTjOutCall测试运行器
 * 用于测试TblTjOutCall相关功能
 */
@Component
public class TblTjOutCallTestRunner implements CommandLineRunner {

    @Autowired
    private TblTjOutCallService tblTjOutCallService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== TblTjOutCall功能测试 ===");
        
        // 测试查询所有符合条件的客户ID（去重）
        System.out.println("1. 测试查询所有符合条件的客户ID（去重）:");
        List<String> clientIds = tblTjOutCallService.getDistinctClientIds();
        System.out.println("查询到的客户ID数量: " + (clientIds != null ? clientIds.size() : 0));
        if (clientIds != null && !clientIds.isEmpty()) {
            System.out.println("前5个客户ID: ");
            clientIds.stream().limit(5).forEach(System.out::println);
        }
        
        // 测试根据用户ID查询符合条件的第一条记录
        System.out.println("\n2. 测试根据用户ID查询符合条件的第一条记录:");
        if (clientIds != null && !clientIds.isEmpty()) {
            String testUserId = clientIds.get(0);
            System.out.println("使用测试用户ID: " + testUserId);
            TblTjOutCall firstRecord = tblTjOutCallService.getFirstRecordByUserId(testUserId);
            if (firstRecord != null) {
                System.out.println("查询到的第一条记录: " + firstRecord);
            } else {
                System.out.println("未找到符合条件的记录");
            }
        } else {
            System.out.println("没有可用的客户ID进行测试");
        }
        
        System.out.println("=== TblTjOutCall功能测试结束 ===");
    }
}