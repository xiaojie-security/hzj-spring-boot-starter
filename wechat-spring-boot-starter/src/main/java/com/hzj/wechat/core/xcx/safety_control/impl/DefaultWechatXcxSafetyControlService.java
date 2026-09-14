package com.hzj.wechat.core.xcx.safety_control.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.enums.WechatHttpMethod;
import com.hzj.wechat.core.xcx.safety_control.WechatXcxSafetyControlException;
import com.hzj.wechat.core.xcx.safety_control.WechatXcxSafetyControlService;
import com.hzj.wechat.core.xcx.safety_control.domain.WechatXcxSafetyControlApiRequest;
import com.hzj.wechat.core.xcx.safety_control.domain.WechatXcxSafetyControlUserRiskRankRequest;
import com.hzj.wechat.core.xcx.safety_control.domain.WechatXcxSafetyControlUserRiskRankResponse;
import com.hzj.wechat.core.xcx.safety_control.enums.WechatXcxSafetyControlScene;
import com.hzj.wechat.provider.wechat.safety_control.WechatSafetyControlConfigProvider;
import com.hzj.wechat.provider.wechat.safety_control.entity.WechatSafetyControlConfig;
import com.hzj.wechat.utils.WechatPayUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * 微信小程序安全风控默认实现。
 */
@Slf4j
public class DefaultWechatXcxSafetyControlService implements WechatXcxSafetyControlService {

    /**
     * 响应体日志截断长度。
     */
    private static final int LOG_BODY_MAX_LENGTH = 512;

    private final WechatAccessTokenService accessTokenService;

    private final WechatSafetyControlConfigProvider provider;

    private final OkHttpClient client;

    /**
     * 使用默认 OkHttp 客户端创建安全风控服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     */
    public DefaultWechatXcxSafetyControlService(WechatAccessTokenService accessTokenService) {
        this(accessTokenService, null, new OkHttpClient.Builder().build());
    }

    /**
     * 使用默认 OkHttp 客户端创建安全风控服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider           微信安全风控配置提供者
     */
    public DefaultWechatXcxSafetyControlService(WechatAccessTokenService accessTokenService,
                                                WechatSafetyControlConfigProvider provider) {
        this(accessTokenService, provider, new OkHttpClient.Builder().build());
    }

    /**
     * 创建微信小程序安全风控服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider           微信安全风控配置提供者
     * @param client             HTTP 客户端
     */
    public DefaultWechatXcxSafetyControlService(WechatAccessTokenService accessTokenService,
                                                WechatSafetyControlConfigProvider provider,
                                                OkHttpClient client) {
        if (accessTokenService == null) {
            throw new IllegalArgumentException("WechatAccessTokenService 不能为空");
        }
        if (client == null) {
            throw new IllegalArgumentException("OkHttpClient 不能为空");
        }
        this.accessTokenService = accessTokenService;
        this.provider = provider;
        this.client = client;
    }

    @Override
    public WechatXcxSafetyControlUserRiskRankResponse getUserRiskRank(
            WechatXcxSafetyControlUserRiskRankRequest request) {
        String methodName = "DefaultWechatXcxSafetyControlService.getUserRiskRank";
        requireRequest(request, methodName);
        applyConfiguredDefaults(request);
        requireNotBlank(request.appid, "appid", methodName);
        requireNotBlank(request.openid, "openid", methodName);
        if (request.scene == null) {
            log.error("{} scene 不能为空", methodName);
            throw new WechatXcxSafetyControlException("scene 不能为空");
        }
        requireNotBlank(request.clientIp, "clientIp", methodName);
        return execute(request, methodName, WechatXcxSafetyControlUserRiskRankResponse.class);
    }

    @Override
    public WechatXcxSafetyControlUserRiskRankResponse getUserRiskRank(String openid,
                                                                     WechatXcxSafetyControlScene scene,
                                                                     String clientIp) {
        WechatXcxSafetyControlUserRiskRankRequest request = new WechatXcxSafetyControlUserRiskRankRequest();
        request.openid = openid;
        request.scene = scene;
        request.clientIp = clientIp;
        return getUserRiskRank(request);
    }

    private void applyConfiguredDefaults(WechatXcxSafetyControlUserRiskRankRequest request) {
        if (isBlank(request.appid)) {
            request.appid = resolveAppid();
        }
        if (request.isTest == null) {
            request.isTest = resolveIsTest();
        }
    }

    private String resolveAppid() {
        WechatSafetyControlConfig config = getConfig();
        return config == null ? null : config.getAppid();
    }

    private Boolean resolveIsTest() {
        WechatSafetyControlConfig config = getConfig();
        if (config == null || config.getIsTest() == null) {
            return Boolean.FALSE;
        }
        return config.getIsTest();
    }

    private WechatSafetyControlConfig getConfig() {
        return provider == null ? null : provider.getConfig();
    }

