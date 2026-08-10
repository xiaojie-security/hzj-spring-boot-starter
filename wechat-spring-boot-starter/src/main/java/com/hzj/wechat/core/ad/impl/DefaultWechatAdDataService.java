package com.hzj.wechat.core.ad.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.ad.WechatAdDataException;
import com.hzj.wechat.core.ad.WechatAdDataService;
import com.hzj.wechat.core.ad.domain.WechatAdBaseResponse;
import com.hzj.wechat.core.ad.domain.WechatAdDataRequest;
import com.hzj.wechat.core.ad.domain.WechatAdDataResponse;
import com.hzj.wechat.core.ad.domain.WechatAdDataDetailResponse;
import com.hzj.wechat.core.ad.domain.WechatAdUnitListResponse;
import com.hzj.wechat.core.ad.domain.WechatAdSettlementResponse;
import com.hzj.wechat.utils.WechatPayUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

/**
 * 微信小程序广告汇总数据默认实现。
 */
@Slf4j
public class DefaultWechatAdDataService implements WechatAdDataService {

    private static final String AD_DATA_URL = "https://api.weixin.qq.com/publisher/stat";

    private static final String SUMMARY_ACTION = "publisher_adpos_general";

    private static final String DETAIL_ACTION = "publisher_adunit_general";

    private static final String AD_UNIT_LIST_ACTION = "get_adunit_list";

    private static final String SETTLEMENT_ACTION = "publisher_settlement";

    private static final int MAX_PAGE_SIZE = 90;

    private static final int MAX_DATE_RANGE_DAYS = 90;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final WechatAccessTokenService accessTokenService;

    private final OkHttpClient client;

    /**
     * 使用默认 OkHttp 客户端创建广告数据服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     */
    public DefaultWechatAdDataService(WechatAccessTokenService accessTokenService) {
        this(accessTokenService, new OkHttpClient.Builder().build());
    }

    /**
     * 创建微信广告数据服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param client HTTP 客户端
     */
    public DefaultWechatAdDataService(WechatAccessTokenService accessTokenService, OkHttpClient client) {
        if (accessTokenService == null) {
            throw new IllegalArgumentException("WechatAccessTokenService 不能为空");
        }
        if (client == null) {
            throw new IllegalArgumentException("OkHttpClient 不能为空");
        }
        this.accessTokenService = accessTokenService;
        this.client = client;
    }

    @Override
    public WechatAdDataResponse getAdDataSummary(WechatAdDataRequest request) {
        return execute(request, SUMMARY_ACTION, "DefaultWechatAdDataService.getAdDataSummary",
                true, true, WechatAdDataResponse.class);
    }

    @Override
    public WechatAdDataDetailResponse getAdDataDetail(WechatAdDataRequest request) {
        return execute(request, DETAIL_ACTION, "DefaultWechatAdDataService.getAdDataDetail",
                true, true, WechatAdDataDetailResponse.class);
    }

    @Override
    public WechatAdUnitListResponse getAdUnitList(WechatAdDataRequest request) {
        return execute(request, AD_UNIT_LIST_ACTION, "DefaultWechatAdDataService.getAdUnitList",
                false, false, WechatAdUnitListResponse.class);
    }

    @Override
    public WechatAdSettlementResponse getSettlementData(WechatAdDataRequest request) {
        return execute(request, SETTLEMENT_ACTION, "DefaultWechatAdDataService.getSettlementData",
                true, false, WechatAdSettlementResponse.class);
    }

