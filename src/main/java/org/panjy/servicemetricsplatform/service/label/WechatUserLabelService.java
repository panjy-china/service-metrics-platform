package org.panjy.servicemetricsplatform.service.label;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.panjy.servicemetricsplatform.entity.label.WechatUserLabel;
import org.panjy.servicemetricsplatform.mapper.label.WechatUserLabelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * 微信用户健康画像标签服务类
 */
@Service
public class WechatUserLabelService {
    
    @Autowired
    private WechatUserLabelMapper wechatUserLabelMapper;
    
    /**
     * 根据微信好友ID和销售账号ID查询标签
     * @param wechatFriendId 微信好友ID
     * @param wechatAccountId 微信销售账号ID
     * @return 标签信息，如果不存在则返回空对象
     */
    public WechatUserLabel getByWechatFriendIdAndAccountId(String wechatFriendId, String wechatAccountId) {
        // 参数校验
        if (wechatFriendId == null || wechatFriendId.trim().isEmpty() || 
            wechatAccountId == null || wechatAccountId.trim().isEmpty()) {
            return new WechatUserLabel();
        }
        
        // 查询标签信息
        WechatUserLabel wechatUserLabel = wechatUserLabelMapper.selectByWechatFriendIdAndAccountId(wechatFriendId, wechatAccountId);
        
        // 如果查询结果为空，返回一个空对象而不是null
        if (wechatUserLabel == null) {
            return new WechatUserLabel();
        }
        
        return wechatUserLabel;
    }
    
    /**
     * 根据微信好友ID和销售账号ID查询标签并解析为Map格式
     * @param wechatFriendId 微信好友ID
     * @param wechatAccountId 微信销售账号ID
     * @return 解析后的标签Map，如果解析失败或不存在则返回空Map
     */
    public Map<String, Object> getLabelAsMap(String wechatFriendId, String wechatAccountId) {
        // 参数校验
        if (wechatFriendId == null || wechatFriendId.trim().isEmpty() || 
            wechatAccountId == null || wechatAccountId.trim().isEmpty()) {
            return new HashMap<>();
        }
        
        try {
            // 查询标签信息
            WechatUserLabel wechatUserLabel = wechatUserLabelMapper.selectByWechatFriendIdAndAccountId(wechatFriendId, wechatAccountId);
            
            // 如果查询结果为空，返回一个空Map
            if (wechatUserLabel == null) {
                return new HashMap<>();
            }
            
            // 获取标签字符串
            String label = wechatUserLabel.getLabel();
            
            // 如果标签为空，返回空Map
            if (label == null || label.trim().isEmpty()) {
                return new HashMap<>();
            }
            
            // 处理转义字符
            // 先移除外层的引号（如果有的话）
            if (label.startsWith("\"") && label.endsWith("\"") && label.length() >= 2) {
                label = label.substring(1, label.length() - 1);
            }
            
            // 处理转义的双引号
            label = label.replace("\\\"", "\"");
            
            // 使用ObjectMapper解析JSON字符串
            ObjectMapper objectMapper = new ObjectMapper();
            // 根据错误信息，label字段可能是JSON数组字符串，需要特殊处理
            if (label.startsWith("[")) {
                // 如果是数组，解析为List然后包装在Map中
                List<Map<String, Object>> list = objectMapper.readValue(label, List.class);
                Map<String, Object> result = new HashMap<>();
                result.put("labels", list);
                return result;
            } else {
                // 如果是对象，直接解析为Map
                Map<String, Object> map = objectMapper.readValue(label, Map.class);
                return map;
            }
        } catch (JsonProcessingException e) {
            // JSON解析失败，返回空Map
            e.printStackTrace();
            return new HashMap<>();
        } catch (Exception e) {
            // 其他异常，返回空Map
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}