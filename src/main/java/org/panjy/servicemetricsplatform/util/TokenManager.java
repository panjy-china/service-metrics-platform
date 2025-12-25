package org.panjy.servicemetricsplatform.util;

import com.google.gson.*;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Token管理器
 * 负责处理token刷新和401错误处理
 */
public class TokenManager {

    private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);
    private static final String LOGIN_URL = "https://002.siyuguanli.com:9991/token";
    private static final String USERNAME = "admin01";
    private static final String PASSWORD = "admin01";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static String currentToken = null;
    private static LocalDateTime tokenExpireTime = null;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();

    /**
     * 获取当前有效的token
     * 如果token过期或不存在，会自动刷新
     */
    public static String getValidToken() {
        // 检查token是否存在且未过期
        if (currentToken == null || isTokenExpired()) {
            refreshToken();
        }
        return currentToken;
    }

    /**
     * 检查token是否过期
     */
    private static boolean isTokenExpired() {
        if (tokenExpireTime == null) {
            return true;
        }
        // 提前5分钟刷新token，避免边界情况
        return LocalDateTime.now().isAfter(tokenExpireTime.minusMinutes(5));
    }

    /**
     * 刷新token
     */
    private static void refreshToken() {
        logger.info("开始刷新token...");

        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(30000)
                        .setSocketTimeout(60000)
                        .build())
                .build()) {

            HttpPost httpPost = new HttpPost(LOGIN_URL);

            // 设置请求头
            httpPost.setHeader("accept", "application/json, text/plain, */*");
            httpPost.setHeader("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
            httpPost.setHeader("client", "system");
            httpPost.setHeader("content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("origin", "https://002.siyuguanli.com");
            httpPost.setHeader("priority", "u=1, i");
            httpPost.setHeader("referer", "https://002.siyuguanli.com/");
            httpPost.setHeader("sec-ch-ua",
                    "\"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"138\", \"Microsoft Edge\";v=\"138\"");
            httpPost.setHeader("sec-ch-ua-mobile", "?0");
            httpPost.setHeader("sec-ch-ua-platform", "\"Windows\"");
            httpPost.setHeader("sec-fetch-dest", "empty");
            httpPost.setHeader("sec-fetch-mode", "cors");
            httpPost.setHeader("sec-fetch-site", "same-site");
            httpPost.setHeader("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36 Edg/138.0.0.0");
            httpPost.setHeader("verifycode", "");
            httpPost.setHeader("verifysessionid", "null");

            // 设置请求体
            String requestBody = "grant_type=password&username=" + USERNAME + "&password=" + PASSWORD;
            httpPost.setEntity(new StringEntity(requestBody, "UTF-8"));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity entity = response.getEntity();
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(entity, "UTF-8");

                if (statusCode == 200) {
                    // 解析响应
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);

                    if (jsonResponse.has("access_token")) {
                        currentToken = "bearer " + jsonResponse.get("access_token").getAsString();

                        // 计算token过期时间
                        if (jsonResponse.has("expires_in")) {
                            int expiresIn = jsonResponse.get("expires_in").getAsInt();
                            tokenExpireTime = LocalDateTime.now().plusSeconds(expiresIn);
                        }

                        logger.info("Token刷新成功，过期时间: " +
                                (tokenExpireTime != null ? tokenExpireTime.format(DATE_FORMATTER) : "未知"));

                        return;
                    }
                }

                // 记录失败日志
                logger.error("Token刷新失败，状态码: " + statusCode + ", 响应: " + responseBody);

            } catch (Exception e) {
//                FeishuLogUtil.sendToRobot("Token刷新异常: " + e.getMessage(), FeishuLogUtil.feishuRobot);
                logger.error("Token刷新异常: " + e.getMessage());
            }

        } catch (Exception e) {
//            FeishuLogUtil.sendToRobot("创建HTTP客户端失败: " + e.getMessage(), FeishuLogUtil.feishuRobot);
            logger.error("创建HTTP客户端失败: " + e.getMessage());
        }

        // 如果刷新失败，保持原有token（如果有的话）
        if (currentToken == null) {
            logger.error("无法获取有效token，请检查登录凭据");
        }
    }

    /**
     * 处理401错误，自动刷新token
     */
    public static boolean handle401Error() {
        logger.info("检测到401错误，尝试刷新token...");
        refreshToken();
        return currentToken != null;
    }

    /**
     * 获取当前token（不刷新）
     */
    public static String getCurrentToken() {
        return currentToken;
    }

    /**
     * 强制刷新token
     */
    public static void forceRefreshToken() {
        currentToken = null;
        tokenExpireTime = null;
        refreshToken();
    }

    /**
     * LocalDateTime类型适配器
     */
    private static class LocalDateTimeTypeAdapter
            implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            if (src == null) {
                return JsonNull.INSTANCE;
            }
            return new JsonPrimitive(src.format(DATE_TIME_FORMATTER));
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            if (json == null || json.isJsonNull()) {
                return null;
            }

            String dateTimeStr = json.getAsString();
            if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
                return null;
            }

            try {
                return LocalDateTime.parse(dateTimeStr.trim(), DATE_TIME_FORMATTER);
            } catch (Exception e) {
//                FeishuLogUtil.sendToRobot("无法解析LocalDateTime: " + dateTimeStr + ", 错误: " + e.getMessage(),
//                        FeishuLogUtil.feishuRobot);
                throw new JsonParseException("无法解析LocalDateTime: " + dateTimeStr, e);
            }
        }
    }
}