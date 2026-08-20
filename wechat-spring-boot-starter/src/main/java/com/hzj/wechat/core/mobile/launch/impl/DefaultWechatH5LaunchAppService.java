package com.hzj.wechat.core.mobile.launch.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.mobile.launch.WechatH5LaunchAppException;
import com.hzj.wechat.core.mobile.launch.WechatH5LaunchAppService;
import com.hzj.wechat.core.mobile.launch.domain.WechatH5JsSdkSignatureRequest;
import com.hzj.wechat.core.mobile.launch.domain.WechatH5JsSdkSignatureResponse;
import com.hzj.wechat.core.mobile.launch.domain.WechatH5LaunchAppSceneIssueRequest;
import com.hzj.wechat.core.mobile.launch.domain.WechatH5LaunchAppSceneResolveResponse;
import com.hzj.wechat.core.mobile.launch.domain.WechatH5LaunchAppSceneResponse;
import com.hzj.wechat.provider.wechat.mobile.launch.WechatH5LaunchAppConfigProvider;
import com.hzj.wechat.provider.wechat.mobile.launch.entity.WechatH5LaunchAppConfig;
import com.hzj.wechat.utils.WechatPayUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 微信 H5 JS-SDK 签名与 Launch App 场景默认实现。
 */
@Slf4j
public class DefaultWechatH5LaunchAppService implements WechatH5LaunchAppService {

    /** 微信 JS-SDK Launch App 开放标签。 */
    private static final List<String> OPEN_TAG_LIST = List.of("wx-open-launch-app");

    /** HMAC SHA-256 算法。 */
    private static final String HMAC_SHA_256 = "HmacSHA256";

    /** Ticket 缓存刷新锁。 */
    private final ReentrantLock ticketRefreshLock = new ReentrantLock(true);

    /** 随机串生成器。 */
    private final SecureRandom random = new SecureRandom();

    /** 微信接口调用凭据服务。 */
    private final WechatAccessTokenService accessTokenService;

    /** 微信 H5 拉起 App 动态配置提供者。 */
    private final WechatH5LaunchAppConfigProvider launchAppConfigProvider;

    /** HTTP 客户端。 */
    private final OkHttpClient client;

    /** 当前 JSAPI Ticket 缓存。 */
    private volatile JsapiTicketCache ticketCache;

    /**
     * 使用默认 OkHttp 客户端创建服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param launchAppConfigProvider 微信 H5 拉起 App 动态配置提供者
     */
    public DefaultWechatH5LaunchAppService(WechatAccessTokenService accessTokenService,
                                           WechatH5LaunchAppConfigProvider launchAppConfigProvider) {
        this(accessTokenService, launchAppConfigProvider, new OkHttpClient.Builder().build());
    }

    /**
     * 创建服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param launchAppConfigProvider 微信 H5 拉起 App 动态配置提供者
     * @param client HTTP 客户端
     */
    public DefaultWechatH5LaunchAppService(WechatAccessTokenService accessTokenService,
                                           WechatH5LaunchAppConfigProvider launchAppConfigProvider,
                                           OkHttpClient client) {
        this.accessTokenService = requireConstructorArgument(accessTokenService, "WechatAccessTokenService");
        this.launchAppConfigProvider = requireConstructorArgument(launchAppConfigProvider,
                "WechatH5LaunchAppConfigProvider");
        this.client = requireConstructorArgument(client, "OkHttpClient");
    }

    @Override
    public WechatH5JsSdkSignatureResponse signJsSdk(WechatH5JsSdkSignatureRequest request) {
        if (request == null) {
            log.error("DefaultWechatH5LaunchAppService.signJsSdk 签名请求不能为空");
            throw new WechatH5LaunchAppException("微信 H5 JS-SDK 签名请求不能为空");
        }
        String url = removeFragment(requireText(request.getUrl(), "url"));
        WechatH5LaunchAppConfig launchConfig = requireLaunchConfig();
        String appid = requireText(launchConfig.getJsSdkAppid(), "wechat.mobile.launch.jsSdkAppid");
        String nonceStr = isBlank(request.getNonceStr()) ? createNonceStr() : request.getNonceStr().trim();
        long timestamp = request.getTimestamp() == null || request.getTimestamp() <= 0
                ? Instant.now().getEpochSecond() : request.getTimestamp();
        String ticket = getJsapiTicket(appid, launchConfig);
        String signContent = "jsapi_ticket=" + ticket + "&noncestr=" + nonceStr + "&timestamp=" + timestamp
                + "&url=" + url;
        String signature = sha1(signContent);
        return new WechatH5JsSdkSignatureResponse(appid, timestamp, nonceStr, signature, OPEN_TAG_LIST);
    }

