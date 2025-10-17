package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * FriendFirstChat数据访问接口
 * 提供对aikang.friend_first_chat表的查询操作
 */
@Mapper
public interface FriendFirstChatMapper {
    
    /**
     * 查询指定时间范围内的用户微信ID列表
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 用户微信ID列表
     */
    List<String> selectFriendWechatIdsByTimeRange(
        @Param("startTime") String startTime, 
        @Param("endTime") String endTime
    );
}