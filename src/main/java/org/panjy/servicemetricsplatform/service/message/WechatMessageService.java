package org.panjy.servicemetricsplatform.service.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.panjy.servicemetricsplatform.entity.message.Conversation;
import org.panjy.servicemetricsplatform.entity.message.Message;
import org.panjy.servicemetricsplatform.mapper.message.WechatMessageMapper;
import org.panjy.servicemetricsplatform.util.TokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class WechatMessageService {
    
    @Autowired
    private WechatMessageMapper wechatMessageMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据日期查询包含地址信息的用户对话记录列表
     *
     * @param startDate 开始日期
     * @return 包含地址信息的用户对话记录列表
     */
    public List<Conversation> findConversationsWithAddressInfoByDate(Date startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("开始日期不能为空");
        }

        try {
            // 1. 调用findWechatUserIdsWithAddressInfo获取包含地址信息的用户ID列表
            List<String> userIds = wechatMessageMapper.findWechatUserIdsWithAddressInfo(startDate);

            // 2. 创建用于存储对话记录的列表
            List<Conversation> conversations = new ArrayList<>();

            // 3. 遍历用户ID列表，逐个调用findConversationByWechatId获取对话记录
            for (String userId : userIds) {
                if (userId != null && !userId.isEmpty()) {
                    Conversation conversation = wechatMessageMapper.findConversationByWechatId(userId);
                    if (conversation != null) {
                        conversations.add(conversation);
                    }
                }
            }

            return conversations;
        } catch (Exception e) {
            System.err.println("根据日期查询包含地址信息的用户对话记录时发生错误: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); // 发生错误时返回空列表而不是null
        }
    }

    /**
     * 根据wechatAccountId，onlyFriendId查询用户wechatId
     *
     * @param wechatAccountId
     * @param onlyFriendId
     * @return wechatId
     */
    public String getWechatIdByWechatAccountIdAndOnlyFriendId(String wechatAccountId, String onlyFriendId) throws Exception {
        return executeWithRetry(wechatAccountId, onlyFriendId, 0);
    }

    private String executeWithRetry(String wechatAccountId, String onlyFriendId, int retryCount) throws Exception {
        final int MAX_RETRY = 1; // 最多重试1次（仅用于401）

        URIBuilder uriBuilder = new URIBuilder("https://002.siyuguanli.com:9991/api/WechatFriend/oneWechatFriends");
        uriBuilder.setParameter("keyword", "")
                .setParameter("pageIndex", "0")
                .setParameter("pageSize", "20")
                .setParameter("wechatAccountId", wechatAccountId)
                .setParameter("onlyFriendId", onlyFriendId);

        URI uri = uriBuilder.build();
        HttpGet httpGet = new HttpGet(uri);

        String token = TokenManager.getValidToken();
        httpGet.setHeader("Authorization", "Bearer " + token);
        httpGet.setHeader("Content-Type", "application/json");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpResponse response = httpClient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 401) {
                if (retryCount < MAX_RETRY && TokenManager.handle401Error()) {
                    return executeWithRetry(wechatAccountId, onlyFriendId, retryCount + 1);
                } else {
                    throw new RuntimeException("Token refresh failed or max retry exceeded after 401.");
                }
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new RuntimeException("Empty response from server");
            }

            String responseBody = EntityUtils.toString(entity, "UTF-8");

            // 解析 JSON
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode results = root.path("results");

            if (results.isArray() && !results.isEmpty()) {
                JsonNode firstResult = results.get(0);
                String wechatId = firstResult.path("wechatId").asText();
                if (wechatId != null && !wechatId.trim().isEmpty()) {
                    return wechatId;
                } else {
                    throw new RuntimeException("wechatId is missing in the response");
                }
            } else {
                throw new RuntimeException("No matching friend found for the given parameters");
            }

        }
    }
    /**
     * 根据微信ID查询用户最早的一条消息记录
     * 
     * @param wechatId 用户微信ID
     * @return 最早的消息记录，如果未找到则返回null
     */
    public Message findEarliestMessageByWechatId(String wechatId) {
        if (wechatId == null || wechatId.isEmpty()) {
            throw new IllegalArgumentException("微信ID不能为空");
        }
        
        try {
            return wechatMessageMapper.findEarliestMessageByWechatId(wechatId);
        } catch (Exception e) {
            System.err.println("根据微信ID查询用户最早消息记录时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 根据微信ID查询用户最晚的一条消息记录
     * 
     * @param wechatId 用户微信ID
     * @return 最晚的消息记录，如果未找到则返回null
     */
    public Message findLatestMessageByWechatId(String wechatId) {
        if (wechatId == null || wechatId.isEmpty()) {
            throw new IllegalArgumentException("微信ID不能为空");
        }
        
        try {
            return wechatMessageMapper.findLatestMessageByWechatId(wechatId);
        } catch (Exception e) {
            System.err.println("根据微信ID查询用户最晚消息记录时发生错误: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}