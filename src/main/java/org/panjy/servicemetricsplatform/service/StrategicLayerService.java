package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.WechatActivity;
import org.panjy.servicemetricsplatform.mapper.WechatAccountMapper;
import org.panjy.servicemetricsplatform.mapper.WechatActivityMapper;
import org.panjy.servicemetricsplatform.mapper.WechatGroupMessageMapper;
import org.panjy.servicemetricsplatform.mapper.WechatMemberMapper;
import org.panjy.servicemetricsplatform.mapper.WechatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class StrategicLayerService {
    private static final Logger logger = LoggerFactory.getLogger(StrategicLayerService.class);
    
    @Autowired
    WechatMessageMapper wechatMessageMapper;
    @Autowired
    WechatGroupMessageMapper wechatGroupMessageMapper;
    @Autowired
    WechatMemberMapper wechatMemberMapper;
    @Autowired
    WechatActivityMapper wechatActivityMapper;
    @Autowired
    WechatAccountMapper wechatAccountMapper;

    /**
     * 查询一天内的新增用户
     *
     * @param begin 一天的开始时间（例如 2025-08-28 00:00:00）
     * @return 新增用户的微信 ID 列表
     */
    public List<String> findNewUserByDay(Date begin) {
        logger.info("开始查询一天内的新增用户，开始时间: {}", begin);
        
        // 计算一天的结束时间（当天最后一秒），去除毫秒部分
        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        Date end = cal.getTime();
        
        logger.info("查询时间范围: {} - {}", begin, end);

        // 调用 Mapper 方法
        List<String> newUsers = wechatMessageMapper.findMessageByContent("你已添加了", begin, end);
        logger.info("查询到一天内新增用户数: {}", newUsers != null ? newUsers.size() : 0);
        
        return newUsers;
    }

    /**
     * 查询一周内的新增用户
     *
     * @param week 某一周的任意一天（例如 2025-08-28 10:30:00）
     * @return 新增用户的微信 ID 列表
     */
    public List<String> findNewUserByWeek(Date week) {
        logger.info("开始查询一周内的新增用户，参考时间: {}", week);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(week);

        // 设置为本周第一天（周一）00:00:00
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date begin = cal.getTime();

        // 设置为下周第一天减 1 秒（即本周最后一秒），去除毫秒部分
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        cal.add(Calendar.SECOND, -1);
        cal.set(Calendar.MILLISECOND, 0);
        Date end = cal.getTime();
        
        logger.info("查询时间范围: {} - {}", begin, end);

        // 调用 Mapper 方法
        List<String> newUsers = wechatMessageMapper.findMessageByContent("你已添加了", begin, end);
        logger.info("查询到一周内新增用户数: {}", newUsers != null ? newUsers.size() : 0);
        
        return newUsers;
    }

    /**
     * 查询一个月内的新增用户
     *
     * @param month 某个月份的第一天（例如 2025-08-01 00:00:00）
     * @return 新增用户的微信 ID 列表
     */
    public List<String> findNewUserByMonth(Date month) {
        logger.info("开始查询一个月内的新增用户，参考时间: {}", month);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(month);

        // 设置为当月第一天 00:00:00
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date begin = cal.getTime();

        // 设置为下个月第一天减 1 秒（即本月最后一秒），去除毫秒部分
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.SECOND, -1);
        cal.set(Calendar.MILLISECOND, 0);
        Date end = cal.getTime();
        
        logger.info("查询时间范围: {} - {}", begin, end);

        // 调用 Mapper 方法
        List<String> newUsers = wechatMessageMapper.findMessageByContent("你已添加了", begin, end);
        logger.info("查询到一个月内新增用户数: {}", newUsers != null ? newUsers.size() : 0);
        
        return newUsers;
    }


    /**
     * 查询活跃用户数
     *
     * @param currentTime 当前时间
     */
    public int getActiveUserCount(Date currentTime) {
        logger.info("开始计算活跃用户数，查询日期: {}", currentTime);
        
        // 获取私聊活跃用户
        logger.info("开始查询私聊活跃用户");
        List<String> activeUsers = wechatMessageMapper.findActiveUsers(currentTime);
        logger.info("查询到私聊活跃用户数: {}", activeUsers != null ? activeUsers.size() : 0);
        
        // 获取群聊活跃用户
        logger.info("开始查询群聊活跃用户");
        List<String> groupActivateUser = wechatGroupMessageMapper.findActiveUsers(currentTime);
        logger.info("查询到群聊活跃用户数: {}", groupActivateUser != null ? groupActivateUser.size() : 0);
        
        // 获取所有职工微信ID
        logger.info("开始查询所有职工微信ID");
        List<String> staffWechatIds = wechatAccountMapper.selectAllWechatIds();
        logger.info("查询到职工微信ID数: {}", staffWechatIds != null ? staffWechatIds.size() : 0);
        
        // 转换为Set以提高查找效率
        Set<String> staffWechatSet = new HashSet<>(staffWechatIds);

        // 合并并去重 activeUsers 和 groupActivateUser
        Set<String> allActiveUsers = new HashSet<>();
        if (activeUsers != null) {
            allActiveUsers.addAll(activeUsers);
        }
        if (groupActivateUser != null) {
            allActiveUsers.addAll(groupActivateUser);
        }

        // 去除合并后结果中出现的职工
        allActiveUsers.removeAll(staffWechatSet);
        logger.info("去除职工后的活跃用户数: {}", allActiveUsers.size());

        // 返回活跃用户数
        int activeUserCount = allActiveUsers.size();
        logger.info("活跃用户数计算完成: {}", activeUserCount);
        return activeUserCount;
    }

    /**
     * 计算用户留存率
     *
     * @param currentTime  指定的日期（通常是新增用户的日期）
     * @param days         留存天数，比如 1 表示次日留存，3 表示3日留存 [3, 7, 10]
     * @return             留存率（百分比）
     */
    public double getRetentionRate(Date currentTime, int days) {
        logger.info("开始计算 {} 日留存率，观测日期: {}", days, currentTime);
        
        // 1. 获取指定日期新增的用户
        List<String> newUsers = findNewUserByDay(currentTime);
        logger.info("获取到当天新增用户数: {}", newUsers != null ? newUsers.size() : 0);
        
        if (newUsers == null || newUsers.isEmpty()) {
            // 如果当天没有新增用户，直接返回0，避免除以0
            logger.info("当天无新增用户，留存率返回 0.0");
            return 0.0;
        }

        // 2. 查询 days 天后仍然活跃的用户（私聊消息活跃）
        logger.info("开始查询 {} 天后仍然活跃的私聊用户", days);
        List<String> userServived = wechatMessageMapper.findUserServived(currentTime, days);
        logger.info("查询到 {} 天后仍然活跃的私聊用户数: {}", days, userServived != null ? userServived.size() : 0);

        // 3. 查询 days 天后仍然活跃的用户（群聊消息活跃）
        logger.info("开始查询 {} 天后仍然活跃的群聊用户", days);
        List<String> groupUserServived = wechatGroupMessageMapper.findUserServived(currentTime, days);
        logger.info("查询到 {} 天后仍然活跃的群聊用户数: {}", days, groupUserServived != null ? groupUserServived.size() : 0);

        // 4. 获取所有职工微信ID
        logger.info("开始查询所有职工微信ID");
        List<String> staffWechatIds = wechatAccountMapper.selectAllWechatIds();
        logger.info("查询到职工微信ID数: {}", staffWechatIds != null ? staffWechatIds.size() : 0);
        Set<String> staffWechatSet = new HashSet<>(staffWechatIds);

        // 5. 合并群聊和私聊的活跃用户，并去除职工
        Set<String> activeUsers = new HashSet<>();
        if (userServived != null) {
            activeUsers.addAll(userServived);
        }
        if (groupUserServived != null) {
            activeUsers.addAll(groupUserServived);
        }
        activeUsers.removeAll(staffWechatSet); // 去除职工用户
        logger.info("合并去重后的活跃用户数（已去除职工）: {}", activeUsers.size());

        // 6. 从新增用户中去除职工，得到真实的客户新增用户
        Set<String> realNewUsers = new HashSet<>(newUsers);
        realNewUsers.removeAll(staffWechatSet);
        logger.info("去除职工后的实际新增客户数: {}", realNewUsers.size());
        
        if (realNewUsers.isEmpty()) {
            // 如果去除职工后没有新增客户，返回0
            logger.info("去除职工后无新增客户，留存率返回 0.0");
            return 0.0;
        }

        // 7. 计算留存用户（只统计新增客户里仍然活跃的）
        Set<String> retainedUsers = new HashSet<>(realNewUsers);
        retainedUsers.retainAll(activeUsers); // 保证 retainedUsers ⊆ realNewUsers
        logger.info("属于新增客户的留存用户数: {}", retainedUsers.size());

        // 8. 留存率 = 留存用户数 ÷ 新增客户数 × 100%
        double retentionRate = retainedUsers.size() * 100.0 / realNewUsers.size();
        logger.info("{} 日留存率计算完成: {} / {} = {}%", days, retainedUsers.size(), realNewUsers.size(), retentionRate);
        return retentionRate;
    }

    /**
     * 计算指定日期的用户流失率 (Churn Rate)。
     * <p>
     * 流失率定义为：当天新增用户中，在后续时间窗口内不再活跃的用户比例。
     * <br>
     * 公式: Churn Rate = (流失用户数 / 新增用户数) * 100%
     *
     * @param currentTime 当前观测日期
     * @return 流失率（百分比，范围 0.0 ~ 100.0）
     */
    public double getChurnRate(Date currentTime) {
        // 默认使用七日流失率
        logger.info("开始计算默认七日流失率，观测日期: {}", currentTime);
        double churnRate = getChurnRate(currentTime, 7);
        logger.info("默认七日流失率计算完成，结果: {}%", churnRate);
        return churnRate;
    }

    /**
     * 计算指定日期的用户流失率 (Churn Rate)。
     * <p>
     * 流失率定义为：当天新增用户中，在后续指定天数内不再活跃的用户比例。
     * <br>
     * 公式: Churn Rate = (流失用户数 / 新增用户数) * 100%
     *
     * @param currentTime 当前观测日期
     * @param days 流失观察天数（比如 7 表示七日流失率）
     * @return 流失率（百分比，范围 0.0 ~ 100.0）
     */
    public double getChurnRate(Date currentTime, int days) {
        logger.info("开始计算 {} 日流失率，观测日期: {}", days, currentTime);
        
        // 1. 获取当天新增用户
        List<String> newUsers = findNewUserByDay(currentTime);
        logger.info("获取到当天新增用户数: {}", newUsers != null ? newUsers.size() : 0);
        
        if (newUsers == null || newUsers.isEmpty()) {
            logger.info("当天无新增用户，流失率返回 0.0");
            return 0.0;
        }

        // 2. 获取流失用户（私聊 + 群聊）
        // 注意：这里使用现有的findInactiveUsers方法，该方法应该已经包含了合适的时间窗口逻辑
        // 如果需要更精确的时间窗口控制，可能需要在Mapper中添加带有days参数的方法
        logger.info("开始查询 {} 天内不活跃的私聊用户", days);
        List<String> inactiveUsers = wechatMessageMapper.findInactiveUsers(currentTime);
        logger.info("查询到不活跃的私聊用户数: {}", inactiveUsers != null ? inactiveUsers.size() : 0);
        
        logger.info("开始查询 {} 天内不活跃的群聊用户", days);
        List<String> groupInactiveUsers = wechatGroupMessageMapper.findInactiveUsers(currentTime);
        logger.info("查询到不活跃的群聊用户数: {}", groupInactiveUsers != null ? groupInactiveUsers.size() : 0);

        // 3. 获取所有职工微信ID
        logger.info("开始查询所有职工微信ID");
        List<String> staffWechatIds = wechatAccountMapper.selectAllWechatIds();
        logger.info("查询到职工微信ID数: {}", staffWechatIds != null ? staffWechatIds.size() : 0);
        Set<String> staffWechatSet = new HashSet<>(staffWechatIds);

        // 4. 合并两类流失用户，并去除职工
        Set<String> churnUsers = new HashSet<>();
        if (inactiveUsers != null) {
            churnUsers.addAll(inactiveUsers);
        }
        if (groupInactiveUsers != null) {
            churnUsers.addAll(groupInactiveUsers);
        }
        churnUsers.removeAll(staffWechatSet); // 去除职工用户
        logger.info("合并去重后的流失用户数（已去除职工）: {}", churnUsers.size());

        // 5. 从新增用户中去除职工，得到真实的客户新增用户
        Set<String> realNewUsers = new HashSet<>(newUsers);
        realNewUsers.removeAll(staffWechatSet);
        logger.info("去除职工后的实际新增客户数: {}", realNewUsers.size());
        
        if (realNewUsers.isEmpty()) {
            logger.info("去除职工后无新增客户，流失率返回 0.0");
            return 0.0;
        }

        // 6. 仅保留属于当天新增客户的流失用户
        churnUsers.retainAll(realNewUsers);
        logger.info("属于当天新增客户的流失用户数: {}", churnUsers.size());

        // 7. 计算流失率
        double churnRate = (churnUsers.size() * 100.0) / realNewUsers.size();
        logger.info("{} 日流失率计算完成: {} / {} = {}%", days, churnUsers.size(), realNewUsers.size(), churnRate);
        return churnRate;
    }

    /**
     * 计算某日开始的平均服务时间（单位：天）
     *
     * @param checkTime 指定的日期
     * @return 平均服务时长（天）
     */
    public double getAverageServiceTime(Date checkTime) {
        logger.info("开始计算平均服务时间，查询日期: {}", checkTime);
        
        // 1. 查询私聊活动
        logger.debug("开始查询私聊活动");
        List<WechatActivity> privateActivities = wechatActivityMapper.getWechatActivities(checkTime);
        logger.info("查询到私聊活动数: {}", privateActivities != null ? privateActivities.size() : 0);
        
        // 添加详细的调试信息
        if (privateActivities != null) {
            logger.debug("私聊活动详情:");
            for (int i = 0; i < privateActivities.size(); i++) {
                WechatActivity activity = privateActivities.get(i);
                logger.debug("  [{}] wechatId: {}, firstTime: {}, lastTime: {}", 
                    i, 
                    activity != null ? activity.getWechatId() : "null",
                    activity != null ? activity.getFirstActivityTime() : "null",
                    activity != null ? activity.getLastActivityTime() : "null");
            }
        } else {
            logger.warn("私聊活动查询返回null");
        }
        
        if (privateActivities == null) privateActivities = Collections.emptyList();

        // 2. 查询群聊活动
        logger.debug("开始查询群聊活动");
        List<WechatActivity> groupActivities = wechatActivityMapper.getWechatGroupActivities(checkTime);
        logger.info("查询到群聊活动数: {}", groupActivities != null ? groupActivities.size() : 0);
        
        // 添加详细的调试信息
        if (groupActivities != null) {
            logger.debug("群聊活动详情:");
            for (int i = 0; i < groupActivities.size(); i++) {
                WechatActivity activity = groupActivities.get(i);
                logger.debug("  [{}] wechatId: {}, firstTime: {}, lastTime: {}", 
                    i, 
                    activity != null ? activity.getWechatId() : "null",
                    activity != null ? activity.getFirstActivityTime() : "null",
                    activity != null ? activity.getLastActivityTime() : "null");
            }
        } else {
            logger.warn("群聊活动查询返回null");
        }
        
        if (groupActivities == null) groupActivities = Collections.emptyList();

        // 3. 获取所有职工用户列表
        logger.info("开始查询所有职工微信ID");
        List<String> staffWechatIds = wechatAccountMapper.selectAllWechatIds();
        logger.info("查询到职工微信ID数: {}", staffWechatIds != null ? staffWechatIds.size() : 0);
        Set<String> staffWechatSet = new HashSet<>(staffWechatIds);

        // 4. 合并 first/last 活动时间
        Map<String, Date> firstActivityMap = new HashMap<>();
        Map<String, Date> lastActivityMap = new HashMap<>();

        // 私聊活动：first + last
        logger.info("开始处理私聊活动数据");
        for (WechatActivity activity : privateActivities) {
            if (activity == null) continue;
            logger.debug("处理私聊活动: {}", activity);
            String wechatId = activity.getWechatId();
            
            // 添加空值检查
            if (wechatId == null || wechatId.isEmpty()) {
                logger.warn("跳过wechatId为空的私聊活动记录");
                continue;
            }

            Date firstTime = activity.getFirstActivityTime();
            Date lastTime = activity.getLastActivityTime();
            
            logger.debug("wechatId: {}, firstTime: {}, lastTime: {}", wechatId, firstTime, lastTime);

            if (firstTime != null && (!firstActivityMap.containsKey(wechatId) || firstTime.before(firstActivityMap.get(wechatId)))) {
                firstActivityMap.put(wechatId, firstTime);
            }
            if (lastTime != null && (!lastActivityMap.containsKey(wechatId) || lastTime.after(lastActivityMap.get(wechatId)))) {
                lastActivityMap.put(wechatId, lastTime);
            }
        }
        logger.info("处理完私聊活动数据，firstActivityMap大小: {}, lastActivityMap大小: {}", 
                     firstActivityMap.size(), lastActivityMap.size());

        // 群聊活动：只算 last（first 不统计，避免干扰）
        logger.info("开始处理群聊活动数据");
        for (WechatActivity activity : groupActivities) {
            if (activity == null) continue;
            logger.debug("处理群聊活动: {}", activity);
            String wechatId = activity.getWechatId();
            
            // 添加空值检查
            if (wechatId == null || wechatId.isEmpty()) {
                logger.warn("跳过wechatId为空的群聊活动记录");
                continue;
            }
            
            if (staffWechatSet.contains(wechatId)) {
                logger.debug("跳过职工用户: {}", wechatId);
                continue; // 过滤职工用户
            }

            Date lastTime = activity.getLastActivityTime();
            logger.debug("群聊活动 wechatId: {}, lastTime: {}", wechatId, lastTime);
            
            if (lastTime != null && (!lastActivityMap.containsKey(wechatId) || lastTime.after(lastActivityMap.get(wechatId)))) {
                lastActivityMap.put(wechatId, lastTime);
            }
        }
        logger.info("处理完群聊活动数据，lastActivityMap大小: {}", lastActivityMap.size());

        // 5. 计算总服务时长
        long totalDurationMillis = 0L;
        int validCount = 0;

        logger.info("开始计算总服务时长，firstActivityMap大小: {}, lastActivityMap大小: {}", 
                   firstActivityMap.size(), lastActivityMap.size());
                   
        // 添加详细的调试信息
        logger.debug("firstActivityMap内容:");
        firstActivityMap.forEach((k, v) -> logger.debug("  {}: {}", k, v));
        
        logger.debug("lastActivityMap内容:");
        lastActivityMap.forEach((k, v) -> logger.debug("  {}: {}", k, v));

        for (String wechatId : firstActivityMap.keySet()) {
            Date firstTime = firstActivityMap.get(wechatId);
            Date lastTime = lastActivityMap.get(wechatId);
            
            logger.debug("处理用户 {}: firstTime={}, lastTime={}", wechatId, firstTime, lastTime);
            
            if (firstTime != null && lastTime != null && lastTime.after(firstTime)) {
                long duration = (lastTime.getTime() - firstTime.getTime());
                totalDurationMillis += duration;
                validCount++;
                logger.debug("用户 {} 有效，服务时长: {} 毫秒", wechatId, duration);
            } else {
                logger.debug("用户 {} 无效，firstTime: {}, lastTime: {}, lastTime.after(firstTime): {}", 
                           wechatId, firstTime, lastTime, 
                           (firstTime != null && lastTime != null) ? lastTime.after(firstTime) : "N/A");
            }
        }
        logger.info("计算完成，有效用户数: {}, 总时长毫秒数: {}", validCount, totalDurationMillis);

        // 6. 防止除零
        if (validCount == 0) {
            logger.info("无有效用户数据，平均服务时间返回 0.0");
            return 0.0;
        }

        // 7. 转换为天
        double averageServiceTime = totalDurationMillis / (validCount * 1000.0 * 60 * 60 * 24);
        logger.info("平均服务时间计算完成: {} 天", averageServiceTime);
        return averageServiceTime;
    }

    /**
     * 计算新增用户数的日环比增长率
     *
     * @param currentDate 当前查询日期
     * @return 包含当前值、前一天值和增长率的统计数据
     */
    public Map<String, Object> calculateDailyNewUsersWithGrowth(Date currentDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取当前日期的新增用户数
            List<String> currentNewUsers = findNewUserByDay(currentDate);
            int currentCount = currentNewUsers != null ? currentNewUsers.size() : 0;
            
            // 2. 计算前一天日期
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            Date previousDayDate = cal.getTime();
            
            // 3. 获取前一天新增用户数
            List<String> previousNewUsers = findNewUserByDay(previousDayDate);
            int previousCount = previousNewUsers != null ? previousNewUsers.size() : 0;
            
            // 4. 计算增长率
            BigDecimal growthRate = calculateGrowthRate(
                BigDecimal.valueOf(currentCount), 
                BigDecimal.valueOf(previousCount)
            );
            
            // 5. 构建返回结果
            result.put("currentValue", currentCount);
            result.put("previousDayValue", previousCount);
            result.put("growthRate", growthRate);
            result.put("currentDate", currentDate);
            result.put("previousDayDate", previousDayDate);
            
            System.out.println("新增用户日环比增长计算完成: 当前值=" + currentCount + 
                             ", 前一天=" + previousCount + ", 增长率=" + growthRate + "%");
            
        } catch (Exception e) {
            System.err.println("计算新增用户日环比增长失败: " + e.getMessage());
            throw e;
        }
        
        return result;
    }

    /**
     * 计算周新增用户数的周环比增长率
     *
     * @param currentDate 当前查询日期（周内任意一天）
     * @return 包含当前周值、上周值和增长率的统计数据
     */
    public Map<String, Object> calculateWeeklyNewUsersWithGrowth(Date currentDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取当前周的新增用户数
            List<String> currentWeekNewUsers = findNewUserByWeek(currentDate);
            int currentCount = currentWeekNewUsers != null ? currentWeekNewUsers.size() : 0;
            
            // 2. 计算上周日期（往前推一7天）
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            Date previousWeekDate = cal.getTime();
            
            // 3. 获取上周新增用户数
            List<String> previousWeekNewUsers = findNewUserByWeek(previousWeekDate);
            int previousCount = previousWeekNewUsers != null ? previousWeekNewUsers.size() : 0;
            
            // 4. 计算增长率
            BigDecimal growthRate = calculateGrowthRate(
                BigDecimal.valueOf(currentCount), 
                BigDecimal.valueOf(previousCount)
            );
            
            // 5. 构建返回结果
            result.put("currentValue", currentCount);
            result.put("previousWeekValue", previousCount);
            result.put("growthRate", growthRate);
            result.put("currentDate", currentDate);
            result.put("previousWeekDate", previousWeekDate);
            
            System.out.println("周新增用户环比增长计算完成: 当前周=" + currentCount + 
                             ", 上周=" + previousCount + ", 增长率=" + growthRate + "%");
            
        } catch (Exception e) {
            System.err.println("计算周新增用户环比增长失败: " + e.getMessage());
            throw e;
        }
        
        return result;
    }

    /**
     * 计算月新增用户数的月环比增长率
     *
     * @param currentDate 当前查询日期（月内任意一天）
     * @return 包含当前月值、上月值和增长率的统计数据
     */
    public Map<String, Object> calculateMonthlyNewUsersWithGrowth(Date currentDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取当前月的新增用户数
            List<String> currentMonthNewUsers = findNewUserByMonth(currentDate);
            int currentCount = currentMonthNewUsers != null ? currentMonthNewUsers.size() : 0;
            
            // 2. 计算上月日期（往前推一1个月）
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.MONTH, -1);
            Date previousMonthDate = cal.getTime();
            
            // 3. 获取上月新增用户数
            List<String> previousMonthNewUsers = findNewUserByMonth(previousMonthDate);
            int previousCount = previousMonthNewUsers != null ? previousMonthNewUsers.size() : 0;
            
            // 4. 计算增长率
            BigDecimal growthRate = calculateGrowthRate(
                BigDecimal.valueOf(currentCount), 
                BigDecimal.valueOf(previousCount)
            );
            
            // 5. 构建返回结果
            result.put("currentValue", currentCount);
            result.put("previousMonthValue", previousCount);
            result.put("growthRate", growthRate);
            result.put("currentDate", currentDate);
            result.put("previousMonthDate", previousMonthDate);
            
            System.out.println("月新增用户环比增长计算完成: 当前月=" + currentCount + 
                             ", 上月=" + previousCount + ", 增长率=" + growthRate + "%");
            
        } catch (Exception e) {
            System.err.println("计算月新增用户环比增长失败: " + e.getMessage());
            throw e;
        }
        
        return result;
    }

    /**
     * 计算活跃用户数的同比增长率
     *
     * @param currentDate 当前查询日期
     * @return 包含当前值、上年同期值和增长率的统计数据
     */
    public Map<String, Object> calculateActiveUsersWithGrowth(Date currentDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取当前日期的活跃用户数
            int currentCount = getActiveUserCount(currentDate);
            
            // 2. 计算上年同期日期
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.YEAR, -1);
            Date previousYearDate = cal.getTime();
            
            // 3. 获取上年同期活跃用户数
            int previousCount = getActiveUserCount(previousYearDate);
            
            // 4. 计算增长率
            BigDecimal growthRate = calculateGrowthRate(
                BigDecimal.valueOf(currentCount), 
                BigDecimal.valueOf(previousCount)
            );
            
            // 5. 构建返回结果
            result.put("currentValue", currentCount);
            result.put("previousYearValue", previousCount);
            result.put("growthRate", growthRate);
            result.put("currentDate", currentDate);
            result.put("previousYearDate", previousYearDate);
            
            System.out.println("活跃用户同比增长计算完成: 当前值=" + currentCount + 
                             ", 上年同期=" + previousCount + ", 增长率=" + growthRate + "%");
            
        } catch (Exception e) {
            System.err.println("计算活跃用户同比增长失败: " + e.getMessage());
            throw e;
        }
        
        return result;
    }

    /**
     * 计算留存率的同比增长率
     *
     * @param currentDate 当前查询日期
     * @param days 留存天数
     * @return 包含当前值、上年同期值和增长率的统计数据
     */
    public Map<String, Object> calculateRetentionRateWithGrowth(Date currentDate, int days) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取当前日期的留存率
            double currentRate = getRetentionRate(currentDate, days);
            
            // 2. 计算上年同期日期
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.YEAR, -1);
            Date previousYearDate = cal.getTime();
            
            // 3. 获取上年同期留存率
            double previousRate = getRetentionRate(previousYearDate, days);
            
            // 4. 计算增长率（注意：这里是百分点的变化，不是比率的变化）
            BigDecimal currentRateBD = BigDecimal.valueOf(currentRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal previousRateBD = BigDecimal.valueOf(previousRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal growthRate = calculateGrowthRate(currentRateBD, previousRateBD);
            
            // 5. 构建返回结果
            result.put("currentValue", currentRateBD);
            result.put("previousYearValue", previousRateBD);
            result.put("growthRate", growthRate);
            result.put("days", days);
            result.put("currentDate", currentDate);
            result.put("previousYearDate", previousYearDate);
            
            System.out.println(days + "日留存率同比增长计算完成: 当前值=" + currentRateBD + "%" +
                             ", 上年同期=" + previousRateBD + "%, 增长率=" + growthRate + "%");
            
        } catch (Exception e) {
            System.err.println("计算" + days + "日留存率同比增长失败: " + e.getMessage());
            throw e;
        }
        
        return result;
    }

    /**
     * 计算流失率的同比增长率
     *
     * @param currentDate 当前查询日期
     * @param days 流失观察天数
     * @return 包含当前值、上年同期值和增长率的统计数据
     */
    public Map<String, Object> calculateChurnRateWithGrowth(Date currentDate, int days) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取当前日期的流失率
            double currentRate = getChurnRate(currentDate, days);
            
            // 2. 计算上年同期日期
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.YEAR, -1);
            Date previousYearDate = cal.getTime();
            
            // 3. 获取上年同期流失率
            double previousRate = getChurnRate(previousYearDate, days);
            
            // 4. 计算增长率
            BigDecimal currentRateBD = BigDecimal.valueOf(currentRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal previousRateBD = BigDecimal.valueOf(previousRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal growthRate = calculateGrowthRate(currentRateBD, previousRateBD);
            
            // 5. 构建返回结果
            result.put("currentValue", currentRateBD);
            result.put("previousYearValue", previousRateBD);
            result.put("growthRate", growthRate);
            result.put("days", days);
            result.put("currentDate", currentDate);
            result.put("previousYearDate", previousYearDate);
            
            System.out.println(days + "日流失率同比增长计算完成: 当前值=" + currentRateBD + "%" +
                             ", 上年同期=" + previousRateBD + "%, 增长率=" + growthRate + "%");
            
        } catch (Exception e) {
            System.err.println("计算" + days + "日流失率同比增长失败: " + e.getMessage());
            throw e;
        }
        
        return result;
    }

    /**
     * 计算平均服务时间的同比增长率
     *
     * @param currentDate 当前查询日期
     * @return 包含当前值、上年同期值和增长率的统计数据
     */
    public Map<String, Object> calculateAverageServiceTimeWithGrowth(Date currentDate) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取当前日期的平均服务时间
            double currentTime = getAverageServiceTime(currentDate);
            
            // 2. 计算上年同期日期
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentDate);
            cal.add(Calendar.YEAR, -1);
            Date previousYearDate = cal.getTime();
            
            // 3. 获取上年同期平均服务时间
            double previousTime = getAverageServiceTime(previousYearDate);
            
            // 4. 计算增长率
            BigDecimal currentTimeBD = BigDecimal.valueOf(currentTime).setScale(2, RoundingMode.HALF_UP);
            BigDecimal previousTimeBD = BigDecimal.valueOf(previousTime).setScale(2, RoundingMode.HALF_UP);
            BigDecimal growthRate = calculateGrowthRate(currentTimeBD, previousTimeBD);
            
            // 5. 构建返回结果
            result.put("currentValue", currentTimeBD);
            result.put("previousYearValue", previousTimeBD);
            result.put("growthRate", growthRate);
            result.put("currentDate", currentDate);
            result.put("previousYearDate", previousYearDate);
            
            System.out.println("平均服务时间同比增长计算完成: 当前值=" + currentTimeBD + "天" +
                             ", 上年同期=" + previousTimeBD + "天, 增长率=" + growthRate + "%");
            
        } catch (Exception e) {
            System.err.println("计算平均服务时间同比增长失败: " + e.getMessage());
            throw e;
        }
        
        return result;
    }

    /**
     * 计算增长率的通用方法
     * 公式: (当前值 - 上期值) / 上期值 × 100%
     *
     * @param currentValue 当前值
     * @param previousValue 上期值
     * @return 增长率（百分比），保留2位小数
     */
    private BigDecimal calculateGrowthRate(BigDecimal currentValue, BigDecimal previousValue) {
        if (previousValue.compareTo(BigDecimal.ZERO) == 0) {
            // 如果上期值为0，无法计算增长率
            if (currentValue.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO; // 0 → 0，增长率为0%
            } else {
                return BigDecimal.valueOf(100.0); // 0 → 正数，增长率为100%
            }
        }
        
        // 正常计算: (current - previous) / previous * 100
        return currentValue.subtract(previousValue)
                .divide(previousValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