    private <T> T execute(WechatAdDataRequest request, String action, String methodName,
                          boolean dateRequired, boolean maxDateRange, Class<T> responseType) {
        validateRequest(request, methodName, dateRequired, maxDateRange);
        LocalDate startDate = dateRequired ? parseDate(request.getStartDate(), "startDate", methodName) : null;
        LocalDate endDate = dateRequired ? parseDate(request.getEndDate(), "endDate", methodName) : null;
        String accessToken = accessTokenService.getAccessToken();
        if (isBlank(accessToken)) {
            log.error("{} 获取到空 access_token", methodName);
            throw new WechatAdDataException("获取微信广告数据失败：access_token 为空");
        }

        HttpUrl.Builder urlBuilder = HttpUrl.get(AD_DATA_URL).newBuilder()
                .addQueryParameter("action", action)
                .addQueryParameter("access_token", accessToken)
                .addQueryParameter("page", String.valueOf(request.getPage()))
                .addQueryParameter("page_size", String.valueOf(request.getPageSize()));
        if (dateRequired) {
            urlBuilder.addQueryParameter("start_date", startDate.format(DATE_FORMATTER))
                    .addQueryParameter("end_date", endDate.format(DATE_FORMATTER));
        }
        if (!isBlank(request.getAdSlot())) {
            urlBuilder.addQueryParameter("ad_slot", request.getAdSlot());
        }
        if (!isBlank(request.getAdUnitId())) {
            urlBuilder.addQueryParameter("ad_unit_id", request.getAdUnitId());
        }

        String url = urlBuilder.build().toString();
        log.info("{} 开始调用微信小程序广告数据接口，action={}, startDate={}, endDate={}, page={}, pageSize={}, adSlot={}, adUnitId={}",
                methodName, action, request.getStartDate(), request.getEndDate(), request.getPage(),
                request.getPageSize(), request.getAdSlot(), request.getAdUnitId());
        Request httpRequest = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .get()
                .build();
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            if (!response.isSuccessful()) {
                log.error("{} 请求微信广告数据接口失败，code={}, responseBody={}", methodName,
                        response.code(), abbreviate(responseBody));
                throw new WechatAdDataException(
                        "请求微信广告数据接口失败，HTTP 状态码=" + response.code(), null, response.code());
            }
            return parseResponse(responseBody, response.code(), responseType, methodName);
        } catch (IOException e) {
            log.error("{} 调用微信广告数据接口异常，startDate={}, endDate={}", methodName,
                    request.getStartDate(), request.getEndDate(), e);
            throw new UncheckedIOException("调用微信广告数据接口异常", e);
        }
    }

    private void validateRequest(WechatAdDataRequest request, String methodName,
                                 boolean dateRequired, boolean maxDateRange) {
        if (request == null) {
            log.error("{} 请求参数为空", methodName);
            throw new WechatAdDataException("请求参数不能为空");
        }
        if (request.getPage() == null || request.getPage() < 1) {
            log.error("{} page 参数非法，page={}", methodName, request.getPage());
            throw new WechatAdDataException("page 必须从 1 开始");
        }
        if (request.getPageSize() == null || request.getPageSize() < 1
                || request.getPageSize() > MAX_PAGE_SIZE) {
            log.error("{} pageSize 参数非法，pageSize={}", methodName,
                    request.getPageSize());
            throw new WechatAdDataException("pageSize 取值范围为 1 至 90");
        }
        if (dateRequired) {
            LocalDate startDate = parseDate(request.getStartDate(), "startDate", methodName);
            LocalDate endDate = parseDate(request.getEndDate(), "endDate", methodName);
            if (endDate.isBefore(startDate)) {
                log.error("{} 日期范围非法，startDate={}, endDate={}", methodName,
                        request.getStartDate(), request.getEndDate());
                throw new WechatAdDataException("endDate 不能早于 startDate");
            }
            if (maxDateRange && ChronoUnit.DAYS.between(startDate, endDate) > MAX_DATE_RANGE_DAYS) {
                log.error("{} 日期跨度超过 90 天，startDate={}, endDate={}", methodName,
                        request.getStartDate(), request.getEndDate());
                throw new WechatAdDataException("startDate 与 endDate 的时间跨度不能超过 90 天");
            }
        }
    }

    private LocalDate parseDate(String value, String fieldName, String methodName) {
        if (isBlank(value)) {
            log.error("{} 日期参数为空，fieldName={}", methodName, fieldName);
            throw new WechatAdDataException(fieldName + " 不能为空，格式必须为 yyyy-MM-dd");
        }
        try {
            return LocalDate.parse(value, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            log.error("{} 日期参数格式非法，fieldName={}, value={}", methodName, fieldName, value, e);
            throw new WechatAdDataException(fieldName + " 格式必须为 yyyy-MM-dd", e);
        }
    }

    private <T> T parseResponse(String responseBody, int httpStatus, Class<T> responseType,
                                String methodName) {
        try {
            JsonObject jsonObject = WechatPayUtils.fromJson(responseBody, JsonObject.class);
            if (jsonObject == null || !jsonObject.has("base_resp")) {
                log.error("{} 微信响应缺少 base_resp，responseBody={}", methodName,
                        abbreviate(responseBody));
                throw new WechatAdDataException("微信广告数据响应格式异常", null, httpStatus);
            }
            WechatAdBaseResponse baseResponse = WechatPayUtils.fromJson(
                    jsonObject.get("base_resp").toString(), WechatAdBaseResponse.class);
            if (baseResponse == null || baseResponse.getRet() == null) {
                log.error("{} 微信响应缺少有效业务状态，responseBody={}", methodName,
                        abbreviate(responseBody));
                throw new WechatAdDataException("微信广告数据响应缺少有效业务状态", null, httpStatus);
            }
            if (baseResponse.getRet() != 0) {
                log.error("{} 微信接口业务返回失败，ret={}, errMsg={}", methodName,
                        baseResponse.getRet(), baseResponse.getErrMsg());
                throw new WechatAdDataException(
                        "微信广告数据业务返回失败，ret=" + baseResponse.getRet()
                                + ", errMsg=" + baseResponse.getErrMsg(),
                        baseResponse.getRet(), httpStatus);
            }
            return WechatPayUtils.fromJson(responseBody, responseType);
        } catch (JsonParseException | IllegalStateException e) {
            log.error("{} 解析微信广告数据响应失败，responseBody={}", methodName, abbreviate(responseBody), e);
            throw new WechatAdDataException("解析微信广告数据响应失败", e);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String abbreviate(String value) {
        return value == null || value.length() <= 512 ? value : value.substring(0, 512);
    }
}
