package org.panjy.servicemetricsplatform.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.panjy.servicemetricsplatform.entity.Dto.WechatMessageDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WechatMessageAnalyzeAddressServiceTransactionTest {

    @Autowired
    private WechatMessageAnalyzeAddressService wechatMessageAnalyzeAddressService;

    @Test
    public void testCalculateServiceTotalTime() {
        // 由于需要连接到实际的数据库，这里只做基本的空值检查测试
        // 实际的数据库测试需要在有数据的环境中进行
        
        // 测试参数为空的情况
        Double result1 = wechatMessageAnalyzeAddressService.calculateServiceTotalTime(null);
        assertNull(result1);
        
        WechatMessageDto dto = new WechatMessageDto();
        dto.setFirstChatTime(LocalDateTime.now());
        dto.setAddress(null);
        
        Double result2 = wechatMessageAnalyzeAddressService.calculateServiceTotalTime(dto);
        assertNull(result2);
    }

    @Test
    public void testCalculateServiceTotalTimeBatch() {
        // 测试批量计算服务总时间
        List<WechatMessageDto> dtoList = new ArrayList<>();
        
        WechatMessageDto dto1 = new WechatMessageDto();
        dto1.setWechatId("test123");
        dto1.setAddress("山西省-晋中市-乐平街王湖经济适用房7号楼一单元");
        dto1.setFirstChatTime(LocalDateTime.now());
        dtoList.add(dto1);
        
        WechatMessageDto dto2 = new WechatMessageDto();
        dto2.setWechatId("test456");
        dto2.setAddress("北京市-朝阳区-建国路88号");
        dto2.setFirstChatTime(LocalDateTime.now());
        dtoList.add(dto2);
        
        // 由于需要连接到实际的数据库，这里只做基本的功能调用测试
        // 实际的结果验证需要在有数据的环境中进行
        List<WechatMessageDto> result = wechatMessageAnalyzeAddressService.calculateServiceTotalTimeBatch(dtoList);
        assertNotNull(result);
    }
}