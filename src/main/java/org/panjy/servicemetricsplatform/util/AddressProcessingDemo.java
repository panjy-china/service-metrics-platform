package org.panjy.servicemetricsplatform.util;

import org.panjy.servicemetricsplatform.entity.WechatMessageAnalyzeAddress;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 地址处理演示类
 */
@Component
public class AddressProcessingDemo {
    
    public static void main(String[] args) {
        // 创建测试数据
        List<WechatMessageAnalyzeAddress> testRecords = createTestRecords();
        
        // 创建AddressTypeHandler实例
        AddressTypeHandler addressTypeHandler = new AddressTypeHandler();
        
        System.out.println("前10条记录的地址处理结果：");
        System.out.println("=====================================");
        
        for (int i = 0; i < Math.min(10, testRecords.size()); i++) {
            WechatMessageAnalyzeAddress record = testRecords.get(i);
            
            // 模拟处理后的地址（这里我们直接显示处理逻辑的结果）
            String processedAddress = simulateAddressProcessing(record.getAddress());
            
            System.out.println("记录 " + (i + 1) + ":");
            System.out.println("  微信ID: " + record.getWechatId());
            System.out.println("  时间: " + record.getWechatTime());
            System.out.println("  原始地址: " + record.getAddress());
            System.out.println("  处理后地址: " + processedAddress);
            System.out.println("  消息类型: " + record.getMsgType());
            System.out.println("  内容: " + record.getContent());
            System.out.println("-------------------------------------");
        }
        
        System.out.println("总共处理了 " + Math.min(10, testRecords.size()) + " 条记录");
    }
    
    /**
     * 模拟地址处理过程
     * @param address 原始地址
     * @return 处理后的地址
     */
    private static String simulateAddressProcessing(String address) {
        // 创建AddressTypeHandler实例
        AddressTypeHandler handler = new AddressTypeHandler();
        
        // 由于TypeHandler是用于MyBatis的，我们这里直接模拟处理逻辑
        return processAddressWithDashes(address);
    }
    
    /**
     * 处理包含"-"的地址
     * @param address 原始地址
     * @return 处理后的地址
     */
    private static String processAddressWithDashes(String address) {
        // 检查是否包含至少两个"-"
        if (address == null || countChar(address, '-') < 2) {
            // 不足两个"-"的地址不进行处理
            return address;
        }
        
        // 按"-"分割地址
        String[] parts = address.split("-");
        
        // 构建新地址格式
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i].trim();
            
            if (i < parts.length - 1) {
                // 非最后一部分，取前两个字符
                if (part.length() >= 2) {
                    result.append(part.substring(0, 2));
                } else {
                    result.append(part);
                }
                
                // 添加分隔符
                result.append("%");
            } else {
                // 最后一部分，特殊处理
                result.append(processLastPart(part));
            }
        }
        
        return result.toString();
    }
    
    /**
     * 处理最后一部分
     * @param lastPart 最后一部分
     * @return 处理后的最后一部分
     */
    private static String processLastPart(String lastPart) {
        // 取前两个字符
        String prefix = lastPart.length() >= 2 ? lastPart.substring(0, 2) : lastPart;
        
        // 提取所有数字
        StringBuilder allDigits = new StringBuilder();
        for (char c : lastPart.toCharArray()) {
            if (Character.isDigit(c)) {
                allDigits.append(c);
            }
        }
        
        // 如果没有数字，只返回前缀
        if (allDigits.length() == 0) {
            return prefix;
        }
        
        String digits = allDigits.toString();
        
        // 查找最后一个非数字字符（如"号"）
        String suffix = "";
        for (int i = lastPart.length() - 1; i >= 0; i--) {
            if (!Character.isDigit(lastPart.charAt(i)) && lastPart.charAt(i) != '-') {
                suffix = String.valueOf(lastPart.charAt(i));
                break;
            }
        }
        
        // 根据数字长度处理
        if (digits.length() == 1) {
            // 只有一位数字，格式为"前缀%数字+后缀"
            return prefix + "%" + digits + suffix;
        } else if (digits.length() == 2) {
            // 两位数字，格式为"前缀%第一位数字%第二位数字+后缀"
            return prefix + "%" + digits.charAt(0) + "%" + digits.charAt(1) + suffix;
        } else {
            // 多于两位数字，格式为"前缀%倒数第二位数字%最后两位数字"
            // 但需要保留后缀
            char secondLast = digits.charAt(digits.length() - 2);
            String lastTwo = digits.substring(digits.length() - 2);
            
            // 如果有后缀，则添加到末尾
            if (!suffix.isEmpty()) {
                return prefix + "%" + secondLast + "%" + lastTwo + suffix;
            } else {
                return prefix + "%" + secondLast + "%" + lastTwo;
            }
        }
    }
    
    /**
     * 统计字符串中某个字符的出现次数
     * @param str 字符串
     * @param ch 要统计的字符
     * @return 字符出现次数
     */
    private static int countChar(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 创建测试数据
     * @return 测试记录列表
     */
    private static List<WechatMessageAnalyzeAddress> createTestRecords() {
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
                
        return testRecords;
    }
}