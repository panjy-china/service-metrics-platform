package org.panjy.servicemetricsplatform.mapper.label;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.label.WechatUserLabel;

import java.util.List;

/**
 * 微信用户健康画像标签数据访问接口
 */
@Mapper
public interface WechatUserLabelMapper {

    /**
     * 插入微信用户健康画像标签
     * @param wechatUserLabel 微信用户健康画像标签对象
     * @return 影响的行数
     */
    int insert(WechatUserLabel wechatUserLabel);
    
    /**
     * 批量插入微信用户健康画像标签
     * @param wechatUserLabels 微信用户健康画像标签对象列表
     * @return 影响的行数
     */
    int batchInsert(@Param("wechatUserLabels") List<WechatUserLabel> wechatUserLabels);
    
    /**
     * 根据微信好友ID、销售账号ID和标签查询标签信息
     * @param wechatFriendId 微信好友ID
     * @param wechatAccountId 微信销售账号ID
     * @param label 标签
     * @return 微信用户健康画像标签对象
     */
    WechatUserLabel selectByWechatFriendIdAndAccountIdAndLabel(@Param("wechatFriendId") String wechatFriendId,
                                                               @Param("wechatAccountId") String wechatAccountId,
                                                               @Param("label") String label);

    /**
     * 根据微信好友ID和销售账号ID查询标签
     * @param wechatFriendId 微信好友ID
     * @param wechatAccountId 微信销售账号ID
     * @return 标签对象
     */
    WechatUserLabel selectByWechatFriendIdAndAccountId(@Param("wechatFriendId") String wechatFriendId,
                                                       @Param("wechatAccountId") String wechatAccountId);
    
    /**
     * 根据微信好友ID和销售账号ID查询所有标签
     * @param wechatFriendId 微信好友ID
     * @param wechatAccountId 微信销售账号ID
     * @return 微信用户健康画像标签对象列表
     */
    List<WechatUserLabel> selectListByWechatFriendIdAndAccountId(@Param("wechatFriendId") String wechatFriendId,
                                                                @Param("wechatAccountId") String wechatAccountId);
                                                                
    /**
     * 根据微信ID查询标签
     * @param wechatId 微信ID
     * @return 标签对象
     */
    WechatUserLabel selectByWechatId(@Param("wechatId") String wechatId);
}