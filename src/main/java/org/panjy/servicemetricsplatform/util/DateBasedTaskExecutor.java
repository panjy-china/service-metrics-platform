package org.panjy.servicemetricsplatform.util;

import org.panjy.servicemetricsplatform.controller.AnalysisAddressController;
import org.panjy.servicemetricsplatform.controller.DietaryGuidanceAnalysisController;
import org.panjy.servicemetricsplatform.controller.MealCheckinAnalysisController;
import org.panjy.servicemetricsplatform.controller.ServerTimeController;
import org.panjy.servicemetricsplatform.controller.UserFirstFeedbackController;
import org.panjy.servicemetricsplatform.controller.DateBasedAnalysisController;
import org.panjy.servicemetricsplatform.entity.WechatMember;
import org.panjy.servicemetricsplatform.entity.WechatMessageAnalyzeAddress;
import org.panjy.servicemetricsplatform.mapper.WechatMessageAnalyzeAddressMapper;
import org.panjy.servicemetricsplatform.mapper.WechatMemberMapper;
import org.panjy.servicemetricsplatform.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 基于指定日期的任务执行器
 * 按照定时任务的顺序执行所有任务，并且所有任务都按照给定的日期进行分析
 */
@Component
public class DateBasedTaskExecutor {

    @Autowired
    private AnalysisAddressController analysisAddressController;

    @Autowired
    private DietaryGuidanceAnalysisController dietaryGuidanceAnalysisController;

    @Autowired
    private MealCheckinAnalysisController mealCheckinAnalysisController;

    @Autowired
    private ServerTimeController serverTimeController;

    @Autowired
    private UserFirstFeedbackController userFirstFeedbackController;

