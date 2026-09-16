package com.hzj.wechat.core.xcx.face.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.enums.WechatHttpMethod;
import com.hzj.wechat.core.xcx.face.WechatXcxFaceVerifyException;
import com.hzj.wechat.core.xcx.face.WechatXcxFaceVerifyService;
import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceApiRequest;
import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceCertInfo;
import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceGetVerifyIdRequest;
import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceGetVerifyIdResponse;
import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceQueryVerifyInfoRequest;
import com.hzj.wechat.core.xcx.face.domain.WechatXcxFaceQueryVerifyInfoResponse;
import com.hzj.wechat.core.xcx.face.enums.WechatXcxFaceCertType;
import com.hzj.wechat.provider.wechat.face.WechatFaceVerifyRuntimeConfigProvider;
import com.hzj.wechat.provider.wechat.face.entity.WechatFaceVerifyRuntimeConfig;
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
import java.util.regex.Pattern;

/**
 * 微信小程序人脸验证默认实现。
 */
@Slf4j
public class DefaultWechatXcxFaceVerifyService implements WechatXcxFaceVerifyService {

    /**
     * out_seq_no 的最小长度。
     */
    private static final int MIN_OUT_SEQ_NO_LENGTH = 5;

    /**
     * out_seq_no 的最大长度。
     */
    private static final int MAX_OUT_SEQ_NO_LENGTH = 32;

    /**
     * out_seq_no 允许的字符集。
     */
    private static final Pattern OUT_SEQ_NO_PATTERN = Pattern.compile("^[0-9A-Za-z_-]+$");

    /**
     * 响应体日志截断长度。
     */
    private static final int LOG_BODY_MAX_LENGTH = 512;

    private final WechatAccessTokenService accessTokenService;

    private final WechatFaceVerifyRuntimeConfigProvider provider;

    private final OkHttpClient client;

    /**
     * 使用默认 OkHttp 客户端创建人脸验证服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     */
    public DefaultWechatXcxFaceVerifyService(WechatAccessTokenService accessTokenService) {
        this(accessTokenService, null, new OkHttpClient.Builder().build());
    }

    /**
     * 使用默认 OkHttp 客户端创建人脸验证服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider           微信人脸核身配置提供者
     */
    public DefaultWechatXcxFaceVerifyService(WechatAccessTokenService accessTokenService,
                                             WechatFaceVerifyRuntimeConfigProvider provider) {
        this(accessTokenService, provider, new OkHttpClient.Builder().build());
    }

