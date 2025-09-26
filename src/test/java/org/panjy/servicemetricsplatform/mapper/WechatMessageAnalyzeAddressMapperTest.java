package org.panjy.servicemetricsplatform.mapper;

import org.junit.jupiter.api.Test;
import org.panjy.servicemetricsplatform.entity.WechatMessageAnalyzeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WechatMessageAnalyzeAddressMapper测试类
 */
@SpringBootTest
@Transactional
public class WechatMessageAnalyzeAddressMapperTest {

    @Autowired
    private WechatMessageAnalyzeAddressMapper addressMapper;

    @Test
    public void testSelectAllWithAddressProcessing() {
        // 先插入一些测试数据
        insertTestRecords();
        
        // 查询前10条记录
        List<WechatMessageAnalyzeAddress> records = addressMapper.selectAll(0, 10);
        
        // 输出处理后的地址
        System.out.println("前10条记录的地址处理结果：");
        System.out.println("=====================================");
        
        for (int i = 0; i < records.size(); i++) {
            WechatMessageAnalyzeAddress record = records.get(i);
            System.out.println("记录 " + (i + 1) + ":");
            System.out.println("  微信ID: " + record.getWechatId());
            System.out.println("  时间: " + record.getWechatTime());
            System.out.println("  原始地址: " + record.getAddress());
            System.out.println("  处理后地址: " + record.getAddress());
            System.out.println("  消息类型: " + record.getMsgType());
            System.out.println("  内容: " + record.getContent());
            System.out.println("-------------------------------------");
        }
        
        System.out.println("总共查询到 " + records.size() + " 条记录");
    }
    
    /**
     * 插入测试数据
     */
    private void insertTestRecords() {
        List<WechatMessageAnalyzeAddress> testRecords = new ArrayList<>();
        
        // 添加包含"-"的地址测试数据
        testRecords.add(WechatMessageAnalyzeAddress.builder()
                .wechatId("test_user_1")
                .msgType(1)
                .wechatTime(System.currentTimeMillis())
                .content("测试消息1")
                .address("江苏-无锡-江阴-顾山镇锡张路422号")
                .build());
                
        testRecords.add(WechatMessageAnalyzeAddress.builder()
                .wechatId("test_user_2")
                .msgType(1)
                .wechatTime(System.currentTimeMillis() + 1000)
                .content("测试消息2")
                .address("河北省-石家庄市-翟营南大街458号海天花园小区12-4-702")
                .build());
                
        testRecords.add(WechatMessageAnalyzeAddress.builder()
                .wechatId("test_user_3")
                .msgType(1)
                .wechatTime(System.currentTimeMillis() + 2000)
                .content("测试消息3")
                .address("北京市朝阳区某某街道123号")
                .build());
                
        testRecords.add(WechatMessageAnalyzeAddress.builder()
                .wechatId("test_user_4")
                .msgType(1)
                .wechatTime(System.currentTimeMillis() + 3000)
                .content("测试消息4")
                .address("上海市-浦东新区-张江镇-高科技园区456号")
                .build());
                
        testRecords.add(WechatMessageAnalyzeAddress.builder()
                .wechatId("test_user_5")
                .msgType(1)
                .wechatTime(System.currentTimeMillis() + 4000)
                .content("测试消息5")
                .address("广东省-深圳市-南山区-科技园南路789号")
                .build());
                
        testRecords.add(WechatMessageAnalyzeAddress.builder()
                .wechatId("test_user_6")
                .msgType(1)
                .wechatTime(System.currentTimeMillis() + 5000)
                .content("测试消息6")
                .address("浙江省-杭州市-西湖区-文三路1001号")
                .build());
                
        testRecords.add(WechatMessageAnalyzeAddress.builder()
                .wechatId("test_user_7")
                .msgType(1)
                .wechatTime(System.currentTimeMillis() + 6000)
                .content("测试消息7")
                .address("四川省-成都市-高新区-天府大道2002号")
                .build());
                
        testRecords.add(WechatMessageAnalyzeAddress.builder()
                .wechatId("test_user_8")
                .msgType(1)
                .wechatTime(System.currentTimeMillis() + 7000)
                .content("测试消息8")
                .address("湖北省-武汉市-江汉区-解放大道3003号")
                .build());
                
        testRecords.add(WechatMessageAnalyzeAddress.builder()
                .wechatId("test_user_9")
                .msgType(1)
                .wechatTime(System.currentTimeMillis() + 8000)
                .content("测试消息9")
                .address("湖南省-长沙市-岳麓区-麓谷大道4004号")
                .build());
                
        testRecords.add(WechatMessageAnalyzeAddress.builder()
                .wechatId("test_user_10")
                .msgType(1)
                .wechatTime(System.currentTimeMillis() + 9000)
                .content("测试消息10")
                .address("江苏省-南京市-鼓楼区-中山路5005号")
                .build());
                
        testRecords.add(WechatMessageAnalyzeAddress.builder()
                .wechatId("test_user_11")
                .msgType(1)
                .wechatTime(System.currentTimeMillis() + 10000)
                .content("测试消息11")
                .address("山东省-青岛市-市南区-香港中路6006号")
                .build());
                
        // 批量插入测试数据
        addressMapper.batchInsert(testRecords);
    }
}