    @Override
    public WechatH5LaunchAppSceneResponse issueScene(WechatH5LaunchAppSceneIssueRequest request) {
        if (request == null) {
            log.error("DefaultWechatH5LaunchAppService.issueScene 场景签发请求不能为空");
            throw new WechatH5LaunchAppException("微信 H5 Launch App 场景签发请求不能为空");
        }
        WechatH5LaunchAppConfig config = requireLaunchConfig();
        String appid = requireText(config.getAppid(), "wechat.mobile.launch.appid");
        String landingPageUrl = requireText(config.getLandingPageUrl(), "wechat.mobile.launch.landingPageUrl");
        String signingSecret = requireText(config.getSceneSigningSecret(), "wechat.mobile.launch.sceneSigningSecret");
        String targetPath = requireText(request.getTargetPath(), "targetPath");
        long issuedAt = Instant.now().getEpochSecond();
        long expiresAt = issuedAt + requirePositive(config.getSceneTtlSeconds(), "wechat.mobile.launch.sceneTtlSeconds");
        JsonObject payload = new JsonObject();
        payload.addProperty("targetPath", targetPath);
        addOptionalProperty(payload, "targetId", request.getTargetId());
        addOptionalProperty(payload, "extraData", request.getExtraData());
        payload.addProperty("issuedAt", issuedAt);
        payload.addProperty("expiresAt", expiresAt);
        payload.addProperty("nonce", createNonceStr());
        String scene = createScene(WechatPayUtils.toJson(payload), signingSecret);
        return new WechatH5LaunchAppSceneResponse(scene, appendSceneToLandingPage(landingPageUrl, scene), appid, scene,
                expiresAt);
    }

    @Override
    public WechatH5LaunchAppSceneResolveResponse resolveScene(String scene) {
        String normalizedScene = requireText(scene, "scene");
        WechatH5LaunchAppConfig config = requireLaunchConfig();
        String signingSecret = requireText(config.getSceneSigningSecret(), "wechat.mobile.launch.sceneSigningSecret");
        JsonObject payload = parseVerifiedScene(normalizedScene, signingSecret);
        String targetPath = getRequiredString(payload, "targetPath");
        long issuedAt = getRequiredLong(payload, "issuedAt");
        long expiresAt = getRequiredLong(payload, "expiresAt");
        if (Instant.now().getEpochSecond() >= expiresAt) {
            log.error("DefaultWechatH5LaunchAppService.resolveScene 场景码已过期, expiresAt={}", expiresAt);
            throw new WechatH5LaunchAppException("微信 H5 Launch App 场景码已过期");
        }
        return new WechatH5LaunchAppSceneResolveResponse(targetPath, getOptionalString(payload, "targetId"),
                getOptionalString(payload, "extraData"), issuedAt, expiresAt);
    }

    /**
     * 获取有效的 JSAPI Ticket。
     *
     * @param appid 认证服务号 AppID
     * @param config H5 拉起 App 配置
     * @return JSAPI Ticket
     */
    private String getJsapiTicket(String appid, WechatH5LaunchAppConfig config) {
        JsapiTicketCache currentCache = ticketCache;
        if (isTicketValid(currentCache, appid, config)) {
            return currentCache.ticket();
        }
        ticketRefreshLock.lock();
        try {
            currentCache = ticketCache;
            if (isTicketValid(currentCache, appid, config)) {
                return currentCache.ticket();
            }
            String ticket = requestJsapiTicket(config);
            ticketCache = new JsapiTicketCache(appid, config.getJsapiTicketUrl(), ticket,
                    Instant.now().getEpochSecond() + requestTicketExpiresIn(config));
            return ticket;
        } finally {
            ticketRefreshLock.unlock();
        }
    }

