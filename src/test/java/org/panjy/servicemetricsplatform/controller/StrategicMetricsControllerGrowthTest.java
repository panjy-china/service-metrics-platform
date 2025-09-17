package org.panjy.servicemetricsplatform.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 策略层指标控制器同比增长率功能测试
 */
@SpringBootTest
@ActiveProfiles("test")
@SpringJUnitConfig
public class StrategicMetricsControllerGrowthTest {

    @Autowired
    private StrategicMetricsController strategicMetricsController;

    private Date testDate;
    private SimpleDateFormat dateFormat;

    @BeforeEach
    void setUp() {
        // 设置测试日期为2024年1月1日
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 1, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        testDate = cal.getTime();
        
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        System.out.println("设置测试日期: " + dateFormat.format(testDate));
    }

    @Test
    @DisplayName("测试新增用户同比增长率API")
    void testGetNewUsersByDayWithGrowth() {
        try {
            System.out.println("开始测试新增用户同比增长率API...");
            
            ResponseEntity<?> response = strategicMetricsController.getNewUsersByDayWithGrowth(testDate);
            
            assertNotNull(response, "响应不应为null");
            assertEquals(200, response.getStatusCodeValue(), "HTTP状态码应为200");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertNotNull(responseBody, "响应体不应为null");
            
            assertTrue((Boolean) responseBody.get("success"), "API调用应成功");
            assertNotNull(responseBody.get("data"), "数据不应为null");
            assertNotNull(responseBody.get("timestamp"), "时间戳不应为null");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            assertNotNull(data.get("currentCount"), "当前值不应为null");
            assertNotNull(data.get("previousDayCount"), "前一天值不应为null");
            assertNotNull(data.get("growthRate"), "增长率不应为null");
            
            System.out.println("新增用户同比增长率测试通过: " + responseBody);
            
        } catch (Exception e) {
            System.err.println("新增用户同比增长率测试失败: " + e.getMessage());
            fail("测试出现异常: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试周新增用户周环比增长率API")
    void testGetNewUsersByWeekWithGrowth() {
        try {
            System.out.println("开始测试周新增用户周环比增长率API...");
            
            ResponseEntity<?> response = strategicMetricsController.getNewUsersByWeekWithGrowth(testDate);
            
            assertNotNull(response, "响应不应为null");
            assertEquals(200, response.getStatusCodeValue(), "HTTP状态码应为200");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertNotNull(responseBody, "响应体不应为null");
            
            assertTrue((Boolean) responseBody.get("success"), "API调用应成功");
            assertNotNull(responseBody.get("data"), "数据不应为null");
            assertNotNull(responseBody.get("timestamp"), "时间戳不应为null");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            assertNotNull(data.get("currentWeekCount"), "当前周值不应为null");
            assertNotNull(data.get("previousWeekCount"), "上周值不应为null");
            assertNotNull(data.get("growthRate"), "增长率不应为null");
            assertNotNull(data.get("previousWeekDate"), "上周日期不应为null");
            
            System.out.println("周新增用户周环比增长率测试通过: " + responseBody);
            
        } catch (Exception e) {
            System.err.println("周新增用户周环比增长率测试失败: " + e.getMessage());
            fail("测试出现异常: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试月新增用户月环比增长率API")
    void testGetNewUsersByMonthWithGrowth() {
        try {
            System.out.println("开始测试月新增用户月环比增长率API...");
            
            ResponseEntity<?> response = strategicMetricsController.getNewUsersByMonthWithGrowth(testDate);
            
            assertNotNull(response, "响应不应为null");
            assertEquals(200, response.getStatusCodeValue(), "HTTP状态码应为200");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertNotNull(responseBody, "响应体不应为null");
            
            assertTrue((Boolean) responseBody.get("success"), "API调用应成功");
            assertNotNull(responseBody.get("data"), "数据不应为null");
            assertNotNull(responseBody.get("timestamp"), "时间戳不应为null");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            assertNotNull(data.get("currentMonthCount"), "当前月值不应为null");
            assertNotNull(data.get("previousMonthCount"), "上月值不应为null");
            assertNotNull(data.get("growthRate"), "增长率不应为null");
            assertNotNull(data.get("previousMonthDate"), "上月日期不应为null");
            
            System.out.println("月新增用户月环比增长率测试通过: " + responseBody);
            
        } catch (Exception e) {
            System.err.println("月新增用户月环比增长率测试失败: " + e.getMessage());
            fail("测试出现异常: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试活跃用户数同比增长率API")
    void testGetActiveUserCountWithGrowth() {
        try {
            System.out.println("开始测试活跃用户数同比增长率API...");
            
            ResponseEntity<?> response = strategicMetricsController.getActiveUserCountWithGrowth(testDate);
            
            assertNotNull(response, "响应不应为null");
            assertEquals(200, response.getStatusCodeValue(), "HTTP状态码应为200");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertNotNull(responseBody, "响应体不应为null");
            
            assertTrue((Boolean) responseBody.get("success"), "API调用应成功");
            assertNotNull(responseBody.get("data"), "数据不应为null");
            assertNotNull(responseBody.get("timestamp"), "时间戳不应为null");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            assertNotNull(data.get("currentActiveUserCount"), "当前活跃用户数不应为null");
            assertNotNull(data.get("previousYearActiveUserCount"), "上年同期活跃用户数不应为null");
            assertNotNull(data.get("growthRate"), "增长率不应为null");
            
            System.out.println("活跃用户数同比增长率测试通过: " + responseBody);
            
        } catch (Exception e) {
            System.err.println("活跃用户数同比增长率测试失败: " + e.getMessage());
            fail("测试出现异常: " + e.getMessage());
        }
    }







    @Test
    @DisplayName("测试平均服务时间同比增长率API")
    void testGetAverageServiceTimeWithGrowth() {
        try {
            System.out.println("开始测试平均服务时间同比增长率API...");
            
            ResponseEntity<?> response = strategicMetricsController.getAverageServiceTimeWithGrowth(testDate);
            
            assertNotNull(response, "响应不应为null");
            assertEquals(200, response.getStatusCodeValue(), "HTTP状态码应为200");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertNotNull(responseBody, "响应体不应为null");
            
            assertTrue((Boolean) responseBody.get("success"), "API调用应成功");
            assertNotNull(responseBody.get("data"), "数据不应为null");
            assertNotNull(responseBody.get("timestamp"), "时间戳不应为null");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            assertNotNull(data.get("currentAverageServiceTimeDays"), "当前平均服务时间不应为null");
            assertNotNull(data.get("previousYearAverageServiceTimeDays"), "上年同期平均服务时间不应为null");
            assertNotNull(data.get("growthRate"), "增长率不应为null");
            assertNotNull(data.get("currentAverageServiceTimeFormatted"), "格式化的当前服务时间不应为null");
            assertNotNull(data.get("previousYearAverageServiceTimeFormatted"), "格式化的上年同期服务时间不应为null");
            
            System.out.println("平均服务时间同比增长率测试通过: " + responseBody);
            
        } catch (Exception e) {
            System.err.println("平均服务时间同比增长率测试失败: " + e.getMessage());
            fail("测试出现异常: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试综合指标概览同比增长率API")
    void testGetMetricsOverviewWithGrowth() {
        try {
            System.out.println("开始测试综合指标概览同比增长率API...");
            
            ResponseEntity<?> response = strategicMetricsController.getMetricsOverviewWithGrowth(testDate);
            
            assertNotNull(response, "响应不应为null");
            assertEquals(200, response.getStatusCodeValue(), "HTTP状态码应为200");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertNotNull(responseBody, "响应体不应为null");
            
            assertTrue((Boolean) responseBody.get("success"), "API调用应成功");
            assertNotNull(responseBody.get("data"), "数据不应为null");
            assertNotNull(responseBody.get("timestamp"), "时间戳不应为null");
            
            // 在新的统一格式中，概览数据在data字段中
            @SuppressWarnings("unchecked")
            Map<String, Object> overview = (Map<String, Object>) responseBody.get("data");
            assertNotNull(overview, "概览数据不应为null");
            
            // 验证新增用户相关数据
            assertNotNull(overview.get("newUsersCount"), "新增用户数不应为null");
            assertNotNull(overview.get("newUsersGrowthRate"), "新增用户增长率不应为null");
            assertNotNull(overview.get("previousDayNewUsersCount"), "前一天新增用户数不应为null");
            
            // 验证活跃用户相关数据
            assertNotNull(overview.get("activeUserCount"), "活跃用户数不应为null");
            assertNotNull(overview.get("activeUsersGrowthRate"), "活跃用户增长率不应为null");
            assertNotNull(overview.get("previousYearActiveUserCount"), "上年同期活跃用户数不应为null");
            
            // 验证平均服务时间相关数据
            assertNotNull(overview.get("averageServiceTimeDays"), "平均服务时间不应为null");
            assertNotNull(overview.get("serviceTimeGrowthRate"), "服务时间增长率不应为null");
            assertNotNull(overview.get("previousYearAverageServiceTimeDays"), "上年同期平均服务时间不应为null");
            
            // 验证上年同期日期
            assertNotNull(overview.get("previousYearDate"), "上年同期日期不应为null");
            
            System.out.println("综合指标概览同比增长率测试通过: " + overview);
            
        } catch (Exception e) {
            System.err.println("综合指标概览同比增长率测试失败: " + e.getMessage());
            fail("测试出现异常: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试空日期参数处理")
    void testNullDateParameter() {
        try {
            System.out.println("开始测试空日期参数处理...");
            
            ResponseEntity<?> response = strategicMetricsController.getNewUsersByDayWithGrowth(null);
            
            assertNotNull(response, "响应不应为null");
            assertEquals(400, response.getStatusCodeValue(), "空日期参数应返回400错误");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertNotNull(responseBody, "响应体不应为null");
            
            assertFalse((Boolean) responseBody.get("success"), "空日期参数调用应失败");
            assertEquals("INVALID_DATE", responseBody.get("errorCode"), "错误代码应为INVALID_DATE");
            
            System.out.println("空日期参数处理测试通过: " + responseBody);
            
        } catch (Exception e) {
            System.err.println("空日期参数处理测试失败: " + e.getMessage());
            fail("测试出现异常: " + e.getMessage());
        }
    }
}