    /**
     * 创建微信小程序人脸验证服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider           微信人脸核身配置提供者
     * @param client             HTTP 客户端
     */
    public DefaultWechatXcxFaceVerifyService(WechatAccessTokenService accessTokenService,
                                             WechatFaceVerifyRuntimeConfigProvider provider,
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
    public WechatXcxFaceGetVerifyIdResponse getVerifyId(WechatXcxFaceGetVerifyIdRequest request) {
        String methodName = "DefaultWechatXcxFaceVerifyService.getVerifyId";
        requireRequest(request, methodName);
        applyConfiguredDefaults(request);
        requireOutSeqNo(request.outSeqNo, methodName);
        requireCertInfo(request.certInfo, methodName);
        requireNotBlank(request.openid, "openid", methodName);
        return execute(request, methodName, WechatXcxFaceGetVerifyIdResponse.class);
    }

    @Override
    public WechatXcxFaceGetVerifyIdResponse getVerifyId(String outSeqNo, WechatXcxFaceCertInfo certInfo,
                                                        String openid) {
        WechatXcxFaceGetVerifyIdRequest request = new WechatXcxFaceGetVerifyIdRequest();
        request.outSeqNo = outSeqNo;
        request.certInfo = certInfo;
        request.openid = openid;
        return getVerifyId(request);
    }

    @Override
    public WechatXcxFaceGetVerifyIdResponse getVerifyId(String outSeqNo, WechatXcxFaceCertType certType,
                                                        String certName, String certNo, String openid) {
        WechatXcxFaceCertInfo certInfo = new WechatXcxFaceCertInfo();
        certInfo.certType = certType;
        certInfo.certName = certName;
        certInfo.certNo = certNo;
        return getVerifyId(outSeqNo, certInfo, openid);
    }

    @Override
    public WechatXcxFaceQueryVerifyInfoResponse queryVerifyInfo(WechatXcxFaceQueryVerifyInfoRequest request) {
        String methodName = "DefaultWechatXcxFaceVerifyService.queryVerifyInfo";
        requireRequest(request, methodName);
        requireNotBlank(request.verifyId, "verifyId", methodName);
        requireOutSeqNo(request.outSeqNo, methodName);
        requireNotBlank(request.certHash, "certHash", methodName);
        requireNotBlank(request.openid, "openid", methodName);
        return execute(request, methodName, WechatXcxFaceQueryVerifyInfoResponse.class);
    }

    @Override
    public WechatXcxFaceQueryVerifyInfoResponse queryVerifyInfo(String verifyId, String outSeqNo, String certHash,
                                                                String openid) {
        WechatXcxFaceQueryVerifyInfoRequest request = new WechatXcxFaceQueryVerifyInfoRequest();
        request.verifyId = verifyId;
        request.outSeqNo = outSeqNo;
        request.certHash = certHash;
        request.openid = openid;
        return queryVerifyInfo(request);
    }

    private void applyConfiguredDefaults(WechatXcxFaceGetVerifyIdRequest request) {
        if (request.certInfo != null && request.certInfo.certType == null) {
            request.certInfo.certType = resolveDefaultCertType();
        }
    }

    private WechatXcxFaceCertType resolveDefaultCertType() {
        if (provider != null) {
            WechatFaceVerifyRuntimeConfig config = provider.getConfig();
            if (config != null && config.getCertType() != null) {
                return config.getCertType();
            }
        }
        return WechatXcxFaceCertType.IDENTITY_CARD;
    }

    private <T> T execute(WechatXcxFaceApiRequest request, String methodName, Class<T> responseType) {
        validateApiRequest(request, methodName);
        String accessToken = accessTokenService.getAccessToken();
        if (isBlank(accessToken)) {
            log.error("{} 获取到空 access_token", methodName);
            throw new WechatXcxFaceVerifyException("调用微信人脸核身接口失败：access_token 为空");
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

        log.info("{} 开始调用微信人脸核身接口，requestPath={}", methodName, request.requestPath);
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                log.error("{} 请求微信人脸核身接口失败，code={}, responseBody={}",
                        methodName, response.code(), abbreviate(responseBody));
                throw buildHttpException(responseBody, response.code());
            }
            return parseResponse(responseBody, response.code(), responseType, methodName);
        } catch (IOException e) {
            log.error("{} 调用微信人脸核身接口异常，requestPath={}", methodName, request.requestPath, e);
            throw new UncheckedIOException("调用微信人脸核身接口异常", e);
        }
    }

