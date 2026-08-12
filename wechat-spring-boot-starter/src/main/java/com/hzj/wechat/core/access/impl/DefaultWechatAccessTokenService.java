package com.hzj.wechat.core.access.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hzj.wechat.core.access.WechatAccessTokenException;
import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.access.domain.WechatAccessTokenRequest;
import com.hzj.wechat.core.access.domain.WechatAccessTokenResponse;
import com.hzj.wechat.core.enums.WechatHttpMethod;
import com.hzj.wechat.provider.wechat.access.WechatAccessConfigProvider;
import com.hzj.wechat.provider.wechat.access.entity.WechatAccessConfig;
import com.hzj.wechat.utils.WechatPayUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * 微信稳定版接口调用凭据默认实现。
 */
@Slf4j
public class DefaultWechatAccessTokenService implements WechatAccessTokenService {

    private final WechatAccessConfigProvider provider;

    private final OkHttpClient client;

    /**
     * 使用默认 OkHttp 客户端创建服务。
     *
     * @param provider 微信接口调用凭据配置提供者
     */
    public DefaultWechatAccessTokenService(WechatAccessConfigProvider provider) {
        this(provider, new OkHttpClient.Builder().build());
    }

    /**
     * 创建微信稳定版接口调用凭据服务。
     *
     * @param provider 微信接口调用凭据配置提供者
     * @param client HTTP 客户端
     */
    public DefaultWechatAccessTokenService(WechatAccessConfigProvider provider, OkHttpClient client) {
        if (provider == null) {
            throw new IllegalArgumentException("WechatAccessConfigProvider 不能为空");
        }
        if (client == null) {
            throw new IllegalArgumentException("OkHttpClient 不能为空");
        }
        this.provider = provider;
        this.client = client;
    }

    @Override
    public WechatAccessTokenResponse getStableAccessToken(boolean forceRefresh) {
        return getStableAccessToken(WechatAccessTokenRequest.builder().forceRefresh(forceRefresh).build());
    }

    @Override
    public WechatAccessTokenResponse getStableAccessToken(WechatAccessTokenRequest request) {
        requireNonNull(request, "WechatAccessTokenRequest");
        WechatAccessConfig config = getConfig();
        requireNotBlank(config.getAppid(), "appid");
        requireNotBlank(config.getSecret(), "secret");
        requireNotBlank(request.getGrantType(), "grantType");
        requireNotBlank(request.getRequestUrl(), "requestUrl");
        requireNonNull(request.getRequestMethod(), "requestMethod");

        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("grant_type", request.getGrantType());
        requestJson.addProperty("appid", config.getAppid());
        requestJson.addProperty("secret", config.getSecret());
        requestJson.addProperty("force_refresh", request.isForceRefresh());
        String requestBody = WechatPayUtils.toJson(requestJson);
        Request httpRequest = buildHttpRequest(request.getRequestUrl(), request.getRequestMethod(), requestBody)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();

        log.info("DefaultWechatAccessTokenService.getStableAccessToken 开始获取微信稳定版接口调用凭据，appid={}, forceRefresh={}",
                maskValue(config.getAppid()), request.isForceRefresh());
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                log.error("DefaultWechatAccessTokenService.getStableAccessToken 请求微信接口失败，code={}, responseBody={}",
                        response.code(), abbreviate(responseBody));
                throw new WechatAccessTokenException(
                        "请求微信稳定版接口调用凭据失败，HTTP 状态码=" + response.code(), null, response.code());
            }
            return parseResponse(responseBody, response.code());
        } catch (IOException e) {
            log.error("DefaultWechatAccessTokenService.getStableAccessToken 调用微信接口异常，appid={}",
                    maskValue(config.getAppid()), e);
            throw new UncheckedIOException("调用微信稳定版接口调用凭据接口异常", e);
        }
    }

    private Request.Builder buildHttpRequest(String url, WechatHttpMethod requestMethod, String requestBody) {
        Request.Builder builder = new Request.Builder().url(url);
        return switch (requestMethod) {
            case GET -> builder.get();
            case DELETE -> builder.delete();
            case POST, PUT, PATCH -> builder.method(requestMethod.name(),
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody));
        };
    }

    private WechatAccessTokenResponse parseResponse(String responseBody, int httpStatus) {
        try {
            JsonObject jsonObject = WechatPayUtils.fromJson(responseBody, JsonObject.class);
            if (jsonObject == null) {
                throw new WechatAccessTokenException("微信稳定版接口调用凭据响应为空", null, httpStatus);
            }
            Integer errcode = getInteger(jsonObject, "errcode");
            String errmsg = getString(jsonObject, "errmsg");
            if (errcode != null && errcode != 0) {
                log.error("DefaultWechatAccessTokenService.getStableAccessToken 微信接口业务返回失败，errcode={}, errmsg={}",
                        errcode, errmsg);
                throw new WechatAccessTokenException(
                        "微信稳定版接口调用凭据业务返回失败，errcode=" + errcode + ", errmsg=" + errmsg,
                        errcode, httpStatus);
            }
            WechatAccessTokenResponse result = WechatPayUtils.fromJson(responseBody, WechatAccessTokenResponse.class);
            if (result == null || isBlank(result.getAccessToken()) || result.getExpiresIn() == null
                    || result.getExpiresIn() <= 0) {
                log.error("DefaultWechatAccessTokenService.getStableAccessToken 微信接口响应缺少有效凭据，responseBody={}",
                        abbreviate(responseBody));
                throw new WechatAccessTokenException("微信稳定版接口调用凭据响应缺少有效 access_token", null, httpStatus);
            }
            return result;
        } catch (JsonParseException | IllegalStateException e) {
            log.error("DefaultWechatAccessTokenService.getStableAccessToken 解析微信接口响应失败，responseBody={}",
                    abbreviate(responseBody), e);
            throw new WechatAccessTokenException("解析微信稳定版接口调用凭据响应失败", e);
        }
    }

    private WechatAccessConfig getConfig() {
        WechatAccessConfig config = provider.getConfig();
        if (config == null) {
            log.error("DefaultWechatAccessTokenService.getConfig 未获取到微信接口调用凭据配置");
            throw new WechatAccessTokenException("未获取到微信接口调用凭据配置");
        }
        return config;
    }

    private void requireNotBlank(String value, String fieldName) {
        if (isBlank(value)) {
            log.error("DefaultWechatAccessTokenService.requireNotBlank 微信接口调用凭据配置缺少必要参数，fieldName={}", fieldName);
            throw new WechatAccessTokenException("微信接口调用凭据配置不能为空: " + fieldName);
        }
    }

    private void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            log.error("DefaultWechatAccessTokenService.requireNonNull 请求参数不能为空，fieldName={}", fieldName);
            throw new WechatAccessTokenException("请求参数不能为空: " + fieldName);
        }
    }

    private Integer getInteger(JsonObject jsonObject, String memberName) {
        return jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull()
                ? jsonObject.get(memberName).getAsInt() : null;
    }

    private String getString(JsonObject jsonObject, String memberName) {
        return jsonObject.has(memberName) && !jsonObject.get(memberName).isJsonNull()
                ? jsonObject.get(memberName).getAsString() : "";
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String abbreviate(String value) {
        return value == null || value.length() <= 512 ? value : value.substring(0, 512);
    }

    private String maskValue(String value) {
        if (isBlank(value)) {
            return value;
        }
        return value.length() <= 8 ? "***" : value.substring(0, 4) + "***" + value.substring(value.length() - 4);
    }
}
