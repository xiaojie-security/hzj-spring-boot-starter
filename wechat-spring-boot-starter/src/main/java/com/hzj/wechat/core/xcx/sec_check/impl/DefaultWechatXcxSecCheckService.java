package com.hzj.wechat.core.xcx.sec_check.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.enums.WechatHttpMethod;
import com.hzj.wechat.core.xcx.sec_check.WechatXcxSecCheckException;
import com.hzj.wechat.core.xcx.sec_check.WechatXcxSecCheckService;
import com.hzj.wechat.core.xcx.sec_check.domain.WechatXcxSecCheckApiRequest;
import com.hzj.wechat.core.xcx.sec_check.domain.WechatXcxSecCheckMediaRequest;
import com.hzj.wechat.core.xcx.sec_check.domain.WechatXcxSecCheckMediaResponse;
import com.hzj.wechat.core.xcx.sec_check.domain.WechatXcxSecCheckMsgRequest;
import com.hzj.wechat.core.xcx.sec_check.domain.WechatXcxSecCheckMsgResponse;
import com.hzj.wechat.core.xcx.sec_check.enums.WechatXcxSecCheckMediaType;
import com.hzj.wechat.core.xcx.sec_check.enums.WechatXcxSecCheckScene;
import com.hzj.wechat.provider.wechat.sec_check.WechatSecCheckConfigProvider;
import com.hzj.wechat.provider.wechat.sec_check.entity.WechatSecCheckConfig;
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
 * 微信小程序内容安全默认实现。
 */
@Slf4j
public class DefaultWechatXcxSecCheckService implements WechatXcxSecCheckService {

    /**
     * 文本内容单次检测的最大字符数。
     */
    private static final int MAX_CONTENT_LENGTH = 2500;

    /**
     * 响应体日志截断长度。
     */
    private static final int LOG_BODY_MAX_LENGTH = 512;

    private final WechatAccessTokenService accessTokenService;

    private final WechatSecCheckConfigProvider provider;

    private final OkHttpClient client;

    /**
     * 使用默认 OkHttp 客户端创建内容安全服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     */
    public DefaultWechatXcxSecCheckService(WechatAccessTokenService accessTokenService) {
        this(accessTokenService, null, new OkHttpClient.Builder().build());
    }

    /**
     * 使用默认 OkHttp 客户端创建内容安全服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider           微信内容安全配置提供者
     */
    public DefaultWechatXcxSecCheckService(WechatAccessTokenService accessTokenService,
                                           WechatSecCheckConfigProvider provider) {
        this(accessTokenService, provider, new OkHttpClient.Builder().build());
    }

    /**
     * 创建微信小程序内容安全服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider           微信内容安全配置提供者
     * @param client             HTTP 客户端
     */
    public DefaultWechatXcxSecCheckService(WechatAccessTokenService accessTokenService,
                                           WechatSecCheckConfigProvider provider,
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
    public WechatXcxSecCheckMsgResponse msgSecCheck(WechatXcxSecCheckMsgRequest request) {
        String methodName = "DefaultWechatXcxSecCheckService.msgSecCheck";
        requireRequest(request, methodName);
        applyConfiguredDefaults(request);
        requireNotBlank(request.content, "content", methodName);
        int contentLength = request.content.codePointCount(0, request.content.length());
        if (contentLength > MAX_CONTENT_LENGTH) {
            log.error("{} content 超出长度限制，length={}", methodName, contentLength);
            throw new WechatXcxSecCheckException("content 不能超过 2500 个字符");
        }
        requireNotBlank(request.openid, "openid", methodName);
        requireVersion(request.version, methodName);
        requireScene(request.scene, methodName);
        if (!isBlank(request.signature) && request.scene != WechatXcxSecCheckScene.PROFILE) {
            log.warn("{} signature 仅在资料类场景（scene=1）有效，当前 scene={}", methodName, request.scene);
        }
        return execute(request, methodName, WechatXcxSecCheckMsgResponse.class);
    }

    @Override
    public WechatXcxSecCheckMsgResponse msgSecCheck(String content, String openid) {
        WechatXcxSecCheckMsgRequest request = new WechatXcxSecCheckMsgRequest();
        request.content = content;
        request.openid = openid;
        return msgSecCheck(request);
    }

    @Override
    public WechatXcxSecCheckMediaResponse mediaCheckAsync(WechatXcxSecCheckMediaRequest request) {
        String methodName = "DefaultWechatXcxSecCheckService.mediaCheckAsync";
        requireRequest(request, methodName);
        applyConfiguredDefaults(request);
        requireNotBlank(request.mediaUrl, "mediaUrl", methodName);
        if (request.mediaType == null) {
            log.error("{} mediaType 不能为空", methodName);
            throw new WechatXcxSecCheckException("mediaType 不能为空");
        }
        requireNotBlank(request.openid, "openid", methodName);
        requireVersion(request.version, methodName);
        requireScene(request.scene, methodName);
        return execute(request, methodName, WechatXcxSecCheckMediaResponse.class);
    }

    @Override
    public WechatXcxSecCheckMediaResponse mediaCheckAsync(String mediaUrl,
                                                          WechatXcxSecCheckMediaType mediaType,
                                                          String openid) {
        WechatXcxSecCheckMediaRequest request = new WechatXcxSecCheckMediaRequest();
        request.mediaUrl = mediaUrl;
        request.mediaType = mediaType;
        request.openid = openid;
        return mediaCheckAsync(request);
    }

    private void applyConfiguredDefaults(WechatXcxSecCheckMsgRequest request) {
        if (request.scene == null) {
            request.scene = resolveDefaultScene();
        }
    }

    private void applyConfiguredDefaults(WechatXcxSecCheckMediaRequest request) {
        if (request.scene == null) {
            request.scene = resolveDefaultScene();
        }
    }

    private WechatXcxSecCheckScene resolveDefaultScene() {
        if (provider == null) {
            return WechatXcxSecCheckScene.PROFILE;
        }
        WechatSecCheckConfig config = provider.getConfig();
        if (config != null && config.getScene() != null) {
            return config.getScene();
        }
        return WechatXcxSecCheckScene.PROFILE;
    }

    private <T> T execute(WechatXcxSecCheckApiRequest request, String methodName, Class<T> responseType) {
        validateApiRequest(request, methodName);
        String accessToken = accessTokenService.getAccessToken();
        if (isBlank(accessToken)) {
            log.error("{} 获取到空 access_token", methodName);
            throw new WechatXcxSecCheckException("调用微信内容安全接口失败：access_token 为空");
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

        log.info("{} 开始调用微信内容安全接口，requestPath={}", methodName, request.requestPath);
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                log.error("{} 请求微信内容安全接口失败，code={}, responseBody={}",
                        methodName, response.code(), abbreviate(responseBody));
                throw buildHttpException(responseBody, response.code());
            }
            return parseResponse(responseBody, response.code(), responseType, methodName);
        } catch (IOException e) {
            log.error("{} 调用微信内容安全接口异常，requestPath={}", methodName, request.requestPath, e);
            throw new UncheckedIOException("调用微信内容安全接口异常", e);
        }
    }