    private void validateApiRequest(WechatXcxFaceApiRequest request, String methodName) {
        requireRequest(request, methodName);
        if (isBlank(request.requestHost)) {
            log.error("{} requestHost 不能为空", methodName);
            throw new WechatXcxFaceVerifyException("requestHost 不能为空");
        }
        if (isBlank(request.requestPath)) {
            log.error("{} requestPath 不能为空", methodName);
            throw new WechatXcxFaceVerifyException("requestPath 不能为空");
        }
        if (request.requestMethod == null) {
            log.error("{} requestMethod 不能为空", methodName);
            throw new WechatXcxFaceVerifyException("requestMethod 不能为空");
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
                log.error("{} 微信人脸核身接口响应为空，responseBody={}", methodName, abbreviate(responseBody));
                throw new WechatXcxFaceVerifyException("微信人脸核身接口响应为空", null, httpStatus);
            }
            Integer errcode = getInteger(jsonObject, "errcode");
            String errmsg = getString(jsonObject, "errmsg");
            if (errcode != null && errcode != 0) {
                log.error("{} 微信人脸核身接口业务返回失败，errcode={}, errmsg={}", methodName, errcode, errmsg);
                throw new WechatXcxFaceVerifyException(
                        "微信人脸核身接口业务返回失败，errcode=" + errcode + ", errmsg=" + errmsg, errcode, httpStatus);
            }
            T result = WechatPayUtils.fromJson(responseBody, responseType);
            if (result == null) {
                log.error("{} 解析微信人脸核身接口响应为空，responseBody={}", methodName, abbreviate(responseBody));
                throw new WechatXcxFaceVerifyException("解析微信人脸核身接口响应为空", null, httpStatus);
            }
            return result;
        } catch (JsonParseException | IllegalStateException e) {
            log.error("{} 解析微信人脸核身接口响应失败，responseBody={}", methodName, abbreviate(responseBody), e);
            throw new WechatXcxFaceVerifyException("解析微信人脸核身接口响应失败", e);
        }
    }

    private WechatXcxFaceVerifyException buildHttpException(String responseBody, int httpStatus) {
        JsonObject jsonObject = null;
        try {
            jsonObject = WechatPayUtils.fromJson(responseBody, JsonObject.class);
        } catch (JsonParseException | IllegalStateException e) {
            log.warn("DefaultWechatXcxFaceVerifyService.buildHttpException 解析微信错误响应失败，httpStatus={}",
                    httpStatus, e);
        }
        if (jsonObject != null) {
            Integer errcode = getInteger(jsonObject, "errcode");
            if (errcode != null) {
                String errmsg = getString(jsonObject, "errmsg");
                return new WechatXcxFaceVerifyException(
                        "微信人脸核身接口业务返回失败，errcode=" + errcode + ", errmsg=" + errmsg, errcode, httpStatus);
            }
        }
        return new WechatXcxFaceVerifyException(
                "请求微信人脸核身接口失败，HTTP 状态码=" + httpStatus + ", responseBody=" + abbreviate(responseBody),
                null, httpStatus);
    }

    private void requireRequest(WechatXcxFaceApiRequest request, String methodName) {
        if (request == null) {
            log.error("{} 请求参数为空", methodName);
            throw new WechatXcxFaceVerifyException("请求参数不能为空");
        }
    }

    private void requireNotBlank(String value, String fieldName, String methodName) {
        if (isBlank(value)) {
            log.error("{} 请求参数不能为空，fieldName={}", methodName, fieldName);
            throw new WechatXcxFaceVerifyException(fieldName + " 不能为空");
        }
    }

    private void requireOutSeqNo(String outSeqNo, String methodName) {
        if (isBlank(outSeqNo)) {
            log.error("{} outSeqNo 不能为空", methodName);
            throw new WechatXcxFaceVerifyException("out_seq_no 不能为空");
        }
        if (outSeqNo.length() < MIN_OUT_SEQ_NO_LENGTH || outSeqNo.length() > MAX_OUT_SEQ_NO_LENGTH) {
            log.error("{} outSeqNo 长度非法，length={}", methodName, outSeqNo.length());
            throw new WechatXcxFaceVerifyException("out_seq_no 长度必须为 5 至 32 个字符");
        }
        if (!OUT_SEQ_NO_PATTERN.matcher(outSeqNo).matches()) {
            log.error("{} outSeqNo 包含非法字符，outSeqNo={}", methodName, outSeqNo);
            throw new WechatXcxFaceVerifyException("out_seq_no 只能包含数字、大小写字母和 _- 字符");
        }
    }

    private void requireCertInfo(WechatXcxFaceCertInfo certInfo, String methodName) {
        if (certInfo == null) {
            log.error("{} certInfo 不能为空", methodName);
            throw new WechatXcxFaceVerifyException("cert_info 不能为空");
        }
        if (certInfo.certType == null) {
            log.error("{} certType 不能为空", methodName);
            throw new WechatXcxFaceVerifyException("cert_type 不能为空");
        }
        requireNotBlank(certInfo.certName, "cert_name", methodName);
        requireNotBlank(certInfo.certNo, "cert_no", methodName);
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
