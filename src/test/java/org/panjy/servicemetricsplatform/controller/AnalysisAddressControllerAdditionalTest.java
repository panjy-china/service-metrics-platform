package org.panjy.servicemetricsplatform.controller;

import org.junit.jupiter.api.Test;
import org.panjy.servicemetricsplatform.service.WechatMessageAnalyzeAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnalysisAddressController.class)
public class AnalysisAddressControllerAdditionalTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WechatMessageAnalyzeAddressService wechatMessageAnalyzeAddressService;

    @Test
    public void testGetTotalServiceTime() throws Exception {
        // 模拟服务方法返回值
        when(wechatMessageAnalyzeAddressService.calculateTotalServiceTime())
                .thenReturn(1250.5);
        
        // 执行测试
        mockMvc.perform(get("/analyze/address/total-service-time")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value("1250.50"))
                .andExpect(jsonPath("$.unit").value("小时"));
    }
}