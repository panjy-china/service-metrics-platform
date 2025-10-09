package org.panjy.servicemetricsplatform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.panjy.servicemetricsplatform.entity.CallStatistics;
import org.panjy.servicemetricsplatform.mapper.TblTjInCallMapper;
import org.panjy.servicemetricsplatform.mapper.TblTjOutCallStatisticsMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * 个性化指导率服务测试类
 */
class PersonalizedGuidanceRateServiceTest {

    @Mock
    private TblTjInCallMapper tblTjInCallMapper;

    @Mock
    private TblTjOutCallStatisticsMapper tblTjOutCallStatisticsMapper;

    @InjectMocks
    private PersonalizedGuidanceRateService personalizedGuidanceRateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCalculatePersonalizedGuidanceRates() {
        // 准备测试数据
        CallStatistics inCallStat1 = new CallStatistics("wechat1", 10, 5, 0.0);
        CallStatistics inCallStat2 = new CallStatistics("wechat2", 8, 2, 0.0);
        List<CallStatistics> inCallStats = Arrays.asList(inCallStat1, inCallStat2);

        CallStatistics outCallStat1 = new CallStatistics("wechat1", 6, 3, 0.0);
        CallStatistics outCallStat2 = new CallStatistics("wechat3", 4, 1, 0.0);
        List<CallStatistics> outCallStats = Arrays.asList(outCallStat1, outCallStat2);

        // 设置mock行为
        when(tblTjInCallMapper.selectInCallStatistics()).thenReturn(inCallStats);
        when(tblTjOutCallStatisticsMapper.selectOutCallStatistics()).thenReturn(outCallStats);

        // 执行测试
        List<CallStatistics> result = personalizedGuidanceRateService.calculatePersonalizedGuidanceRates();

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());

        // 验证wechat1的数据（合并后：totalCalls=16, longCalls=8）
        CallStatistics wechat1Result = result.stream()
                .filter(stat -> "wechat1".equals(stat.getWechatId()))
                .findFirst()
                .orElse(null);
        assertNotNull(wechat1Result);
        assertEquals(16, wechat1Result.getTotalCalls());
        assertEquals(8, wechat1Result.getLongCalls());
        assertEquals(0.5, wechat1Result.getPersonalizedGuidanceRate(), 0.001);

        // 验证排序（按总通话次数降序）
        assertEquals("wechat1", result.get(0).getWechatId()); // 16次通话
        assertEquals("wechat2", result.get(1).getWechatId()); // 8次通话
        assertEquals("wechat3", result.get(2).getWechatId()); // 4次通话
    }

    @Test
    void testGetPersonalizedGuidanceRateByWechatId() {
        // 准备测试数据
        CallStatistics inCallStat = new CallStatistics("testWechat", 10, 5, 0.0);
        CallStatistics outCallStat = new CallStatistics("testWechat", 10, 3, 0.0);

        when(tblTjInCallMapper.selectInCallStatistics()).thenReturn(Arrays.asList(inCallStat));
        when(tblTjOutCallStatisticsMapper.selectOutCallStatistics()).thenReturn(Arrays.asList(outCallStat));

        // 执行测试
        Double rate = personalizedGuidanceRateService.getPersonalizedGuidanceRateByWechatId("testWechat");

        // 验证结果（总通话20次，长通话8次，触达率0.4）
        assertEquals(0.4, rate, 0.001);
    }

    @Test
    void testGetPersonalizedGuidanceRateByWechatIdNotFound() {
        // 准备空数据
        when(tblTjInCallMapper.selectInCallStatistics()).thenReturn(Arrays.asList());
        when(tblTjOutCallStatisticsMapper.selectOutCallStatistics()).thenReturn(Arrays.asList());

        // 执行测试
        Double rate = personalizedGuidanceRateService.getPersonalizedGuidanceRateByWechatId("nonExistentWechat");

        // 验证结果
        assertEquals(0.0, rate, 0.001);
    }

    @Test
    void testGetAllPersonalizedGuidanceRates() {
        // 准备测试数据
        CallStatistics inCallStat = new CallStatistics("wechat1", 10, 5, 0.0);
        CallStatistics outCallStat = new CallStatistics("wechat1", 10, 3, 0.0);

        when(tblTjInCallMapper.selectInCallStatistics()).thenReturn(Arrays.asList(inCallStat));
        when(tblTjOutCallStatisticsMapper.selectOutCallStatistics()).thenReturn(Arrays.asList(outCallStat));

        // 执行测试
        Map<String, Double> rateMap = personalizedGuidanceRateService.getAllPersonalizedGuidanceRates();

        // 验证结果
        assertNotNull(rateMap);
        assertTrue(rateMap.containsKey("wechat1"));
        assertEquals(0.4, rateMap.get("wechat1"), 0.001);
    }

    @Test
    void testCalculatePersonalizedGuidanceRatesWithZeroTotalCalls() {
        // 准备测试数据（总通话次数为0）
        CallStatistics inCallStat = new CallStatistics("zeroCallWechat", 0, 0, 0.0);

        when(tblTjInCallMapper.selectInCallStatistics()).thenReturn(Arrays.asList(inCallStat));
        when(tblTjOutCallStatisticsMapper.selectOutCallStatistics()).thenReturn(Arrays.asList());

        // 执行测试
        List<CallStatistics> result = personalizedGuidanceRateService.calculatePersonalizedGuidanceRates();

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(0.0, result.get(0).getPersonalizedGuidanceRate(), 0.001);
    }
}