    /**
     * 请求微信 JSAPI Ticket。
     *
     * @param config H5 拉起 App 配置
     * @return JSAPI Ticket
     */
    private String requestJsapiTicket(WechatH5LaunchAppConfig config) {
        String ticketUrl = requireText(config.getJsapiTicketUrl(), "wechat.mobile.launch.jsapiTicketUrl");
        HttpUrl baseUrl;
        try {
            baseUrl = HttpUrl.get(ticketUrl);
        } catch (IllegalArgumentException exception) {
            log.error("DefaultWechatH5LaunchAppService.requestJsapiTicket JSAPI Ticket 地址不合法, url={}", ticketUrl,
                    exception);
            throw new WechatH5LaunchAppException("微信 JSAPI Ticket 地址不合法", exception);
        }
        String accessToken = requireText(accessTokenService.getAccessToken(), "accessToken");
        HttpUrl requestUrl = baseUrl.newBuilder()
                .addQueryParameter("access_token", accessToken)
                .addQueryParameter("type", "jsapi")
                .build();
        Request request = new Request.Builder().url(requestUrl).get().build();
        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                log.error("DefaultWechatH5LaunchAppService.requestJsapiTicket 请求微信 JSAPI Ticket 失败, code={}, body={}",
                        response.code(), abbreviate(responseBody));
                throw new WechatH5LaunchAppException("请求微信 JSAPI Ticket 失败，HTTP 状态码=" + response.code());
            }
            return parseJsapiTicket(responseBody);
        } catch (IOException exception) {
            log.error("DefaultWechatH5LaunchAppService.requestJsapiTicket 调用微信 JSAPI Ticket 接口异常", exception);
            throw new UncheckedIOException("调用微信 JSAPI Ticket 接口异常", exception);
        }
    }

    /**
     * 解析微信 JSAPI Ticket 响应。
     *
     * @param responseBody 微信响应内容
     * @return JSAPI Ticket
     */
    private String parseJsapiTicket(String responseBody) {
        try {
            JsonObject response = JsonParser.parseString(responseBody).getAsJsonObject();
            int errcode = response.has("errcode") ? response.get("errcode").getAsInt() : -1;
            if (errcode != 0) {
                String errmsg = getOptionalString(response, "errmsg");
                log.error("DefaultWechatH5LaunchAppService.parseJsapiTicket 微信 JSAPI Ticket 返回失败, errcode={}, errmsg={}",
                        errcode, errmsg);
                throw new WechatH5LaunchAppException("微信 JSAPI Ticket 获取失败，errcode=" + errcode + ", errmsg=" + errmsg);
            }
            String ticket = getRequiredString(response, "ticket");
            Integer expiresIn = response.has("expires_in") && !response.get("expires_in").isJsonNull()
                    ? response.get("expires_in").getAsInt() : null;
            if (expiresIn == null || expiresIn <= 0) {
                log.error("DefaultWechatH5LaunchAppService.parseJsapiTicket 微信 JSAPI Ticket 缺少有效 expires_in");
                throw new WechatH5LaunchAppException("微信 JSAPI Ticket 响应缺少有效 expires_in");
            }
            lastTicketExpiresIn = expiresIn;
            return ticket;
        } catch (JsonParseException | IllegalStateException exception) {
            log.error("DefaultWechatH5LaunchAppService.parseJsapiTicket 解析微信 JSAPI Ticket 响应失败, body={}",
                    abbreviate(responseBody), exception);
            throw new WechatH5LaunchAppException("解析微信 JSAPI Ticket 响应失败", exception);
        }
    }

    /** 最近一次 Ticket 有效期，受刷新锁保护。 */
    private int lastTicketExpiresIn;

    /**
     * 获取最近一次 Ticket 有效期。
     *
     * @param config H5 拉起 App 配置
     * @return Ticket 缓存有效期
     */
    private long requestTicketExpiresIn(WechatH5LaunchAppConfig config) {
        long refreshAhead = Math.max(0L, config.getJsapiTicketRefreshAheadSeconds());
        long expiresIn = lastTicketExpiresIn;
        if (expiresIn <= refreshAhead) {
            log.error("DefaultWechatH5LaunchAppService.requestTicketExpiresIn JSAPI Ticket 有效期不足, expiresIn={}, refreshAhead={}",
                    expiresIn, refreshAhead);
            throw new WechatH5LaunchAppException("微信 JSAPI Ticket 有效期不足以建立缓存");
        }
        return expiresIn;
    }

    /**
     * 判断 Ticket 缓存是否有效。
     *
     * @param cache Ticket 缓存
     * @param appid 认证服务号 AppID
     * @param config H5 拉起 App 配置
     * @return 是否有效
     */
    private boolean isTicketValid(JsapiTicketCache cache, String appid, WechatH5LaunchAppConfig config) {
        if (cache == null || isBlank(config.getJsapiTicketUrl()) || !appid.equals(cache.appid())
                || !config.getJsapiTicketUrl().equals(cache.ticketUrl())) {
            return false;
        }
        return cache.expiresAt() > Instant.now().getEpochSecond() + Math.max(0L, config.getJsapiTicketRefreshAheadSeconds());
    }

    /**
     * 生成场景码。
     *
     * @param payloadJson 场景内容
     * @param signingSecret HMAC 密钥
     * @return 场景码
     */
    private String createScene(String payloadJson, String signingSecret) {
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadJson.getBytes(StandardCharsets.UTF_8));
        String signature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(hmacSha256(payload, signingSecret));
        return payload + "." + signature;
    }

    /**
     * 解析并验签场景码。
     *
     * @param scene 场景码
     * @param signingSecret HMAC 密钥
     * @return 场景内容
     */
    private JsonObject parseVerifiedScene(String scene, String signingSecret) {
        String[] parts = scene.split("\\.", -1);
        if (parts.length != 2 || isBlank(parts[0]) || isBlank(parts[1])) {
            log.error("DefaultWechatH5LaunchAppService.parseVerifiedScene 场景码格式不正确");
            throw new WechatH5LaunchAppException("微信 H5 Launch App 场景码格式不正确");
        }
        try {
            byte[] actualSignature = Base64.getUrlDecoder().decode(parts[1]);
            byte[] expectedSignature = hmacSha256(parts[0], signingSecret);
            if (!MessageDigest.isEqual(expectedSignature, actualSignature)) {
                log.error("DefaultWechatH5LaunchAppService.parseVerifiedScene 场景码签名校验失败");
                throw new WechatH5LaunchAppException("微信 H5 Launch App 场景码签名校验失败");
            }
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[0]), StandardCharsets.UTF_8);
            return JsonParser.parseString(payloadJson).getAsJsonObject();
        } catch (IllegalArgumentException | JsonParseException | IllegalStateException exception) {
            log.error("DefaultWechatH5LaunchAppService.parseVerifiedScene 解析场景码失败", exception);
            throw new WechatH5LaunchAppException("微信 H5 Launch App 场景码解析失败", exception);
        }
    }

    /**
     * 计算 HMAC SHA-256。
     *
     * @param content 原文
     * @param signingSecret HMAC 密钥
     * @return 签名内容
     */
    private byte[] hmacSha256(String content, String signingSecret) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA_256);
            mac.init(new SecretKeySpec(signingSecret.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256));
            return mac.doFinal(content.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            log.error("DefaultWechatH5LaunchAppService.hmacSha256 计算场景码 HMAC 签名失败", exception);
            throw new WechatH5LaunchAppException("计算微信 H5 Launch App 场景码签名失败", exception);
        }
    }

    /**
     * 计算 SHA-1 签名。
     *
     * @param content 原文
     * @return 小写十六进制签名
     */
    private String sha1(String content) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-1")
                    .digest(content.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            log.error("DefaultWechatH5LaunchAppService.sha1 当前 Java 环境不支持 SHA-1", exception);
            throw new WechatH5LaunchAppException("当前 Java 环境不支持 SHA-1", exception);
        }
    }

    /**
     * 拼接携带场景码的落地页地址。
     *
     * @param landingPageUrl 落地页地址
     * @param scene 场景码
     * @return 完整落地页地址
     */
    private String appendSceneToLandingPage(String landingPageUrl, String scene) {
        try {
            return HttpUrl.get(landingPageUrl).newBuilder().addQueryParameter("scene", scene).build().toString();
        } catch (IllegalArgumentException exception) {
            log.error("DefaultWechatH5LaunchAppService.appendSceneToLandingPage 落地页地址不合法, url={}", landingPageUrl,
                    exception);
            throw new WechatH5LaunchAppException("微信 H5 落地页地址不合法", exception);
        }
    }

    /**
     * 移除 URL 中的前端 Hash 部分。
     *
     * @param url 原始 URL
     * @return 签名 URL
     */
    private String removeFragment(String url) {
        int fragmentIndex = url.indexOf('#');
        return fragmentIndex < 0 ? url : url.substring(0, fragmentIndex);
    }

    /**
     * 生成随机串。
     *
     * @return 随机串
     */
    private String createNonceStr() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    /**
     * 获取微信 H5 拉起 App 配置。
     *
     * @return 微信 H5 拉起 App 配置
     */
    private WechatH5LaunchAppConfig requireLaunchConfig() {
        WechatH5LaunchAppConfig config = launchAppConfigProvider.getConfig();
        if (config == null) {
            log.error("DefaultWechatH5LaunchAppService.requireLaunchConfig 未获取到微信 H5 拉起 App 配置");
            throw new WechatH5LaunchAppException("未获取到微信 H5 拉起 App 配置");
        }
        return config;
    }

    /**
     * 获取必填文本字段。
     *
     * @param value 字段值
     * @param fieldName 字段名
     * @return 去除空白后的字段值
     */
    private String requireText(String value, String fieldName) {
        if (isBlank(value)) {
            log.error("DefaultWechatH5LaunchAppService.requireText 必填字段不能为空, fieldName={}", fieldName);
            throw new WechatH5LaunchAppException("微信 H5 Launch App 必填字段不能为空: " + fieldName);
        }
        return value.trim();
    }

    /**
     * 获取正数配置。
     *
     * @param value 配置值
     * @param fieldName 字段名
     * @return 正数配置
     */
    private long requirePositive(long value, String fieldName) {
        if (value <= 0) {
            log.error("DefaultWechatH5LaunchAppService.requirePositive 配置必须为正数, fieldName={}, value={}",
                    fieldName, value);
            throw new WechatH5LaunchAppException("微信 H5 Launch App 配置必须为正数: " + fieldName);
        }
        return value;
    }

    /**
     * 获取场景 JSON 中的必填字符串。
     *
     * @param payload 场景 JSON
     * @param name 字段名
     * @return 字段值
     */
    private String getRequiredString(JsonObject payload, String name) {
        String value = getOptionalString(payload, name);
        if (isBlank(value)) {
            log.error("DefaultWechatH5LaunchAppService.getRequiredString 场景码缺少字段, fieldName={}", name);
            throw new WechatH5LaunchAppException("微信 H5 Launch App 场景码缺少字段: " + name);
        }
        return value;
    }

    /**
     * 获取场景 JSON 中的可选字符串。
     *
     * @param payload 场景 JSON
     * @param name 字段名
     * @return 字段值
     */
    private String getOptionalString(JsonObject payload, String name) {
        return payload.has(name) && !payload.get(name).isJsonNull() ? payload.get(name).getAsString() : null;
    }

    /**
     * 获取场景 JSON 中的必填长整型。
     *
     * @param payload 场景 JSON
     * @param name 字段名
     * @return 字段值
     */
    private long getRequiredLong(JsonObject payload, String name) {
        try {
            if (!payload.has(name) || payload.get(name).isJsonNull()) {
                throw new IllegalStateException("字段不存在");
            }
            return payload.get(name).getAsLong();
        } catch (IllegalStateException | NumberFormatException exception) {
            log.error("DefaultWechatH5LaunchAppService.getRequiredLong 场景码字段不合法, fieldName={}", name, exception);
            throw new WechatH5LaunchAppException("微信 H5 Launch App 场景码字段不合法: " + name, exception);
        }
    }

    /**
     * 添加可选 JSON 字符串字段。
     *
     * @param jsonObject JSON 对象
     * @param name 字段名
     * @param value 字段值
     */
    private void addOptionalProperty(JsonObject jsonObject, String name, String value) {
        if (!isBlank(value)) {
            jsonObject.addProperty(name, value);
        }
    }

    /**
     * 判断字符串是否为空。
     *
     * @param value 待判断字符串
     * @return 是否为空
     */
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 截断响应日志。
     *
     * @param value 原始值
     * @return 截断后的值
     */
    private String abbreviate(String value) {
        return value == null || value.length() <= 512 ? value : value.substring(0, 512);
    }

    /**
     * 校验构造参数。
     *
     * @param value 参数值
     * @param name 参数名
     * @param <T> 参数类型
     * @return 参数值
     */
    private <T> T requireConstructorArgument(T value, String name) {
        if (value == null) {
            log.error("DefaultWechatH5LaunchAppService.requireConstructorArgument 构造参数不能为空, name={}", name);
            throw new IllegalArgumentException(name + " 不能为空");
        }
        return value;
    }

    /**
     * JSAPI Ticket 缓存。
     *
     * @param appid 认证服务号 AppID
     * @param ticketUrl Ticket 请求地址
     * @param ticket JSAPI Ticket
     * @param expiresAt 缓存过期时间，Unix 秒级时间戳
     */
    private record JsapiTicketCache(String appid, String ticketUrl, String ticket, long expiresAt) {
    }
}
