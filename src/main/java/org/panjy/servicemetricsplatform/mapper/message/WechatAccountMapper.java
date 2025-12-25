package org.panjy.servicemetricsplatform.mapper.message;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.message.WechatAccount;

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
    
    /**
     * 根据微信ID查询微信账号
     * @param wechatId 微信ID
     * @return 微信账号
     */
    WechatAccount selectByWechatId(@Param("wechatId") String wechatId);
    
    /**
     * 根据微信ID更新微信账号
     * @param account 微信账号
     * @return 受影响的行数
     */
    int updateByWechatId(WechatAccount account);
}