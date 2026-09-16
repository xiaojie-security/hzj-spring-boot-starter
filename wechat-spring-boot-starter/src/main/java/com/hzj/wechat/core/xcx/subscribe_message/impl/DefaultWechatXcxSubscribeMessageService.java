package com.hzj.wechat.core.xcx.subscribe_message.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.enums.WechatHttpMethod;
import com.hzj.wechat.core.xcx.subscribe_message.WechatXcxSubscribeMessageException;
import com.hzj.wechat.core.xcx.subscribe_message.WechatXcxSubscribeMessageService;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageApiRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCardActivateRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCardExtUpdateRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCardStatusQueryRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCardStatusResponse;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCategoryRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageCategoryResponse;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageDataValue;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageKeywordRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageKeywordResponse;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessagePublicTemplateRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessagePublicTemplateResponse;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageResult;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageSendRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageTemplateAddRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageTemplateAddResponse;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageTemplateDeleteRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageTemplateListRequest;
import com.hzj.wechat.core.xcx.subscribe_message.domain.WechatXcxSubscribeMessageTemplateListResponse;
import com.hzj.wechat.core.xcx.subscribe_message.enums.WechatXcxSubscribeMessageLang;
import com.hzj.wechat.core.xcx.subscribe_message.enums.WechatXcxSubscribeMessageMiniProgramState;
import com.hzj.wechat.provider.wechat.subscribe_message.WechatSubscribeMessageRuntimeConfigProvider;
import com.hzj.wechat.provider.wechat.subscribe_message.entity.WechatSubscribeMessageRuntimeConfig;
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 微信小程序订阅消息默认实现。
 */
@Slf4j
public class DefaultWechatXcxSubscribeMessageService implements WechatXcxSubscribeMessageService {

    /**
     * 获取类目下的公共模板时单次拉取的最大条数。
     */
    private static final int MAX_PUBLIC_TEMPLATE_LIMIT = 30;

    /**
     * 选用模板时关键词数量的下限。
     */
    private static final int MIN_KID_LIST_SIZE = 2;

    /**
     * 选用模板时关键词数量的上限。
     */
    private static final int MAX_KID_LIST_SIZE = 5;

    /**
     * 服务场景描述的最大字符数。
     */
    private static final int MAX_SCENE_DESC_LENGTH = 15;

    /**
     * 姓名关键词纯中文时的最大字符数。
     */
    private static final int MAX_NAME_CHINESE_LENGTH = 10;

    /**
     * 姓名关键词纯字母或符号时的最大字符数。
     */
    private static final int MAX_NAME_LETTER_LENGTH = 20;

    /**
     * 姓名关键词类型。
     */
    private static final String NAME_KEYWORD_TYPE = "name";

    /**
     * 响应体日志截断长度。
     */
    private static final int LOG_BODY_MAX_LENGTH = 512;

    /**
     * 订阅消息关键词类型对应的参数值最大长度。
     * 详见微信官方文档「订阅消息参数值内容限制说明」。
     */
    private static final Map<String, Integer> KEYWORD_TYPE_MAX_LENGTH = Map.of(
            "thing", 20,
            "number", 32,
            "letter", 32,
            "symbol", 5,
            "character_string", 32,
            "phone_number", 17,
            "car_number", 8,
            "phrase", 5);

    private final WechatAccessTokenService accessTokenService;

    private final WechatSubscribeMessageRuntimeConfigProvider provider;

    private final OkHttpClient client;

    /**
     * 使用默认 OkHttp 客户端创建订阅消息服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     */
    public DefaultWechatXcxSubscribeMessageService(WechatAccessTokenService accessTokenService) {
        this(accessTokenService, null, new OkHttpClient.Builder().build());
    }

    /**
     * 使用默认 OkHttp 客户端创建订阅消息服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider 微信订阅消息配置提供者
     */
    public DefaultWechatXcxSubscribeMessageService(WechatAccessTokenService accessTokenService,
                                                   WechatSubscribeMessageRuntimeConfigProvider provider) {
        this(accessTokenService, provider, new OkHttpClient.Builder().build());
    }

