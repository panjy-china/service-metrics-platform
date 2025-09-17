package org.panjy.servicemetricsplatform.mapper.mysql;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WechatMemberMapper {
    List<String> findAllUser();
}
