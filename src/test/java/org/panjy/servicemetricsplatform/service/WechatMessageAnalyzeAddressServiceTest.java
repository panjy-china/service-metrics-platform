package org.panjy.servicemetricsplatform.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.panjy.servicemetricsplatform.entity.WechatMessageAnalyzeAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信消息地址分析结果服务测试类
 */
@SpringBootTest
public class WechatMessageAnalyzeAddressServiceTest {
    
    @Autowired
    private WechatMessageAnalyzeAddressService addressService;
    
    /**
     * 测试单条插入
     */
    @Test
    public void testSave() {
        WechatMessageAnalyzeAddress record = WechatMessageAnalyzeAddress.builder()
                .id(1001L)
                .wechatId("test_wechat_001")
                .msgType(1)
                .wechatTime(System.currentTimeMillis())
                .content("我在北京市朝阳区三里屯")
                .address("北京市-朝阳区-三里屯")
                .build();
        
        boolean result = addressService.save(record);
        System.out.println("保存结果: " + result);
        
        // 验证是否保存成功
        WechatMessageAnalyzeAddress saved = addressService.findById(1001L);
        System.out.println("查询结果: " + saved);
    }
    
    /**
     * 测试批量插入
     */
    @Test
    public void testBatchSave() {
        List<WechatMessageAnalyzeAddress> records = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            WechatMessageAnalyzeAddress record = WechatMessageAnalyzeAddress.builder()
                    .id(2000L + i)
                    .wechatId("test_wechat_batch_" + String.format("%03d", i))
                    .msgType(1)
                    .wechatTime(System.currentTimeMillis() + i * 1000)
                    .content("测试地址内容 " + i + " 上海市浦东新区")
                    .address("上海市-浦东新区-测试地址" + i)
                    .build();
            records.add(record);
        }
        
        int result = addressService.batchSave(records);
        System.out.println("批量保存结果: " + result + " 条记录");
        
        // 验证批量保存结果
        List<WechatMessageAnalyzeAddress> savedRecords = addressService.findByWechatId("test_wechat_batch_001");
        System.out.println("查询结果数量: " + savedRecords.size());
    }
    
    /**
     * 测试按时间范围查询
     */
    @Test
    public void testFindByTimeRange() {
        long currentTime = System.currentTimeMillis();
        long startTime = currentTime - 24 * 60 * 60 * 1000L; // 24小时前
        long endTime = currentTime + 60 * 60 * 1000L; // 1小时后
        
        List<WechatMessageAnalyzeAddress> results = addressService.findByTimeRange(startTime, endTime);
        System.out.println("时间范围查询结果数量: " + results.size());
        
        for (WechatMessageAnalyzeAddress record : results) {
            System.out.println("记录: " + record.getWechatId() + " - " + record.getAddress());
        }
    }
    
    /**
     * 测试记录存在性检查
     */
    @Test
    public void testExistsById() {
        boolean exists = addressService.existsById(1001L);
        System.out.println("记录1001是否存在: " + exists);
        
        boolean notExists = addressService.existsById(9999L);
        System.out.println("记录9999是否存在: " + notExists);
    }
    
    /**
     * 测试分页查询
     */
    @Test
    public void testFindByPage() {
        List<WechatMessageAnalyzeAddress> page1 = addressService.findByPage(1, 3);
        System.out.println("第1页结果数量: " + page1.size());
        
        Long totalCount = addressService.count();
        System.out.println("总记录数: " + totalCount);
    }
}