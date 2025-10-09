package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.ServerAccount;
import org.panjy.servicemetricsplatform.entity.WechatMember;
import org.panjy.servicemetricsplatform.entity.WechatMessageAnalyzeAddress;
import org.panjy.servicemetricsplatform.mapper.WechatMessageAnalyzeAddressMapper;
import org.panjy.servicemetricsplatform.mapper.WechatMemberMapper;
import org.panjy.servicemetricsplatform.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 地址用户映射控制器
 * 直接整合地址验证、用户查询和微信会员存储功能，避免服务间调用
 */
@RestController
@RequestMapping("/api/address-user-mapping")
public class AddressUserMappingController {

    @Autowired
    private WechatMessageAnalyzeAddressMapper addressMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    // 定义地址合法性验证的正则表达式
    private static final java.util.regex.Pattern VALID_ADDRESS_PATTERN = java.util.regex.Pattern.compile(".*[省市县区镇村街道路街巷弄号].*");
    private static final java.util.regex.Pattern INVALID_ADDRESS_PATTERN = java.util.regex.Pattern.compile(".*[!@#$%^&*()_+=\\[\\]{}|\\\\:\";'<>?,./].*");

    /**
     * 判断地址是否合法
     * 
     * @param address 处理后的地址
     * @return 是否合法
     */
    private boolean isValidAddress(String address) {
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
     * 处理地址数据并建立用户与微信会员的映射关系
     * 1. 从数据库查询地址数据并校验
     * 2. 通过模糊查询匹配地址的用户ID
     * 3. 将用户ID与微信ID存储到微信会员表中
     *
     * @return 处理结果
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processAddressDataAndCreateMapping() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            System.out.println("开始处理地址数据并建立用户与微信会员的映射关系");

            // 1. 从数据库查询所有地址记录（会自动通过AddressTypeHandler处理）
            List<WechatMessageAnalyzeAddress> addressRecords = addressMapper.selectAll(null, null);
            if (addressRecords == null || addressRecords.isEmpty()) {
                System.out.println("未查询到任何地址数据");
                
                response.put("success", false);
                response.put("message", "未查询到任何地址数据");
                response.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.ok(response);
            }

            System.out.println("共查询到 " + addressRecords.size() + " 条地址数据");

            // 2. 验证地址并筛选出合法地址
            List<WechatMessageAnalyzeAddress> validAddressRecords = new ArrayList<>();
            for (WechatMessageAnalyzeAddress record : addressRecords) {
                String address = record.getAddress();
                if (address != null && isValidAddress(address)) {
                    validAddressRecords.add(record);
                }
            }

            System.out.println("其中合法地址 " + validAddressRecords.size() + " 条");

            // 3. 为合法地址查询匹配的用户ID并建立映射关系
            List<WechatMember> mappingMembers = new ArrayList<>();
            int mappedCount = 0;

            for (WechatMessageAnalyzeAddress record : validAddressRecords) {
                String wechatId = record.getWechatId();
                String address = record.getAddress();

                // 通过模糊查询获取用户ID
                String userId = orderMapper.getCltIdByProcessedAddress(address);
                if (userId != null && !userId.trim().isEmpty()) {
                    // 检查是否已存在该微信ID的记录
                    int count = wechatMemberMapper.countByWechatId(wechatId);
                    if (count == 0) {
                        // 创建微信会员记录来存储映射关系
                        try {
                            WechatMember member = new WechatMember();
                            member.setWechatId(wechatId);
                            member.setColCltID(userId);
                            mappingMembers.add(member);
                            mappedCount++;
                        } catch (Exception e) {
                            System.err.println("创建映射记录时发生错误: " + e.getMessage());
                        }
                    }
                }
            }

            System.out.println("成功建立映射关系 " + mappedCount + " 条");

            // 4. 批量插入映射记录到微信会员表
            if (!mappingMembers.isEmpty()) {
                int insertedCount = wechatMemberMapper.batchInsert(mappingMembers);
                System.out.println("成功插入 " + insertedCount + " 条映射记录到微信会员表");
                
                response.put("success", true);
                response.put("message", "处理完成，共建立 " + mappedCount + " 条映射关系，成功插入 " + insertedCount + " 条记录");
                response.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.ok(response);
            } else {
                System.out.println("没有需要插入的映射记录");
                
                response.put("success", true);
                response.put("message", "处理完成，共建立 " + mappedCount + " 条映射关系，无需插入记录");
                response.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            System.err.println("处理地址数据并建立映射关系时发生错误: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "处理过程中发生错误: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 根据微信ID获取用户ID
     *
     * @param wechatId 微信ID
     * @return 用户ID
     */
    @GetMapping("/user-id/{wechatId}")
    public ResponseEntity<Map<String, Object>> getUserIdByWechatId(@PathVariable String wechatId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 根据微信ID查找对应的微信会员记录
            WechatMember member = wechatMemberMapper.selectByWechatId(wechatId);
            if (member != null) {
                String userId = member.getColCltID();
                
                response.put("success", true);
                response.put("userId", userId);
                response.put("wechatId", wechatId);
                response.put("message", "查询成功");
                response.put("timestamp", System.currentTimeMillis());
                return ResponseEntity.ok(response);
            }
            
            response.put("success", false);
            response.put("wechatId", wechatId);
            response.put("message", "未找到对应的用户ID");
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("根据微信ID查询用户ID时发生错误: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("message", "查询过程中发生错误: " + e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }
}