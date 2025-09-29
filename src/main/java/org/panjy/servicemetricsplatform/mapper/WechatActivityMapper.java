package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.WechatActivity;

import java.util.Date;
import java.util.List;

@Mapper
public interface WechatActivityMapper {
    List<WechatActivity> getWechatActivities(@Param("checkTime")Date checkTime);

    List<WechatActivity> getWechatGroupActivities(@Param("checkTime")Date checkTime);
}