    private void validateApiRequest(WechatXcxSecCheckApiRequest request, String methodName) {
        requireRequest(request, methodName);
        if (isBlank(request.requestHost)) {
            log.error("{} requestHost 不能为空", methodName);
            throw new WechatXcxSecCheckException("requestHost 不能为空");
        }
        if (isBlank(request.requestPath)) {
            log.error("{} requestPath 不能为空", methodName);
            throw new WechatXcxSecCheckException("requestPath 不能为空");
        }
        if (request.requestMethod == null) {
            log.error("{} requestMethod 不能为空", methodName);
            throw new WechatXcxSecCheckException("requestMethod 不能为空");
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
                log.error("{} 微信内容安全接口响应为空，responseBody={}", methodName, abbreviate(responseBody));
                throw new WechatXcxSecCheckException("微信内容安全接口响应为空", null, httpStatus);
            }
            Integer errcode = getInteger(jsonObject, "errcode");
            String errmsg = getString(jsonObject, "errmsg");
            if (errcode != null && errcode != 0) {
                log.error("{} 微信内容安全接口业务返回失败，errcode={}, errmsg={}", methodName, errcode, errmsg);
                throw new WechatXcxSecCheckException(
                        "微信内容安全接口业务返回失败，errcode=" + errcode + ", errmsg=" + errmsg, errcode, httpStatus);
            }
            T result = WechatPayUtils.fromJson(responseBody, responseType);
            if (result == null) {
                log.error("{} 解析微信内容安全接口响应为空，responseBody={}", methodName, abbreviate(responseBody));
                throw new WechatXcxSecCheckException("解析微信内容安全接口响应为空", null, httpStatus);
            }
            return result;
        } catch (JsonParseException | IllegalStateException e) {
            log.error("{} 解析微信内容安全接口响应失败，responseBody={}", methodName, abbreviate(responseBody), e);
            throw new WechatXcxSecCheckException("解析微信内容安全接口响应失败", e);
        }
    }

    private WechatXcxSecCheckException buildHttpException(String responseBody, int httpStatus) {
        JsonObject jsonObject = null;
        try {
            jsonObject = WechatPayUtils.fromJson(responseBody, JsonObject.class);
        } catch (JsonParseException | IllegalStateException e) {
            log.warn("DefaultWechatXcxSecCheckService.buildHttpException 解析微信错误响应失败，httpStatus={}",
                    httpStatus, e);
        }
        if (jsonObject != null) {
            Integer errcode = getInteger(jsonObject, "errcode");
            if (errcode != null) {
                String errmsg = getString(jsonObject, "errmsg");
                return new WechatXcxSecCheckException(
                        "微信内容安全接口业务返回失败，errcode=" + errcode + ", errmsg=" + errmsg, errcode, httpStatus);
            }
        }
        return new WechatXcxSecCheckException(
                "请求微信内容安全接口失败，HTTP 状态码=" + httpStatus + ", responseBody=" + abbreviate(responseBody),
                null, httpStatus);
    }

    private void requireRequest(WechatXcxSecCheckApiRequest request, String methodName) {
        if (request == null) {
            log.error("{} 请求参数为空", methodName);
            throw new WechatXcxSecCheckException("请求参数不能为空");
        }
    }

    private void requireNotBlank(String value, String fieldName, String methodName) {
        if (isBlank(value)) {
            log.error("{} 请求参数不能为空，fieldName={}", methodName, fieldName);
            throw new WechatXcxSecCheckException(fieldName + " 不能为空");
        }
    }

    private void requireVersion(Integer version, String methodName) {
        if (version == null || version != WechatXcxSecCheckMsgRequest.VERSION) {
            log.error("{} version 参数非法，version={}", methodName, version);
            throw new WechatXcxSecCheckException("version 必须为 2");
        }
    }

    private void requireScene(WechatXcxSecCheckScene scene, String methodName) {
        if (scene == null) {
            log.error("{} scene 参数非法，scene=null", methodName);
            throw new WechatXcxSecCheckException("scene 不能为空");
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
