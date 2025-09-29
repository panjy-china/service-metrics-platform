package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface WechatGroupMessageMapper {
    List<String> findActiveUsers(@Param("checkTime") Date checkTime);

    List<String> findUserServived(@Param("checkTime")Date checkTime, @Param("days") int days);

    List<String> findInactiveUsers(@Param("startTime")Date startTime);
}
