package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.WechatActivity;
import org.panjy.servicemetricsplatform.mapper.mysql.WechatAccountMapper;
import org.panjy.servicemetricsplatform.mapper.mysql.WechatActivityMapper;
import org.panjy.servicemetricsplatform.mapper.mysql.WechatGroupMessageMapper;
import org.panjy.servicemetricsplatform.mapper.mysql.WechatMemberMapper;
import org.panjy.servicemetricsplatform.mapper.mysql.WechatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StrategicLayerService {
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
        // 计算一天的结束时间（加 1 天减 1 毫秒）
        Calendar cal = Calendar.getInstance();
        cal.setTime(begin);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        Date end = cal.getTime();

        // 调用 Mapper 方法
        return wechatMessageMapper.findMessageByContent("你已添加了", begin, end);
    }

    /**
     * 查询一周内的新增用户
     *
     * @param week 某一周的任意一天（例如 2025-08-28 10:30:00）
     * @return 新增用户的微信 ID 列表
     */
    public List<String> findNewUserByWeek(Date week) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(week);

        // 设置为本周第一天（周一）00:00:00
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date begin = cal.getTime();

        // 设置为下周第一天减 1 毫秒（即本周最后一毫秒）
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        cal.add(Calendar.MILLISECOND, -1);
        Date end = cal.getTime();

        // 调用 Mapper 方法
        return wechatMessageMapper.findMessageByContent("你已添加了", begin, end);
    }

    /**
     * 查询一个月内的新增用户
     *
     * @param month 某个月份的第一天（例如 2025-08-01 00:00:00）
     * @return 新增用户的微信 ID 列表
     */
    public List<String> findNewUserByMonth(Date month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(month);

        // 设置为当月第一天 00:00:00
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date begin = cal.getTime();

        // 设置为下个月第一天减 1 毫秒（即本月最后一毫秒）
        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        Date end = cal.getTime();

        // 调用 Mapper 方法
        return wechatMessageMapper.findMessageByContent("你已添加了", begin, end);
    }


    /**
     * 查询活跃用户数
     *
     * @param currentTime 当前时间
     */
    public int getActiveUserCount(Date currentTime) {
        // 获取私聊活跃用户
        List<String> activeUsers = wechatMessageMapper.findActiveUsers(currentTime);
        // 获取群聊活跃用户
        List<String> groupActivateUser = wechatGroupMessageMapper.findActiveUsers(currentTime);
        // 获取所有职工微信ID
        List<String> staffWechatIds = wechatAccountMapper.selectAllWechatIds();
        
        // 转换为Set以提高查找效率
        Set<String> staffWechatSet = new HashSet<>(staffWechatIds);

        // 合并并去重 activeUsers 和 groupActivateUser
        Set<String> allActiveUsers = new HashSet<>();
        allActiveUsers.addAll(activeUsers);
        allActiveUsers.addAll(groupActivateUser);

        // 去除合并后结果中出现的职工
        allActiveUsers.removeAll(staffWechatSet);

        // 返回活跃用户数
        return allActiveUsers.size();
    }

    /**
     * 计算用户留存率
     *
     * @param currentTime  指定的日期（通常是新增用户的日期）
     * @param days         留存天数，比如 1 表示次日留存，3 表示3日留存 [3, 7, 10]
     * @return             留存率（百分比）
     */
    public double getRetentionRate(Date currentTime, int days) {
        // 1. 获取指定日期新增的用户
        List<String> newUsers = findNewUserByDay(currentTime);
        if (newUsers == null || newUsers.isEmpty()) {
            // 如果当天没有新增用户，直接返回0，避免除以0
            return 0.0;
        }

        // 2. 查询 days 天后仍然活跃的用户（私聊消息活跃）
        List<String> userServived = wechatMessageMapper.findUserServived(currentTime, days);

        // 3. 查询 days 天后仍然活跃的用户（群聊消息活跃）
        List<String> groupUserServived = wechatGroupMessageMapper.findUserServived(currentTime, days);

        // 4. 获取所有职工微信ID
        List<String> staffWechatIds = wechatAccountMapper.selectAllWechatIds();
        Set<String> staffWechatSet = new HashSet<>(staffWechatIds);

        // 5. 合并群聊和私聊的活跃用户，并去除职工
        Set<String> activeUsers = new HashSet<>();
        activeUsers.addAll(userServived);
        activeUsers.addAll(groupUserServived);
        activeUsers.removeAll(staffWechatSet); // 去除职工用户

        // 6. 从新增用户中去除职工，得到真实的客户新增用户
        Set<String> realNewUsers = new HashSet<>(newUsers);
        realNewUsers.removeAll(staffWechatSet);
        
        if (realNewUsers.isEmpty()) {
            // 如果去除职工后没有新增客户，返回0
            return 0.0;
        }

        // 7. 计算留存用户（只统计新增客户里仍然活跃的）
        Set<String> retainedUsers = new HashSet<>(realNewUsers);
        retainedUsers.retainAll(activeUsers); // 保证 retainedUsers ⊆ realNewUsers

        // 8. 留存率 = 留存用户数 ÷ 新增客户数 × 100%
        return retainedUsers.size() * 100.0 / realNewUsers.size();
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
        // 1. 获取当天新增用户
        List<String> newUsers = findNewUserByDay(currentTime);
        if (newUsers == null || newUsers.isEmpty()) {
            return 0.0;
        }

        // 2. 获取流失用户（私聊 + 群聊）
        List<String> inactiveUsers = wechatMessageMapper.findInactiveUsers(currentTime);
        List<String> groupInactiveUsers = wechatGroupMessageMapper.findInactiveUsers(currentTime);

        // 3. 获取所有职工微信ID
        List<String> staffWechatIds = wechatAccountMapper.selectAllWechatIds();
        Set<String> staffWechatSet = new HashSet<>(staffWechatIds);

        // 4. 合并两类流失用户，并去除职工
        Set<String> churnUsers = new HashSet<>();
        churnUsers.addAll(inactiveUsers);
        churnUsers.addAll(groupInactiveUsers);
        churnUsers.removeAll(staffWechatSet); // 去除职工用户

        // 5. 从新增用户中去除职工，得到真实的客户新增用户
        Set<String> realNewUsers = new HashSet<>(newUsers);
        realNewUsers.removeAll(staffWechatSet);
        
        if (realNewUsers.isEmpty()) {
            return 0.0;
        }

        // 6. 仅保留属于当天新增客户的流失用户
        churnUsers.retainAll(realNewUsers);

        // 7. 计算流失率
        return (churnUsers.size() * 100.0) / realNewUsers.size();
    }

    /**
     * 计算某日开始的平均服务时间（单位：小时）
     *
     * @param checkTime 指定的日期
     * @return 平均服务时长（小时）
     */
    public double getAverageServiceTime(Date checkTime) {
        // 1. 查询私聊活动
        List<WechatActivity> privateActivities = wechatActivityMapper.getWechatActivities(checkTime);
        if (privateActivities == null) privateActivities = Collections.emptyList();

        // 2. 查询群聊活动
        List<WechatActivity> groupActivities = wechatActivityMapper.getWechatGroupActivities(checkTime);
        if (groupActivities == null) groupActivities = Collections.emptyList();

        System.out.println(privateActivities.size() + " " + groupActivities.size());

        // 3. 获取所有职工用户列表
        List<String> staffWechatIds = wechatAccountMapper.selectAllWechatIds();
        Set<String> staffWechatSet = new HashSet<>(staffWechatIds);

        // 4. 合并 first/last 活动时间
        Map<String, Date> firstActivityMap = new HashMap<>();
        Map<String, Date> lastActivityMap = new HashMap<>();

        // 私聊活动：first + last
        for (WechatActivity activity : privateActivities) {
            System.out.println(activity);
            if (activity == null) continue;
            String wechatId = activity.getWechatId();
//            if (!allUserSet.contains(wechatId)) continue; // 过滤无效用户

            Date firstTime = activity.getFirstActivityTime();
            Date lastTime = activity.getLastActivityTime();

            if (firstTime != null && (!firstActivityMap.containsKey(wechatId) || firstTime.before(firstActivityMap.get(wechatId)))) {
                firstActivityMap.put(wechatId, firstTime);
            }
            if (lastTime != null && (!lastActivityMap.containsKey(wechatId) || lastTime.after(lastActivityMap.get(wechatId)))) {
                lastActivityMap.put(wechatId, lastTime);
            }
        }

        // 群聊活动：只算 last（first 不统计，避免干扰）
        for (WechatActivity activity : groupActivities) {
            if (activity == null) continue;
            String wechatId = activity.getWechatId();
            if (staffWechatSet.contains(wechatId)) continue; // 过滤职工用户

            Date lastTime = activity.getLastActivityTime();
            if (lastTime != null && (!lastActivityMap.containsKey(wechatId) || lastTime.after(lastActivityMap.get(wechatId)))) {
                lastActivityMap.put(wechatId, lastTime);
            }
        }

        // 5. 计算总服务时长
        long totalDurationMillis = 0L;
        int validCount = 0;

        for (String wechatId : firstActivityMap.keySet()) {
            Date firstTime = firstActivityMap.get(wechatId);
            Date lastTime = lastActivityMap.get(wechatId);
//            System.out.println(firstTime + " " + lastTime);
            if (firstTime != null && lastTime != null && lastTime.after(firstTime)) {
                totalDurationMillis += (lastTime.getTime() - firstTime.getTime());
                validCount++;
            }
        }

        // 6. 防止除零
        if (validCount == 0) return 0.0;

        // 7. 转换为小时
        return totalDurationMillis / (validCount * 1000.0 * 60 * 60);
    }

}
