package org.panjy.servicemetricsplatform.util;

import org.panjy.servicemetricsplatform.controller.AnalysisAddressController;
import org.panjy.servicemetricsplatform.controller.DietaryGuidanceAnalysisController;
import org.panjy.servicemetricsplatform.controller.LLMAnalysisController;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * 统一定时任务类，整合所有定时任务并按顺序执行
 */
@Component
public class UnifiedScheduledTask {

    @Autowired
    private AnalysisAddressController analysisAddressController;

    @Autowired
    private DietaryGuidanceAnalysisController dietaryGuidanceAnalysisController;

    // @Autowired
    // private LLMAnalysisController llmAnalysisController;  // 已移除，由DateBasedAnalysisController替代

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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // 定义地址合法性验证的正则表达式
    private static final Pattern VALID_ADDRESS_PATTERN = Pattern.compile(".*[省市县区镇村街道路街巷弄号].*");
    private static final Pattern INVALID_ADDRESS_PATTERN = Pattern.compile(".*[!@#$%^&*()_+=\\[\\]{}|\\\\:\";'<>?,./].*");

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 每天凌晨1点执行，按顺序执行所有定时任务
     * 1. 处理昨天新增的地址信息并匹配用户
     * 2. 调用地址分析接口
     * 3. 执行饮食指导分析任务
     * 4. 执行基于日期的对话分析任务（替换原有的舌苔体型分析）
     * 5. 执行三餐打卡分析任务
     * 6. 执行服务时间处理任务
     * 7. 执行用户首次反馈统计任务
     * cron表达式：0 0 1 * * ? 表示每天凌晨1点执行
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void executeAllScheduledTasksInOrder() {
        System.out.println("开始执行统一定时任务 - " + dateFormat.format(new Date()));
        
        try {
            // 1. 首先执行地址分析任务
            scheduledAddressAnalysisWithSpring();
            
            // 2. 然后执行地址用户映射任务
            processYesterdayAddressDataAndCreateMapping();
            
            // 3. 执行饮食指导分析任务
            performDietaryGuidanceAnalysis();
            
            // 4. 执行基于日期的对话分析任务（替换原有的舌苔体型分析）
            performDateBasedConversationAnalysis();
            
            // 5. 执行三餐打卡分析任务
            performMealCheckinAnalysis();
            
            // 6. 执行服务时间处理任务
            performServerTimeProcessing();
            
            // 7. 执行用户首次反馈统计任务
            performUserFirstFeedbackStatistics();
            
            System.out.println("统一定时任务执行完成 - " + dateFormat.format(new Date()));
        } catch (Exception e) {
            System.err.println("统一定时任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 按顺序执行所有定时任务（支持指定日期）
     * 
     * @param date 指定的日期
     */
    public void executeAllScheduledTasksInOrder(Date date) {
        System.out.println("开始执行统一定时任务 - " + dateFormat.format(date));
        
        try {
            // 1. 首先执行地址分析任务
            // 注意：地址分析任务使用指定的日期
            scheduledAddressAnalysisWithSpring(date);
            
            // 2. 然后执行地址用户映射任务
            // 注意：地址用户映射任务使用指定的日期
            processYesterdayAddressDataAndCreateMapping(date);
            
            // 3. 执行饮食指导分析任务
            performDietaryGuidanceAnalysis(date);
            
            // 4. 执行基于日期的对话分析任务（替换原有的舌苔体型分析）
            performDateBasedConversationAnalysis(date);
            
            // 5. 执行三餐打卡分析任务
            performMealCheckinAnalysis(date);
            
            // 6. 执行服务时间处理任务
            performServerTimeProcessing(date);
            
            // 7. 执行用户首次反馈统计任务
            performUserFirstFeedbackStatistics(date);
            
            System.out.println("统一定时任务执行完成 - " + dateFormat.format(date));
        } catch (Exception e) {
            System.err.println("统一定时任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理昨天新增的地址信息并匹配用户
     */
    private void processYesterdayAddressDataAndCreateMapping() {
        System.out.println("开始执行任务：处理昨天新增的地址信息并匹配用户 - " + dateFormat.format(new Date()));

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
                System.out.println("任务执行完成：处理昨天新增的地址信息并匹配用户 - " + dateFormat.format(new Date()));
            } else {
                System.out.println("没有需要插入的映射记录");
                System.out.println("任务执行完成：处理昨天新增的地址信息并匹配用户 - " + dateFormat.format(new Date()));
            }

        } catch (Exception e) {
            System.err.println("处理昨天新增的地址信息并匹配用户时发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 处理指定日期的地址信息并匹配用户
     */
    private void processYesterdayAddressDataAndCreateMapping(Date date) {
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
     * 调用地址分析接口
     */
    private void scheduledAddressAnalysisWithSpring() {
        System.out.println("开始执行任务：地址分析 - " + dateFormat.format(new Date()));
        
        try {
            // 获取昨天的日期作为分析目标
            Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            
            // 调用地址分析接口
            ResponseEntity<?> response = analysisAddressController.findMessagesLikeAddress(yesterday);
            
            System.out.println("任务执行完成：地址分析 - " + dateFormat.format(new Date()));
            System.out.println("响应结果: " + response.getBody());
        } catch (Exception e) {
            System.err.println("地址分析任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 调用地址分析接口（支持指定日期）
     */
    private void scheduledAddressAnalysisWithSpring(Date date) {
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
     * 执行饮食指导分析任务
     * 每天凌晨1点执行完其他任务后执行
     */
    private void performDietaryGuidanceAnalysis() {
        System.out.println("开始执行任务：饮食指导分析 - " + dateFormat.format(new Date()));
        
        try {
            // 计算昨天的日期作为分析目标
            Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String analysisDate = sdf.format(yesterday);
            
            // 调用饮食指导分析接口
            ResponseEntity<String> response = dietaryGuidanceAnalysisController.batchAnalyzeDietaryGuidance(analysisDate);
            
            System.out.println("任务执行完成：饮食指导分析 - " + dateFormat.format(new Date()));
            System.out.println("响应结果: " + response.getBody());
        } catch (Exception e) {
            System.err.println("饮食指导分析任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 执行饮食指导分析任务（支持指定日期）
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

    // 已移除舌苔和体型照片分析任务，由基于日期的对话分析任务替代

    /**
     * 执行三餐打卡分析任务
     * 每天凌晨1点执行完其他任务后执行
     */
    private void performMealCheckinAnalysis() {
        System.out.println("开始执行任务：三餐打卡分析 - " + dateFormat.format(new Date()));
        
        try {
            // 计算昨天的日期作为分析目标
            Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String analysisDate = sdf.format(yesterday);
            
            // 调用三餐打卡分析接口
            MealCheckinAnalysisController.ApiResponse response = mealCheckinAnalysisController.analyzeAndStoreMealCheckin(analysisDate);
            
            System.out.println("任务执行完成：三餐打卡分析 - " + dateFormat.format(new Date()));
            System.out.println("响应结果: " + response.toString());
        } catch (Exception e) {
            System.err.println("三餐打卡分析任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 执行三餐打卡分析任务（支持指定日期）
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
     * 每天凌晨1点执行完其他任务后执行
     */
    private void performServerTimeProcessing() {
        System.out.println("开始执行任务：服务时间处理 - " + dateFormat.format(new Date()));
        
        try {
            // 计算昨天的日期作为处理目标
            Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String processDate = sdf.format(yesterday);
            
            // 调用服务时间处理接口
            ResponseEntity<?> response = serverTimeController.processServerTimeAfterDate(processDate);
            
            System.out.println("任务执行完成：服务时间处理 - " + dateFormat.format(new Date()));
            System.out.println("响应结果: " + response.getBody());
        } catch (Exception e) {
            System.err.println("服务时间处理任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 执行服务时间处理任务（支持指定日期）
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
     * 每天凌晨1点执行完其他任务后执行
     */
    private void performUserFirstFeedbackStatistics() {
        System.out.println("开始执行任务：用户首次反馈统计 - " + dateFormat.format(new Date()));
        
        try {
            // 调用用户首次反馈统计接口
            ResponseEntity<Map<String, Object>> tonguePhotoRateResponse = userFirstFeedbackController.getTonguePhotoSubmissionRate();
            ResponseEntity<Map<String, Object>> bodyTypePhotoRateResponse = userFirstFeedbackController.getBodyTypePhotoSubmissionRate();
            ResponseEntity<Map<String, Object>> basicInfoRateResponse = userFirstFeedbackController.getBasicInfoSubmissionRate();
            
            System.out.println("舌苔照片提交比例: " + tonguePhotoRateResponse.getBody());
            System.out.println("体型照片提交比例: " + bodyTypePhotoRateResponse.getBody());
            System.out.println("基础资料提交比例: " + basicInfoRateResponse.getBody());
            
            System.out.println("任务执行完成：用户首次反馈统计 - " + dateFormat.format(new Date()));
        } catch (Exception e) {
            System.err.println("用户首次反馈统计任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 执行用户首次反馈统计任务（支持指定日期）
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
     * 每天凌晨1点执行完其他任务后执行
     */
    private void performDateBasedConversationAnalysis() {
        System.out.println("开始执行任务：基于日期的对话分析 - " + dateFormat.format(new Date()));
        
        try {
            // 计算昨天的日期作为分析目标
            Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String analysisDate = sdf.format(yesterday);
            
            // 调用基于日期的对话分析接口
            ResponseEntity<Map<String, Object>> response = dateBasedAnalysisController.analyzeConversationsAfterDate(analysisDate);
            
            System.out.println("任务执行完成：基于日期的对话分析 - " + dateFormat.format(new Date()));
            System.out.println("响应结果: " + response.getBody());
        } catch (Exception e) {
            System.err.println("基于日期的对话分析任务执行失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 执行基于日期的对话分析任务（支持指定日期）
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
     * 使用ScheduledExecutorService实现定时任务
     * 这种方式提供了更多的控制选项
     */
    public void scheduledAddressAnalysisWithExecutor() {
        // 计算到下一个中午12点的时间差
        long initialDelay = computeNextNoonDelay();
        
        // 安排任务每天执行
        scheduler.scheduleAtFixedRate(() -> {
            try {
                System.out.println("开始执行ScheduledExecutorService地址分析任务: " + dateFormat.format(new Date()));
                
                // 获取昨天的日期作为分析目标
                Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
                
                // 调用地址分析接口
                ResponseEntity<?> response = analysisAddressController.findMessagesLikeAddress(yesterday);
                
                System.out.println("ScheduledExecutorService地址分析任务执行完成: " + dateFormat.format(new Date()));
                System.out.println("响应结果: " + response.getBody());
            } catch (Exception e) {
                System.err.println("ScheduledExecutorService地址分析任务执行失败: " + e.getMessage());
                e.printStackTrace();
            }
        }, initialDelay, TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS); // 每天执行一次
    }
    
    /**
     * 计算距离下一个中午12点的时间差（秒）
     * @return 到下一个中午12点的时间差（秒）
     */
    private long computeNextNoonDelay() {
        Date now = new Date();
        Date noonToday = getNoonDate(now);
        
        // 如果现在已经是下午，则下一个中午是明天
        if (now.after(noonToday)) {
            noonToday = new Date(noonToday.getTime() + TimeUnit.DAYS.toMillis(1));
        }
        
        return TimeUnit.MILLISECONDS.toSeconds(noonToday.getTime() - now.getTime());
    }
    
    /**
     * 获取指定日期的中午12点时间
     * @param date 指定日期
     * @return 该日期的中午12点时间
     */
    private Date getNoonDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date datePart = sdf.parse(sdf.format(date));
            return new Date(datePart.getTime() + TimeUnit.HOURS.toMillis(12));
        } catch (Exception e) {
            return new Date();
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
    
    /**
     * 关闭调度器
     */
    public void shutdownScheduler() {
        if (!scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
}