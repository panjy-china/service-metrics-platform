package org.panjy.servicemetricsplatform.util;

import org.panjy.servicemetricsplatform.entity.WechatMember;
import org.panjy.servicemetricsplatform.entity.WechatMessageAnalyzeAddress;
import org.panjy.servicemetricsplatform.mapper.WechatMessageAnalyzeAddressMapper;
import org.panjy.servicemetricsplatform.mapper.WechatMemberMapper;
import org.panjy.servicemetricsplatform.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 定时任务类，用于每天处理昨天新增的地址信息并匹配用户
 */
@Component
public class ScheduledAddressUserMappingTask {

    @Autowired
    private WechatMessageAnalyzeAddressMapper addressMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private WechatMemberMapper wechatMemberMapper;

    // 定义地址合法性验证的正则表达式
    private static final Pattern VALID_ADDRESS_PATTERN = Pattern.compile(".*[省市县区镇村街道路街巷弄号].*");
    private static final Pattern INVALID_ADDRESS_PATTERN = Pattern.compile(".*[!@#$%^&*()_+=\\[\\]{}|\\\\:\";'<>?,./].*");

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 每天凌晨12点执行，处理昨天新增的地址信息并匹配用户
     * cron表达式：0 0 12 * * ? 表示每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void processYesterdayAddressDataAndCreateMapping() {
        System.out.println("开始执行定时任务：处理昨天新增的地址信息并匹配用户 - " + dateFormat.format(new Date()));

        try {
            // 计算昨天的日期
            Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000L);
            System.out.println("处理日期: " + new SimpleDateFormat("yyyy-MM-dd").format(yesterday));

            // 1. 从数据库查询昨天新增的地址记录
            List<WechatMessageAnalyzeAddress> addressRecords = addressMapper.selectByYesterday(yesterday);
            if (addressRecords == null || addressRecords.isEmpty()) {
                System.out.println("未查询到昨天新增的地址数据");
                return;
            }

            System.out.println("共查询到昨天新增的地址数据 " + addressRecords.size() + " 条");

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
                    } else {
                        System.out.println("微信ID " + wechatId + " 的映射关系已存在，跳过");
                    }
                }
            }

            System.out.println("成功建立映射关系 " + mappedCount + " 条");

            // 4. 批量插入映射记录到微信会员表
            if (!mappingMembers.isEmpty()) {
                int insertedCount = wechatMemberMapper.batchInsert(mappingMembers);
                System.out.println("成功插入 " + insertedCount + " 条映射记录到微信会员表");
                System.out.println("定时任务执行完成：处理昨天新增的地址信息并匹配用户 - " + dateFormat.format(new Date()));
            } else {
                System.out.println("没有需要插入的映射记录");
                System.out.println("定时任务执行完成：处理昨天新增的地址信息并匹配用户 - " + dateFormat.format(new Date()));
            }

        } catch (Exception e) {
            System.err.println("处理昨天新增的地址信息并匹配用户时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

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
}