    private <T> T execute(WechatXcxSafetyControlApiRequest request, String methodName, Class<T> responseType) {
        validateApiRequest(request, methodName);
        String accessToken = accessTokenService.getAccessToken();
        if (isBlank(accessToken)) {
            log.error("{} 获取到空 access_token", methodName);
            throw new WechatXcxSafetyControlException("调用微信安全风控接口失败：access_token 为空");
        }

        String url = HttpUrl.get(request.requestHost + request.requestPath).newBuilder()
                .addQueryParameter("access_token", accessToken)
                .build()
                .toString();
        String requestBody = request.requestMethod == WechatHttpMethod.GET
                ? null : WechatPayUtils.toJson(request);
        Request.Builder requestBuilder = buildHttpRequest(url, request.requestMethod, requestBody)
                .addHeader("Accept", "application/json");
        if (requestBody != null) {
            requestBuilder.addHeader("Content-Type", "application/json");
        }
        Request httpRequest = requestBuilder.build();

        log.info("{} 开始调用微信安全风控接口，requestPath={}", methodName, request.requestPath);
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                log.error("{} 请求微信安全风控接口失败，code={}, responseBody={}",
                        methodName, response.code(), abbreviate(responseBody));
                throw buildHttpException(responseBody, response.code());
            }
            return parseResponse(responseBody, response.code(), responseType, methodName);
        } catch (IOException e) {
            log.error("{} 调用微信安全风控接口异常，requestPath={}", methodName, request.requestPath, e);
            throw new UncheckedIOException("调用微信安全风控接口异常", e);
        }
    }

    private void validateApiRequest(WechatXcxSafetyControlApiRequest request, String methodName) {
        requireRequest(request, methodName);
        if (isBlank(request.requestHost)) {
            log.error("{} requestHost 不能为空", methodName);
            throw new WechatXcxSafetyControlException("requestHost 不能为空");
        }
        if (isBlank(request.requestPath)) {
            log.error("{} requestPath 不能为空", methodName);
            throw new WechatXcxSafetyControlException("requestPath 不能为空");
        }
        if (request.requestMethod == null) {
            log.error("{} requestMethod 不能为空", methodName);
            throw new WechatXcxSafetyControlException("requestMethod 不能为空");
        }
    }

    private Request.Builder buildHttpRequest(String url, WechatHttpMethod requestMethod, String requestBody) {
        Request.Builder builder = new Request.Builder().url(url);
        return switch (requestMethod) {
            case GET -> builder.get();
            case DELETE -> builder.delete();
            case POST, PUT, PATCH -> builder.method(requestMethod.name(), RequestBody.create(
                    MediaType.parse("application/json; charset=utf-8"), requestBody == null ? "" : requestBody));
        };
    }

    private <T> T parseResponse(String responseBody, int httpStatus, Class<T> responseType, String methodName) {
        try {
            JsonObject jsonObject = WechatPayUtils.fromJson(responseBody, JsonObject.class);
            if (jsonObject == null) {
                log.error("{} 微信安全风控接口响应为空，responseBody={}", methodName, abbreviate(responseBody));
                throw new WechatXcxSafetyControlException("微信安全风控接口响应为空", null, httpStatus);
            }
            Integer errcode = getInteger(jsonObject, "errcode");
            String errmsg = getString(jsonObject, "errmsg");
            if (errcode != null && errcode != 0) {
                log.error("{} 微信安全风控接口业务返回失败，errcode={}, errmsg={}", methodName, errcode, errmsg);
                throw new WechatXcxSafetyControlException(
                        "微信安全风控接口业务返回失败，errcode=" + errcode + ", errmsg=" + errmsg, errcode, httpStatus);
            }
            T result = WechatPayUtils.fromJson(responseBody, responseType);
            if (result == null) {
                log.error("{} 解析微信安全风控接口响应为空，responseBody={}", methodName, abbreviate(responseBody));
                throw new WechatXcxSafetyControlException("解析微信安全风控接口响应为空", null, httpStatus);
            }
            return result;
        } catch (JsonParseException | IllegalStateException e) {
            log.error("{} 解析微信安全风控接口响应失败，responseBody={}", methodName, abbreviate(responseBody), e);
            throw new WechatXcxSafetyControlException("解析微信安全风控接口响应失败", e);
        }
    }

    private WechatXcxSafetyControlException buildHttpException(String responseBody, int httpStatus) {
        JsonObject jsonObject = null;
        try {
            jsonObject = WechatPayUtils.fromJson(responseBody, JsonObject.class);
        } catch (JsonParseException | IllegalStateException e) {
            log.warn("DefaultWechatXcxSafetyControlService.buildHttpException 解析微信错误响应失败，httpStatus={}",
                    httpStatus, e);
        }
        if (jsonObject != null) {
            Integer errcode = getInteger(jsonObject, "errcode");
            if (errcode != null) {
                String errmsg = getString(jsonObject, "errmsg");
                return new WechatXcxSafetyControlException(
                        "微信安全风控接口业务返回失败，errcode=" + errcode + ", errmsg=" + errmsg, errcode, httpStatus);
            }
        }
        return new WechatXcxSafetyControlException(
                "请求微信安全风控接口失败，HTTP 状态码=" + httpStatus + ", responseBody=" + abbreviate(responseBody),
                null, httpStatus);
    }

    private void requireRequest(WechatXcxSafetyControlApiRequest request, String methodName) {
        if (request == null) {
            log.error("{} 请求参数为空", methodName);
            throw new WechatXcxSafetyControlException("请求参数不能为空");
        }
    }

    private void requireNotBlank(String value, String fieldName, String methodName) {
        if (isBlank(value)) {
            log.error("{} 请求参数不能为空，fieldName={}", methodName, fieldName);
            throw new WechatXcxSafetyControlException(fieldName + " 不能为空");
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
        return value == null || value.length() <= LOG_BODY_MAX_LENGTH
                ? value : value.substring(0, LOG_BODY_MAX_LENGTH);
    }
}
