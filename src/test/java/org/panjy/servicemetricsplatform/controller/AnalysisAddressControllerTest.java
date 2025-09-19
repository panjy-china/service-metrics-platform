package org.panjy.servicemetricsplatform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.panjy.servicemetricsplatform.service.WechatMessageAnalyzeAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalysisAddressController.class)
public class AnalysisAddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WechatMessageAnalyzeAddressService wechatMessageAnalyzeAddressService;

    @Test
    public void testGetAverageServiceTimeByDate() throws Exception {
        // 准备测试数据
        Date testDate = new Date();
        LocalDate localDate = testDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        
        // 模拟服务方法返回值
        when(wechatMessageAnalyzeAddressService.calculateAverageServiceTimeByDate(any(LocalDate.class)))
                .thenReturn(24.5);
        
        // 执行测试
        mockMvc.perform(get("/analyze/address/average-service-time/2023-01-01")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("24.50"))
                .andExpect(jsonPath("$.unit").value("小时"));
    }
    
    @Test
    public void testGetOverallAverageServiceTime() throws Exception {
        // 模拟服务方法返回值
        when(wechatMessageAnalyzeAddressService.calculateOverallAverageServiceTime())
                .thenReturn(30.25);
        
        // 执行测试
        mockMvc.perform(get("/analyze/address/overall-average-service-time")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("30.25"))
                .andExpect(jsonPath("$.unit").value("小时"));
    }
}