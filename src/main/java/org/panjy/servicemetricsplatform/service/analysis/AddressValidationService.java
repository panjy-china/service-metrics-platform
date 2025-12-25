package org.panjy.servicemetricsplatform.service.analysis;

import org.panjy.servicemetricsplatform.mapper.analysis.WechatMessageAnalyzeAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 地址验证服务类
 * 提供从数据库中读取处理后的地址并判断是否为合法地址的功能
 */
@Service
public class AddressValidationService {
    
    @Autowired
    private WechatMessageAnalyzeAddressMapper addressMapper;
    
    // 定义地址合法性验证的正则表达式
    // 这里简单示例：地址应该包含省、市信息，并且不包含明显的非法字符
    private static final Pattern VALID_ADDRESS_PATTERN = Pattern.compile(".*[省市县区镇村街道路街巷弄号].*");
    private static final Pattern INVALID_ADDRESS_PATTERN = Pattern.compile(".*[!@#$%^&*()_+=\\[\\]{}|\\\\:\";'<>?,./].*");
    
    /**
     * 从数据库中读取所有处理后的地址
     * 
     * @return 处理后的地址列表
     */
    public List<String> getAllProcessedAddresses() {
        try {
            return addressMapper.getAllProcessedAddresses();
        } catch (Exception e) {
            System.err.println("获取所有处理后的地址时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 从数据库中读取所有处理后的地址及其对应的微信ID
     * 
     * @return 包含微信ID和处理后地址的映射列表
     */
    public List<Map<String, String>> getAllProcessedAddressWithWechatId() {
        try {
            return addressMapper.getAllProcessedAddressWithWechatId();
        } catch (Exception e) {
            System.err.println("获取所有处理后的地址及其对应的微信ID时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 判断地址是否合法
     * 
     * @param address 处理后的地址
     * @return 是否合法
     */
    public boolean isValidAddress(String address) {
        // 空地址不合法
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        
        // 去除首尾空格
        String trimmedAddress = address.trim();
        
        // 长度过短的地址不合法（简单判断）
        if (trimmedAddress.length() < 3) {
            return false;
        }
        
        // 新增验证规则：如果地址中包含两个以上的"%"，则认为是合法的
        if (countChar(trimmedAddress, '%') >= 2) {
            return true;
        }
        
        // 包含明显非法字符的地址不合法
        if (INVALID_ADDRESS_PATTERN.matcher(trimmedAddress).matches()) {
            return false;
        }
        
        // 符合基本地址格式要求的地址认为合法
        return VALID_ADDRESS_PATTERN.matcher(trimmedAddress).matches();
    }
    
    /**
     * 统计字符串中某个字符的出现次数
     * 
     * @param str 字符串
     * @param ch 要统计的字符
     * @return 字符出现次数
     */
    private int countChar(String str, char ch) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * 验证所有处理后的地址并返回结果
     * 
     * @return 地址验证结果列表
     */
    public List<AddressValidationResult> validateAllProcessedAddresses() {
        try {
            List<String> addresses = getAllProcessedAddresses();
            if (addresses == null) {
                return null;
            }
            
            return addresses.stream()
                    .map(address -> new AddressValidationResult(address, isValidAddress(address)))
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            System.err.println("验证所有处理后的地址时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 验证所有处理后的地址及其对应的微信ID并返回结果
     * 
     * @return 地址验证结果映射列表
     */
    public List<Map<String, Object>> validateAllProcessedAddressWithWechatId() {
        try {
            List<Map<String, String>> addressWithWechatIdList = getAllProcessedAddressWithWechatId();
            if (addressWithWechatIdList == null) {
                return null;
            }
            
            return addressWithWechatIdList.stream()
                    .map(map -> {
                        String wechatId = map.get("wechatId");
                        String address = map.get("processedAddress");
                        
                        Map<String, Object> resultMap = new java.util.HashMap<>();
                        resultMap.put("wechatId", wechatId);
                        resultMap.put("address", address);
                        resultMap.put("valid", isValidAddress(address));
                        
                        return resultMap;
                    })
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            System.err.println("验证所有处理后的地址及其对应的微信ID时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 地址验证结果封装类
     */
    public static class AddressValidationResult {
        private String address;
        private boolean valid;
        
        public AddressValidationResult(String address, boolean valid) {
            this.address = address;
            this.valid = valid;
        }
        
        public String getAddress() {
            return address;
        }
        
        public void setAddress(String address) {
            this.address = address;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        @Override
        public String toString() {
            return "AddressValidationResult{" +
                    "address='" + address + '\'' +
                    ", valid=" + valid +
                    '}';
        }
    }
}