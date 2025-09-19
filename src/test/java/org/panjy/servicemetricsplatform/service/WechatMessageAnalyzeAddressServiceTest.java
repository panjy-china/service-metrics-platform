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
public class WechatMessageAnalyzeAddressServiceTest {

    @Autowired
    private WechatMessageAnalyzeAddressService wechatMessageAnalyzeAddressService;

    @Test
    public void testFormatAddressWithLessThanThreeHyphens() {
        // 测试-出现次数小于三的情况
        String address1 = "山西省-晋中市-榆次区";
        String expected1 = "山西%-晋中%-榆次%"; // 最后一个字段长度<=6，不进行特殊处理
        String result1 = wechatMessageAnalyzeAddressService.formatAddress(address1);
        assertEquals(expected1, result1);

        String address2 = "山西省-晋中市-乐平街王湖经济适用房7号楼一单元";
        String expected2 = "山西%-晋中%-乐平%单元"; // 最后一个字段长度>6，进行特殊处理
        String result2 = wechatMessageAnalyzeAddressService.formatAddress(address2);
        assertEquals(expected2, result2);
    }

    @Test
    public void testFormatAddressWithMoreThanThreeHyphens() {
        // 测试-出现次数大于三的情况
        String address1 = "山西省-晋中市-榆次区-乐平街王湖经济适用房7号楼一单元";
        String expected1 = "山西%-晋中%-榆次%-乐平%单元"; // 最后一个字段长度>6，进行特殊处理
        String result1 = wechatMessageAnalyzeAddressService.formatAddress(address1);
        assertEquals(expected1, result1);

        String address2 = "山西省-晋中市-榆次区-乐平街";
        String expected2 = "山西%-晋中%-榆次%-乐平%"; // 最后一个字段长度<=6，不进行特殊处理
        String result2 = wechatMessageAnalyzeAddressService.formatAddress(address2);
        assertEquals(expected2, result2);
    }

    @Test
    public void testFormatAddressWithEdgeCases() {
        // 测试边界情况
        assertNull(wechatMessageAnalyzeAddressService.formatAddress(null));
        
        String emptyAddress = "";
        assertEquals(emptyAddress, wechatMessageAnalyzeAddressService.formatAddress(emptyAddress));
        
        String singlePartAddress = "山西省";
        String expectedSinglePart = "山西%"; // 长度>2，进行处理
        String resultSinglePart = wechatMessageAnalyzeAddressService.formatAddress(singlePartAddress);
        assertEquals(expectedSinglePart, resultSinglePart);
        
        String shortPartAddress = "山西-晋中-榆次";
        String expectedShortPart = "山西%-晋中%-榆次%"; // 所有部分长度<=2，但最后一个字段仍按规则处理
        String resultShortPart = wechatMessageAnalyzeAddressService.formatAddress(shortPartAddress);
        assertEquals(expectedShortPart, resultShortPart);
    }

    @Test
    public void testFormatAddressInDto() {
        // 测试格式化WechatMessageDto中的地址
        WechatMessageDto dto = new WechatMessageDto();
        dto.setWechatId("test123");
        dto.setAddress("山西省-晋中市-乐平街王湖经济适用房7号楼一单元");
        dto.setFirstChatTime(LocalDateTime.now());
        
        WechatMessageDto formattedDto = wechatMessageAnalyzeAddressService.formatAddressInDto(dto);
        
        assertEquals("山西%-晋中%-乐平%单元", formattedDto.getAddress());
        assertEquals(dto.getWechatId(), formattedDto.getWechatId());
        assertEquals(dto.getFirstChatTime(), formattedDto.getFirstChatTime());
    }

    @Test
    public void testFormatAddressInDtoList() {
        // 测试批量格式化WechatMessageDto列表
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
        
        List<WechatMessageDto> formattedList = wechatMessageAnalyzeAddressService.formatAddressInDtoList(dtoList);
        
        assertEquals(2, formattedList.size());
        assertEquals("山西%-晋中%-乐平%单元", formattedList.get(0).getAddress());
        assertEquals("北京%-朝阳%-建国%88号", formattedList.get(1).getAddress());
    }
}