    @Autowired
    private DateBasedAnalysisController dateBasedAnalysisController;

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
     * 按顺序执行所有定时任务（支持指定日期）
     * 所有任务都按照给定的日期进行分析
     *
     * @param date 指定的日期
     */
    public void executeAllTasksInOrder(Date date) {
        System.out.println("开始执行基于指定日期的任务 - " + dateFormat.format(date));
        
        try {
            // 1. 首先执行地址分析任务
            performAddressAnalysis(date);
            
            // 2. 然后执行地址用户映射任务
            processAddressDataAndCreateMapping(date);
            
            // 3. 执行饮食指导分析任务
            performDietaryGuidanceAnalysis(date);
            
            // 4. 执行基于日期的对话分析任务
            performDateBasedConversationAnalysis(date);
            
            // 5. 执行三餐打卡分析任务
            performMealCheckinAnalysis(date);
            
            // 6. 执行服务时间处理任务
            performServerTimeProcessing(date);
            
            // 7. 执行用户首次反馈统计任务
            performUserFirstFeedbackStatistics(date);
            
            System.out.println("基于指定日期的任务执行完成 - " + dateFormat.format(date));
        } catch (Exception e) {
            System.err.println("基于指定日期的任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行地址分析任务
     * 使用指定日期进行分析
     */
    private void performAddressAnalysis(Date date) {
        System.out.println("开始执行任务：地址分析 - " + dateFormat.format(date));
        
        try {
            // 调用地址分析接口，使用指定日期
            ResponseEntity<?> response = analysisAddressController.findMessagesLikeAddress(date);
            
            System.out.println("任务执行完成：地址分析 - " + dateFormat.format(date));
            System.out.println("响应结果: " + response.getBody());
        } catch (Exception e) {
            System.err.println("地址分析任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理指定日期的地址信息并匹配用户
     */
    private void processAddressDataAndCreateMapping(Date date) {
        System.out.println("开始执行任务：处理指定日期的地址信息并匹配用户 - " + dateFormat.format(date));

        try {
            System.out.println("处理日期: " + new SimpleDateFormat("yyyy-MM-dd").format(date));

            // 1. 从数据库查询指定日期的地址记录
            List<WechatMessageAnalyzeAddress> addressRecords = addressMapper.selectByYesterday(date);
            if (addressRecords == null || addressRecords.isEmpty()) {
                System.out.println("未查询到指定日期的地址数据");
                return;
            }

            System.out.println("共查询到指定日期的地址数据 " + addressRecords.size() + " 条");

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
                System.out.println("任务执行完成：处理指定日期的地址信息并匹配用户 - " + dateFormat.format(date));
            } else {
                System.out.println("没有需要插入的映射记录");
                System.out.println("任务执行完成：处理指定日期的地址信息并匹配用户 - " + dateFormat.format(date));
            }

        } catch (Exception e) {
            System.err.println("处理指定日期的地址信息并匹配用户时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行饮食指导分析任务
     * 
     * @param date 指定的日期
     */
    public void performDietaryGuidanceAnalysis(Date date) {
        System.out.println("开始执行任务：饮食指导分析 - " + dateFormat.format(date));
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String analysisDate = sdf.format(date);
            
            // 调用饮食指导分析接口
            ResponseEntity<String> response = dietaryGuidanceAnalysisController.batchAnalyzeDietaryGuidance(analysisDate);
            
            System.out.println("任务执行完成：饮食指导分析 - " + dateFormat.format(date));
            System.out.println("响应结果: " + response.getBody());
        } catch (Exception e) {
            System.err.println("饮食指导分析任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行三餐打卡分析任务
     * 
     * @param date 指定的日期
     */
    public void performMealCheckinAnalysis(Date date) {
        System.out.println("开始执行任务：三餐打卡分析 - " + dateFormat.format(date));
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String analysisDate = sdf.format(date);
            
            // 调用三餐打卡分析接口
            MealCheckinAnalysisController.ApiResponse response = mealCheckinAnalysisController.analyzeAndStoreMealCheckin(analysisDate);
            
            System.out.println("任务执行完成：三餐打卡分析 - " + dateFormat.format(date));
            System.out.println("响应结果: " + response.toString());
        } catch (Exception e) {
            System.err.println("三餐打卡分析任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行服务时间处理任务
     * 
     * @param date 指定的日期
     */
    public void performServerTimeProcessing(Date date) {
        System.out.println("开始执行任务：服务时间处理 - " + dateFormat.format(date));
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String processDate = sdf.format(date);
            
            // 调用服务时间处理接口
            ResponseEntity<?> response = serverTimeController.processServerTimeAfterDate(processDate);
            
            System.out.println("任务执行完成：服务时间处理 - " + dateFormat.format(date));
            System.out.println("响应结果: " + response.getBody());
        } catch (Exception e) {
            System.err.println("服务时间处理任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行用户首次反馈统计任务
     * 
     * @param date 指定的日期
     */
    public void performUserFirstFeedbackStatistics(Date date) {
        System.out.println("开始执行任务：用户首次反馈统计 - " + dateFormat.format(date));
        
        try {
            // 调用用户首次反馈统计接口
            ResponseEntity<Map<String, Object>> tonguePhotoRateResponse = userFirstFeedbackController.getTonguePhotoSubmissionRate();
            ResponseEntity<Map<String, Object>> bodyTypePhotoRateResponse = userFirstFeedbackController.getBodyTypePhotoSubmissionRate();
            ResponseEntity<Map<String, Object>> basicInfoRateResponse = userFirstFeedbackController.getBasicInfoSubmissionRate();
            
            System.out.println("舌苔照片提交比例: " + tonguePhotoRateResponse.getBody());
            System.out.println("体型照片提交比例: " + bodyTypePhotoRateResponse.getBody());
            System.out.println("基础资料提交比例: " + basicInfoRateResponse.getBody());
            
            System.out.println("任务执行完成：用户首次反馈统计 - " + dateFormat.format(date));
        } catch (Exception e) {
            System.err.println("用户首次反馈统计任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 执行基于日期的对话分析任务
     * 
     * @param date 指定的日期
     */
    public void performDateBasedConversationAnalysis(Date date) {
        System.out.println("开始执行任务：基于日期的对话分析 - " + dateFormat.format(date));
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String analysisDate = sdf.format(date);
            
            // 调用基于日期的对话分析接口
            ResponseEntity<Map<String, Object>> response = dateBasedAnalysisController.analyzeConversationsAfterDate(analysisDate);
            
            System.out.println("任务执行完成：基于日期的对话分析 - " + dateFormat.format(date));
            System.out.println("响应结果: " + response.getBody());
        } catch (Exception e) {
            System.err.println("基于日期的对话分析任务执行失败: " + e.getMessage());
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