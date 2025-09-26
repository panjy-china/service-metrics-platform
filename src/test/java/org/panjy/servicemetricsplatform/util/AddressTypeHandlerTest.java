package org.panjy.servicemetricsplatform.util;

import org.junit.jupiter.api.Test;
import org.panjy.servicemetricsplatform.entity.WechatMessageAnalyzeAddress;
import org.panjy.servicemetricsplatform.mapper.WechatMessageAnalyzeAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AddressTypeHandler测试类
 */
@SpringBootTest
@Transactional
public class AddressTypeHandlerTest {

    @Autowired
    private WechatMessageAnalyzeAddressMapper addressMapper;

    @Test
    public void testAddressProcessing() {
        // 创建测试数据
        String originalAddress = "北京市朝阳区某某街道123号";
        WechatMessageAnalyzeAddress record = WechatMessageAnalyzeAddress.builder()
                .wechatId("test_wechat_id")
                .msgType(1)
                .wechatTime(System.currentTimeMillis())
                .content("测试内容")
                .address(originalAddress)
                .build();

        // 插入记录
        int inserted = addressMapper.insert(record);
        assertEquals(1, inserted, "应该成功插入一条记录");

        // 查询记录
        WechatMessageAnalyzeAddress retrieved = addressMapper.selectByWechatIdAndTime(
                record.getWechatId(), record.getWechatTime());

        // 验证address字段是否正确处理
        assertNotNull(retrieved, "应该能查询到记录");
        // 对于不足两个"-"的地址，不会进行特殊处理
        assertEquals(originalAddress, retrieved.getAddress(), "address字段应该保持原样");
    }

    @Test
    public void testBatchInsertWithAddressProcessing() {
        // 创建测试数据
        String address1 = "江苏-无锡-江阴-顾山镇锡张路422号";
        String address2 = "河北省-石家庄市-翟营南大街458号海天花园小区12-4-702";
        
        WechatMessageAnalyzeAddress record1 = WechatMessageAnalyzeAddress.builder()
                .wechatId("test_batch_1")
                .msgType(1)
                .wechatTime(System.currentTimeMillis())
                .content("测试内容1")
                .address(address1)
                .build();
                
        WechatMessageAnalyzeAddress record2 = WechatMessageAnalyzeAddress.builder()
                .wechatId("test_batch_2")
                .msgType(1)
                .wechatTime(System.currentTimeMillis() + 1000)
                .content("测试内容2")
                .address(address2)
                .build();

        // 批量插入
        java.util.List<WechatMessageAnalyzeAddress> records = java.util.Arrays.asList(record1, record2);
        int inserted = addressMapper.batchInsert(records);
        assertEquals(2, inserted, "应该成功插入两条记录");

        // 查询记录
        List<WechatMessageAnalyzeAddress> retrievedList = addressMapper.selectByWechatId("test_batch_1");
        assertFalse(retrievedList.isEmpty(), "应该能查询到记录");
        assertEquals("江苏%无锡%江阴%顾山%2号", retrievedList.get(0).getAddress(), "第一条记录的address字段应该被正确处理");

        retrievedList = addressMapper.selectByWechatId("test_batch_2");
        assertFalse(retrievedList.isEmpty(), "应该能查询到记录");
        assertEquals("河北%石家%翟营%4%02", retrievedList.get(0).getAddress(), "第二条记录的address字段应该被正确处理");
    }

    @Test
    public void testUpdateWithAddressProcessing() {
        // 创建测试数据
        String originalAddress = "江苏-无锡-江阴-顾山镇锡张路422号";
        String updatedAddress = "河北省-石家庄市-翟营南大街458号海天花园小区12-4-702";
        
        WechatMessageAnalyzeAddress record = WechatMessageAnalyzeAddress.builder()
                .wechatId("test_update")
                .msgType(1)
                .wechatTime(System.currentTimeMillis())
                .content("原始内容")
                .address(originalAddress)
                .build();

        // 插入记录
        int inserted = addressMapper.insert(record);
        assertEquals(1, inserted, "应该成功插入一条记录");

        // 更新记录
        record.setAddress(updatedAddress);
        record.setContent("更新后的内容");
        int updated = addressMapper.updateByWechatIdAndTime(record);
        assertEquals(1, updated, "应该成功更新一条记录");

        // 查询记录
        WechatMessageAnalyzeAddress retrieved = addressMapper.selectByWechatIdAndTime(
                record.getWechatId(), record.getWechatTime());

        // 验证address字段是否正确处理
        assertNotNull(retrieved, "应该能查询到记录");
        assertEquals("河北%石家%翟营%4%02", retrieved.getAddress(), "address字段应该被正确处理");
        assertEquals("更新后的内容", retrieved.getContent(), "content字段应该被正确更新");
    }
    
    @Test
    public void testSensitiveWordFiltering() {
        // 创建包含敏感词的测试数据
        String addressWithSensitiveWords = "这是一个敏感信息地址";
        WechatMessageAnalyzeAddress record = WechatMessageAnalyzeAddress.builder()
                .wechatId("test_sensitive")
                .msgType(1)
                .wechatTime(System.currentTimeMillis())
                .content("测试内容")
                .address(addressWithSensitiveWords)
                .build();

        // 插入记录
        int inserted = addressMapper.insert(record);
        assertEquals(1, inserted, "应该成功插入一条记录");

        // 查询记录
        WechatMessageAnalyzeAddress retrieved = addressMapper.selectByWechatIdAndTime(
                record.getWechatId(), record.getWechatTime());

        // 验证敏感词是否被过滤
        assertNotNull(retrieved, "应该能查询到记录");
        assertEquals("这是一个***信息地址", retrieved.getAddress(), "敏感词应该被过滤");
    }
    
    @Test
    public void testAddressWithLessThanTwoDashes() {
        // 创建测试数据 - 只有一个"-"
        String addressWithOneDash = "江苏省-南京市";
        WechatMessageAnalyzeAddress record = WechatMessageAnalyzeAddress.builder()
                .wechatId("test_one_dash")
                .msgType(1)
                .wechatTime(System.currentTimeMillis())
                .content("测试内容")
                .address(addressWithOneDash)
                .build();

        // 插入记录
        int inserted = addressMapper.insert(record);
        assertEquals(1, inserted, "应该成功插入一条记录");

        // 查询记录
        WechatMessageAnalyzeAddress retrieved = addressMapper.selectByWechatIdAndTime(
                record.getWechatId(), record.getWechatTime());

        // 验证address字段是否保持原样（不足两个"-"不处理）
        assertNotNull(retrieved, "应该能查询到记录");
        assertEquals(addressWithOneDash, retrieved.getAddress(), "不足两个\"-\"的地址应该保持原样");
    }
}