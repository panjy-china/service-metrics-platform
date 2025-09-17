package org.panjy.servicemetricsplatform.mapper.mysql;

import org.apache.ibatis.annotations.Mapper;
import org.panjy.servicemetricsplatform.entity.WechatAccount;

import java.util.List;

/**
 * 微信账号数据访问接口
 */
@Mapper
public interface WechatAccountMapper {
    /**
     * 查询所有微信账号
     * @return 微信账号列表
     */
    List<WechatAccount> selectAll();

    /**
     * 查询所有微信账号的微信ID
     * @return 微信账号微信ID列表
     */
    List<String> selectAllWechatIds();
}