    /**
     * 创建微信小程序订阅消息服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider 微信订阅消息配置提供者
     * @param client HTTP 客户端
     */
    public DefaultWechatXcxSubscribeMessageService(WechatAccessTokenService accessTokenService,
                                                   WechatSubscribeMessageRuntimeConfigProvider provider,
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
    public WechatXcxSubscribeMessageResult deleteTemplate(WechatXcxSubscribeMessageTemplateDeleteRequest request) {
        String methodName = "DefaultWechatXcxSubscribeMessageService.deleteTemplate";
        requireRequest(request, methodName);
        requireNotBlank(request.priTmplId, "priTmplId", methodName);
        return execute(request, methodName, null, WechatXcxSubscribeMessageResult.class);
    }

    @Override
    public WechatXcxSubscribeMessageCategoryResponse getCategories(WechatXcxSubscribeMessageCategoryRequest request) {
        String methodName = "DefaultWechatXcxSubscribeMessageService.getCategories";
        requireRequest(request, methodName);
        return execute(request, methodName, null, WechatXcxSubscribeMessageCategoryResponse.class);
    }

    @Override
    public WechatXcxSubscribeMessageKeywordResponse getTemplateKeywords(
            WechatXcxSubscribeMessageKeywordRequest request) {
        String methodName = "DefaultWechatXcxSubscribeMessageService.getTemplateKeywords";
        requireRequest(request, methodName);
        requireNotBlank(request.tid, "tid", methodName);
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("tid", request.tid);
        return execute(request, methodName, queryParams, WechatXcxSubscribeMessageKeywordResponse.class);
    }

    @Override
    public WechatXcxSubscribeMessagePublicTemplateResponse getPublicTemplates(
            WechatXcxSubscribeMessagePublicTemplateRequest request) {
        String methodName = "DefaultWechatXcxSubscribeMessageService.getPublicTemplates";
        requireRequest(request, methodName);
        requireNotBlank(request.ids, "ids", methodName);
        if (request.start == null || request.start < 0) {
            log.error("{} start 参数非法，start={}", methodName, request.start);
            throw new WechatXcxSubscribeMessageException("start 必须从 0 开始");
        }
        if (request.limit == null || request.limit < 1 || request.limit > MAX_PUBLIC_TEMPLATE_LIMIT) {
            log.error("{} limit 参数非法，limit={}", methodName, request.limit);
            throw new WechatXcxSubscribeMessageException("limit 取值范围为 1 至 30");
        }
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("ids", request.ids);
        queryParams.put("start", String.valueOf(request.start));
        queryParams.put("limit", String.valueOf(request.limit));
        return execute(request, methodName, queryParams, WechatXcxSubscribeMessagePublicTemplateResponse.class);
    }

    @Override
    public WechatXcxSubscribeMessageTemplateListResponse getTemplateList(
            WechatXcxSubscribeMessageTemplateListRequest request) {
        String methodName = "DefaultWechatXcxSubscribeMessageService.getTemplateList";
        requireRequest(request, methodName);
        return execute(request, methodName, null, WechatXcxSubscribeMessageTemplateListResponse.class);
    }

    @Override
    public WechatXcxSubscribeMessageResult send(WechatXcxSubscribeMessageSendRequest request) {
        String methodName = "DefaultWechatXcxSubscribeMessageService.send";
        requireRequest(request, methodName);
        applyConfiguredDefaults(request);
        requireNotBlank(request.touser, "touser", methodName);
        requireNotBlank(request.templateId, "templateId", methodName);
        if (!isBlank(request.page) && request.page.startsWith("/")) {
            log.error("{} page 不能以 / 开头，page={}", methodName, request.page);
            throw new WechatXcxSubscribeMessageException("page 不能以 / 开头");
        }
        validateMessageData(request, methodName);
        return execute(request, methodName, null, WechatXcxSubscribeMessageResult.class);
    }

    @Override
    public WechatXcxSubscribeMessageTemplateAddResponse addTemplate(
            WechatXcxSubscribeMessageTemplateAddRequest request) {
        String methodName = "DefaultWechatXcxSubscribeMessageService.addTemplate";
        requireRequest(request, methodName);
        requireNotBlank(request.tid, "tid", methodName);
        if (request.kidList == null || request.kidList.size() < MIN_KID_LIST_SIZE
                || request.kidList.size() > MAX_KID_LIST_SIZE) {
            log.error("{} kidList 参数非法，size={}", methodName,
                    request.kidList == null ? null : request.kidList.size());
            throw new WechatXcxSubscribeMessageException("kidList 最少 2 个、最多 5 个关键词");
        }
        requireNotBlank(request.sceneDesc, "sceneDesc", methodName);
        if (request.sceneDesc.codePointCount(0, request.sceneDesc.length()) > MAX_SCENE_DESC_LENGTH) {
            log.error("{} sceneDesc 超出长度限制，sceneDesc={}", methodName, request.sceneDesc);
            throw new WechatXcxSubscribeMessageException("sceneDesc 不能超过 15 个字");
        }
        return execute(request, methodName, null, WechatXcxSubscribeMessageTemplateAddResponse.class);
    }

    @Override
    public WechatXcxSubscribeMessageResult activateServiceCard(
            WechatXcxSubscribeMessageCardActivateRequest request) {
        String methodName = "DefaultWechatXcxSubscribeMessageService.activateServiceCard";
        requireRequest(request, methodName);
        requireNotBlank(request.openid, "openid", methodName);
        requireNotifyType(request.notifyType, methodName);
        requireNotBlank(request.notifyCode, "notifyCode", methodName);
        requireNotBlank(request.contentJson, "contentJson", methodName);
        return execute(request, methodName, null, WechatXcxSubscribeMessageResult.class);
    }

    @Override
    public WechatXcxSubscribeMessageResult updateServiceCardExtInfo(
            WechatXcxSubscribeMessageCardExtUpdateRequest request) {
        String methodName = "DefaultWechatXcxSubscribeMessageService.updateServiceCardExtInfo";
        requireRequest(request, methodName);
        requireNotBlank(request.openid, "openid", methodName);
        requireNotifyType(request.notifyType, methodName);
        requireNotBlank(request.notifyCode, "notifyCode", methodName);
        requireNotBlank(request.extJson, "extJson", methodName);
        return execute(request, methodName, null, WechatXcxSubscribeMessageResult.class);
    }

    @Override
    public WechatXcxSubscribeMessageCardStatusResponse getServiceCardStatus(
            WechatXcxSubscribeMessageCardStatusQueryRequest request) {
        String methodName = "DefaultWechatXcxSubscribeMessageService.getServiceCardStatus";
        requireRequest(request, methodName);
        requireNotBlank(request.openid, "openid", methodName);
        requireNotBlank(request.notifyCode, "notifyCode", methodName);
        requireNotifyType(request.notifyType, methodName);
        return execute(request, methodName, null, WechatXcxSubscribeMessageCardStatusResponse.class);
    }

    private <T> T execute(WechatXcxSubscribeMessageApiRequest request, String methodName,
                          Map<String, String> queryParams, Class<T> responseType) {
        validateApiRequest(request, methodName);
        String accessToken = accessTokenService.getAccessToken();
        if (isBlank(accessToken)) {
            log.error("{} 获取到空 access_token", methodName);
            throw new WechatXcxSubscribeMessageException("调用微信订阅消息接口失败：access_token 为空");
        }

        HttpUrl.Builder urlBuilder = HttpUrl.get(request.requestHost + request.requestPath).newBuilder()
                .addQueryParameter("access_token", accessToken);
        if (queryParams != null) {
            queryParams.forEach((key, value) -> {
                if (!isBlank(value)) {
                    urlBuilder.addQueryParameter(key, value);
                }
            });
        }
        String url = urlBuilder.build().toString();
        String requestBody = request.requestMethod == WechatHttpMethod.GET
                ? null : WechatPayUtils.toJson(request);
        Request.Builder requestBuilder = buildHttpRequest(url, request.requestMethod, requestBody)
                .addHeader("Accept", "application/json");
        if (requestBody != null) {
            requestBuilder.addHeader("Content-Type", "application/json");
        }
        Request httpRequest = requestBuilder.build();

        log.info("{} 开始调用微信订阅消息接口，requestPath={}, queryParams={}",
                methodName, request.requestPath, queryParams);
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                log.error("{} 请求微信订阅消息接口失败，code={}, responseBody={}",
                        methodName, response.code(), abbreviate(responseBody));
                throw buildHttpException(responseBody, response.code());
            }
            return parseResponse(responseBody, response.code(), responseType, methodName);
        } catch (IOException e) {
            log.error("{} 调用微信订阅消息接口异常，requestPath={}", methodName, request.requestPath, e);
            throw new UncheckedIOException("调用微信订阅消息接口异常", e);
        }
    }

    private void validateApiRequest(WechatXcxSubscribeMessageApiRequest request, String methodName) {
        requireRequest(request, methodName);
        if (isBlank(request.requestHost)) {
            log.error("{} requestHost 不能为空", methodName);
            throw new WechatXcxSubscribeMessageException("requestHost 不能为空");
        }
        if (isBlank(request.requestPath)) {
            log.error("{} requestPath 不能为空", methodName);
            throw new WechatXcxSubscribeMessageException("requestPath 不能为空");
        }
        if (request.requestMethod == null) {
            log.error("{} requestMethod 不能为空", methodName);
            throw new WechatXcxSubscribeMessageException("requestMethod 不能为空");
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

    private <T> T parseResponse(String responseBody, int httpStatus, Class<T> responseType,
                                String methodName) {
        try {
            JsonObject jsonObject = WechatPayUtils.fromJson(responseBody, JsonObject.class);
            if (jsonObject == null) {
                log.error("{} 微信订阅消息接口响应为空，responseBody={}", methodName, abbreviate(responseBody));
                throw new WechatXcxSubscribeMessageException("微信订阅消息接口响应为空", null, httpStatus);
            }
            Integer errcode = getInteger(jsonObject, "errcode");
            String errmsg = getString(jsonObject, "errmsg");
            if (errcode != null && errcode != 0) {
                log.error("{} 微信订阅消息接口业务返回失败，errcode={}, errmsg={}", methodName, errcode, errmsg);
                throw new WechatXcxSubscribeMessageException(
                        "微信订阅消息接口业务返回失败，errcode=" + errcode + ", errmsg=" + errmsg,
                        errcode, httpStatus);
            }
            T result = WechatPayUtils.fromJson(responseBody, responseType);
            if (result == null) {
                log.error("{} 解析微信订阅消息接口响应为空，responseBody={}", methodName, abbreviate(responseBody));
                throw new WechatXcxSubscribeMessageException("解析微信订阅消息接口响应为空", null, httpStatus);
            }
            return result;
        } catch (JsonParseException | IllegalStateException e) {
            log.error("{} 解析微信订阅消息接口响应失败，responseBody={}", methodName,
                    abbreviate(responseBody), e);
            throw new WechatXcxSubscribeMessageException("解析微信订阅消息接口响应失败", e);
        }
    }

    private WechatXcxSubscribeMessageException buildHttpException(String responseBody, int httpStatus) {
        JsonObject jsonObject = null;
        try {
            jsonObject = WechatPayUtils.fromJson(responseBody, JsonObject.class);
        } catch (JsonParseException | IllegalStateException e) {
            log.warn("DefaultWechatXcxSubscribeMessageService.buildHttpException 解析微信错误响应失败，httpStatus={}",
                    httpStatus, e);
        }
        if (jsonObject != null) {
            Integer errcode = getInteger(jsonObject, "errcode");
            if (errcode != null) {
                String errmsg = getString(jsonObject, "errmsg");
                return new WechatXcxSubscribeMessageException(
                        "微信订阅消息接口业务返回失败，errcode=" + errcode + ", errmsg=" + errmsg,
                        errcode, httpStatus);
            }
        }
        return new WechatXcxSubscribeMessageException(
                "请求微信订阅消息接口失败，HTTP 状态码=" + httpStatus + ", responseBody=" + abbreviate(responseBody),
                null, httpStatus);
    }

    private void applyConfiguredDefaults(WechatXcxSubscribeMessageSendRequest request) {
        WechatSubscribeMessageRuntimeConfig config = getConfig();
        if (request.miniprogramState == null) {
            request.miniprogramState = config != null && config.getMiniprogramState() != null
                    ? config.getMiniprogramState() : WechatXcxSubscribeMessageMiniProgramState.FORMAL;
        }
        if (request.lang == null) {
            request.lang = config != null && config.getLang() != null
                    ? config.getLang() : WechatXcxSubscribeMessageLang.ZH_CN;
        }
    }

    private WechatSubscribeMessageRuntimeConfig getConfig() {
        if (provider == null) {
            return null;
        }
        return provider.getConfig();
    }

    private void validateMessageData(WechatXcxSubscribeMessageSendRequest request, String methodName) {
        if (request.data == null || request.data.isEmpty()) {
            log.error("{} data 不能为空", methodName);
            throw new WechatXcxSubscribeMessageException("data 不能为空");
        }
        for (Map.Entry<String, WechatXcxSubscribeMessageDataValue> entry : request.data.entrySet()) {
            String keyword = entry.getKey();
            WechatXcxSubscribeMessageDataValue dataValue = entry.getValue();
            if (isBlank(keyword)) {
                log.error("{} data 中存在空关键词", methodName);
                throw new WechatXcxSubscribeMessageException("data 中的关键词不能为空");
            }
            if (dataValue == null || isBlank(dataValue.value)) {
                log.error("{} data 中关键词 {} 的 value 不能为空", methodName, keyword);
                throw new WechatXcxSubscribeMessageException("data 中关键词 " + keyword + " 的 value 不能为空");
            }
            validateKeywordValue(keyword, dataValue.value, methodName);
        }
    }

    private void validateKeywordValue(String keyword, String value, String methodName) {
        String keywordType = resolveKeywordType(keyword);
        int length = value.codePointCount(0, value.length());
        Integer maxLength = KEYWORD_TYPE_MAX_LENGTH.get(keywordType);
        if (maxLength != null && length > maxLength) {
            log.error("{} data 中关键词 {} 的 value 超出长度限制，length={}, maxLength={}",
                    methodName, keyword, length, maxLength);
            throw new WechatXcxSubscribeMessageException(
                    "data 中关键词 " + keyword + " 的 value 长度不能超过 " + maxLength + " 个字符");
        }
        if (NAME_KEYWORD_TYPE.equals(keywordType)) {
            int limit = hasChinese(value) ? MAX_NAME_CHINESE_LENGTH : MAX_NAME_LETTER_LENGTH;
            if (length > limit) {
                log.error("{} data 中关键词 {} 的 value 超出长度限制，length={}, maxLength={}",
                        methodName, keyword, length, limit);
                throw new WechatXcxSubscribeMessageException(
                        "data 中关键词 " + keyword + " 的 value 长度不能超过 " + limit + " 个字符");
            }
        }
    }

    private String resolveKeywordType(String keyword) {
        int index = 0;
        while (index < keyword.length() && Character.isLetter(keyword.charAt(index))) {
            index++;
        }
        return keyword.substring(0, index).toLowerCase(Locale.ROOT);
    }

    private boolean hasChinese(String value) {
        return value.codePoints().anyMatch(codePoint ->
                Character.UnicodeScript.of(codePoint) == Character.UnicodeScript.HAN);
    }

    private void requireRequest(WechatXcxSubscribeMessageApiRequest request, String methodName) {
        if (request == null) {
            log.error("{} 请求参数为空", methodName);
            throw new WechatXcxSubscribeMessageException("请求参数不能为空");
        }
    }

    private void requireNotBlank(String value, String fieldName, String methodName) {
        if (isBlank(value)) {
            log.error("{} 请求参数不能为空，fieldName={}", methodName, fieldName);
            throw new WechatXcxSubscribeMessageException(fieldName + " 不能为空");
        }
    }

    private void requireNotifyType(Integer notifyType, String methodName) {
        if (notifyType == null || notifyType <= 0) {
            log.error("{} notifyType 参数非法，notifyType={}", methodName, notifyType);
            throw new WechatXcxSubscribeMessageException("notifyType 必须为正整数");
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
