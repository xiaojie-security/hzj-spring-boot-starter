package com.hzj.wechat.core.payment.service.impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.hzj.wechat.core.access.WechatAccessTokenService;
import com.hzj.wechat.core.payment.domain.*;
import com.hzj.wechat.core.payment.service.WechatVirtualPaymentException;
import com.hzj.wechat.core.payment.service.WechatVirtualPaymentService;
import com.hzj.wechat.core.enums.WechatHttpMethod;
import com.hzj.wechat.provider.wechat.virtual.WechatVirtualPaymentConfigProvider;
import com.hzj.wechat.provider.wechat.virtual.entity.WechatVirtualPaymentConfig;
import com.hzj.wechat.utils.WechatPayUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * 微信小程序虚拟支付默认实现。
 */
@Slf4j
public class DefaultWechatVirtualPaymentService implements WechatVirtualPaymentService {

    private final WechatAccessTokenService accessTokenService;

    private final WechatVirtualPaymentConfigProvider provider;

    private final OkHttpClient client;

    /**
     * 使用默认 HTTP 客户端创建服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider 微信配置提供者
     */
    public DefaultWechatVirtualPaymentService(WechatAccessTokenService accessTokenService,
                                              WechatVirtualPaymentConfigProvider provider) {
        this(accessTokenService, provider, new OkHttpClient.Builder().build());
    }

    /**
     * 创建微信虚拟支付服务。
     *
     * @param accessTokenService 微信接口调用凭据服务
     * @param provider 微信配置提供者
     * @param client HTTP 客户端
     */
    public DefaultWechatVirtualPaymentService(WechatAccessTokenService accessTokenService,
                                              WechatVirtualPaymentConfigProvider provider, OkHttpClient client) {
        if (accessTokenService == null) {
            throw new IllegalArgumentException("WechatAccessTokenService 不能为空");
        }
        if (provider == null) {
            throw new IllegalArgumentException("WechatVirtualPaymentConfigProvider 不能为空");
        }
        if (client == null) {
            throw new IllegalArgumentException("OkHttpClient 不能为空");
        }
        this.accessTokenService = accessTokenService;
        this.provider = provider;
        this.client = client;
    }

    @Override
    public WechatVirtualPaymentResponse queryUserBalance(WechatQueryUserBalanceRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), true, true, true);
        requireNotBlank(request.getOpenid(), getAction(request), "openid");
        requireNotBlank(request.getUserIp(), getAction(request), "user_ip");
        return execute(getAction(request), request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse currencyPay(WechatCurrencyPayRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), true, true, true);
        return execute(getAction(request), request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryOrder(WechatQueryOrderRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), true, false, true);
        requireNotBlank(request.getOpenid(), getAction(request), "openid");
        requireOneOf(request.getOrderId(), request.getWxOrderId(), getAction(request), "order_id 或 wx_order_id");
        return execute(getAction(request), request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse cancelCurrencyPay(WechatCancelCurrencyPayRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), true, true, true);
        requireNotBlank(request.getOpenid(), getAction(request), "openid");
        requireNotBlank(request.getUserIp(), getAction(request), "user_ip");
        requireNotBlank(request.getPayOrderId(), getAction(request), "pay_order_id");
        requireNotBlank(request.getOrderId(), getAction(request), "order_id");
        requirePositive(request.getAmount(), getAction(request), "amount");
        return execute(getAction(request), request, config, true);
    }

    @Override
    public void notifyProvideGoods(WechatNotifyProvideGoodsRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), true, false, false);
        requireOneOf(request.getOrderId(), request.getWxOrderId(), getAction(request),
                "order_id 或 wx_order_id");
        execute(getAction(request), request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse presentCurrency(WechatPresentCurrencyRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), true, false, false);
        requireNotBlank(request.getOpenid(), getAction(request), "openid");
        requireNotBlank(request.getOrderId(), getAction(request), "order_id");
        requirePositive(request.getAmount(), getAction(request), "amount");
        return execute(getAction(request), request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse refundOrder(WechatRefundOrderRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), true, false, true);
        requireNotBlank(request.getOpenid(), getAction(request), "openid");
        requireOneOf(request.getOrderId(), request.getWxOrderId(), getAction(request), "order_id 或 wx_order_id");
        requireNotBlank(request.getRefundOrderId(), getAction(request), "refund_order_id");
        requirePositive(request.getLeftFee(), getAction(request), "left_fee");
        requirePositive(request.getRefundFee(), getAction(request), "refund_fee");
        requireNotBlank(request.getReqFrom(), getAction(request), "req_from");
        return execute(getAction(request), request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse createWithdrawOrder(WechatCreateWithdrawOrderRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), true, false, true);
        requireNotBlank(request.getWithdrawNo(), getAction(request), "withdraw_no");
        return execute(getAction(request), request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryWithdrawOrder(WechatQueryWithdrawOrderRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), true, false, true);
        requireNotBlank(request.getWithdrawNo(), getAction(request), "withdraw_no");
        return execute(getAction(request), request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse sendSubscribePrePayment(WechatSendSubscribePrePaymentRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), false, false, true);
        requireNotBlank(request.getOpenid(), getAction(request), "openid");
        requirePositive(request.getDeductPrice(), getAction(request), "deduct_price");
        requireNotBlank(request.getProductId(), getAction(request), "product_id");
        requireNotBlank(request.getOutContractCode(), getAction(request), "out_contract_code");
        return execute(getAction(request), request, config, false);
    }

    @Override
    public WechatVirtualPaymentResponse submitSubscribePayOrder(WechatSubmitSubscribePayOrderRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), true, false, false);
        if (isBlank(request.getOfferId())) {
            request.setOfferId(config.getOfferId());
        }
        if (isBlank(request.getCurrencyType())) {
            request.setCurrencyType(config.getCurrencyType());
        }
        if (request.getBuyQuantity() == null) {
            request.setBuyQuantity(1);
        }
        requireNotBlank(request.getOpenid(), getAction(request), "openid");
        requireNotBlank(request.getOfferId(), getAction(request), "offer_id");
        requireNotBlank(request.getCurrencyType(), getAction(request), "currency_type");
        requireNotBlank(request.getProductId(), getAction(request), "product_id");
        requirePositive(request.getDeductPrice(), getAction(request), "deduct_price");
        requireNotBlank(request.getOrderId(), getAction(request), "order_id");
        return execute(getAction(request), request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse querySubscribeContract(WechatQuerySubscribeContractRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), false, false, true);
        requireNotBlank(request.getOpenid(), getAction(request), "openid");
        requireNotBlank(request.getProductId(), getAction(request), "product_id");
        requireNotBlank(request.getOutContractCode(), getAction(request), "out_contract_code");
        return execute(getAction(request), request, config, false);
    }

    @Override
    public WechatVirtualPaymentResponse cancelSubscribeContract(WechatCancelSubscribeContractRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, getAction(request), false, false, true);
        requireNotBlank(request.getOpenid(), getAction(request), "openid");
        requireNotBlank(request.getTerminationReason(), getAction(request), "termination_reason");
        requireNotBlank(request.getProductId(), getAction(request), "product_id");
        requireNotBlank(request.getOutContractCode(), getAction(request), "out_contract_code");
        return execute(getAction(request), request, config, false);
    }

    @Override
    public WechatVirtualPaymentResponse downloadBill(WechatDownloadBillRequest request) {
        if (request != null) {
            requirePositive(request.getBeginDs(), getAction(request), "begin_ds");
            requirePositive(request.getEndDs(), getAction(request), "end_ds");
        }
        return executeAdditional(getAction(request), request, false, false);
    }

    @Override
    public WechatVirtualPaymentResponse startUploadGoods(WechatStartUploadGoodsRequest request) {
        if (request != null) {
            requireList(request.getUploadItem(), getAction(request), "upload_item");
        }
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryUploadGoods(WechatQueryUploadGoodsRequest request) {
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse startPublishGoods(WechatStartPublishGoodsRequest request) {
        if (request != null) {
            requireList(request.getPublishItem(), getAction(request), "publish_item");
        }
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryPublishGoods(WechatQueryPublishGoodsRequest request) {
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryBizBalance(WechatQueryBizBalanceRequest request) {
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryTransferAccount(WechatQueryTransferAccountRequest request) {
        return executeAdditionalWithoutPaySignature(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryAdverFunds(WechatQueryAdverFundsRequest request) {
        return executeAdditionalWithoutPaySignature(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse createFundsBill(WechatCreateFundsBillRequest request) {
        return executeAdditionalWithoutPaySignature(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse bindTransferAccount(WechatBindTransferAccountRequest request) {
        return executeAdditionalWithoutPaySignature(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryFundsBill(WechatQueryFundsBillRequest request) {
        return executeAdditionalWithoutPaySignature(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryRecoverBill(WechatQueryRecoverBillRequest request) {
        return executeAdditionalWithoutPaySignature(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse getComplaintList(WechatGetComplaintListRequest request) {
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse getComplaintDetail(WechatGetComplaintDetailRequest request) {
        if (request != null) {
            requireNotBlank(request.getComplaintId(), getAction(request), "complaint_id");
        }
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse getNegotiationHistory(WechatGetNegotiationHistoryRequest request) {
        if (request != null) {
            requireNotBlank(request.getComplaintId(), getAction(request), "complaint_id");
        }
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse responseComplaint(WechatResponseComplaintRequest request) {
        if (request != null) {
            requireNotBlank(request.getComplaintId(), getAction(request), "complaint_id");
            requireNotBlank(request.getResponseContent(), getAction(request), "response_content");
            requireList(request.getResponseImages(), getAction(request), "response_images");
        }
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse completeComplaint(WechatCompleteComplaintRequest request) {
        if (request != null) {
            requireNotBlank(request.getComplaintId(), getAction(request), "complaint_id");
        }
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse uploadVpFile(WechatUploadVpFileRequest request) {
        if (request != null) {
            requireNotBlank(request.getFileName(), getAction(request), "file_name");
            if (isBlank(request.getBase64Img()) && isBlank(request.getImgUrl())) {
                log.error("DefaultWechatVirtualPaymentService.{} 缺少图片内容，base64_img 和 img_url 均为空",
                        getAction(request));
                throw new WechatVirtualPaymentException("base64_img 和 img_url 至少需要填写一个");
            }
        }
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse getUploadFileSign(WechatGetUploadFileSignRequest request) {
        if (request != null) {
            requireNotBlank(request.getWxpayUrl(), getAction(request), "wxpay_url");
            requireNotBlank(request.getComplaintId(), getAction(request), "complaint_id");
        }
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse downloadAdverFundsOrder(WechatDownloadAdverfundsOrderRequest request) {
        if (request != null) {
            requireNotBlank(request.getFundId(), getAction(request), "fund_id");
        }
        return executeAdditionalWithoutPaySignature(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse startDownloadOrder(WechatStartDownloadOrderRequest request) {
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryDownloadOrder(WechatQueryDownloadOrderRequest request) {
        if (request != null) {
            requireNotBlank(request.getTaskId(), getAction(request), "task_id");
        }
        return executeAdditional(getAction(request), request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse downloadIosSettlementBill(WechatDownloadIosSettlementBillRequest request) {
        if (request != null) {
            requireNotBlank(request.getStartMonth(), getAction(request), "start_month");
            requireNotBlank(request.getEndMonth(), getAction(request), "end_month");
        }
        return executeAdditional(getAction(request), request, false, false);
    }

    @Override
    public WechatVirtualPaymentResponse queryPunishmentReasons(WechatQueryPunishmentReasonsRequest request) {
        return executeAdditional(getAction(request), request, false, false);
    }

    private WechatVirtualPaymentResponse executeAdditional(String action, WechatVirtualPaymentRequest request,
                                                           boolean envRequired, boolean envIncluded) {
        WechatVirtualPaymentConfig config = prepare(request, action, envRequired, false, true);
        return execute(action, request, config, envIncluded);
    }

    private WechatVirtualPaymentResponse executeAdditionalWithoutPaySignature(
            String action, WechatVirtualPaymentRequest request, boolean envRequired, boolean envIncluded) {
        WechatVirtualPaymentConfig config = prepare(request, action, envRequired, false, false);
        return execute(action, request, config, envIncluded);
    }

    private WechatVirtualPaymentConfig prepare(WechatVirtualPaymentRequest request, String action,
                                               boolean envRequired, boolean signatureRequired,
                                               boolean paySigRequired) {
        if (request == null) {
            log.error("DefaultWechatVirtualPaymentService.{} 请求参数为空", action);
            throw new WechatVirtualPaymentException("请求参数不能为空");
        }
        if (request.getRequestApi() == null) {
            log.error("DefaultWechatVirtualPaymentService.{} 请求参数未绑定虚拟支付接口", action);
            throw new WechatVirtualPaymentException("请求参数未绑定微信虚拟支付接口");
        }
        if (isBlank(request.getRequestUrl())) {
            request.setRequestUrl(request.getRequestApi().getRequestUrl());
        }
        if (request.getRequestMethod() == null) {
            request.setRequestMethod(request.getRequestApi().getRequestMethod());
        }
        WechatVirtualPaymentConfig config = provider.getConfig();
        if (config == null) {
            log.error("DefaultWechatVirtualPaymentService.{} 未获取到虚拟支付配置", action);
            throw new WechatVirtualPaymentException("未获取到微信虚拟支付配置");
        }
        if (envRequired) {
            if (request.getEnv() == null) {
                request.setEnv(config.getEnv() == null ? 0 : config.getEnv());
            }
            if (request.getEnv() != 0 && request.getEnv() != 1) {
                log.error("DefaultWechatVirtualPaymentService.{} env 参数非法，env={}", action, request.getEnv());
                throw new WechatVirtualPaymentException("env 只能是 0 或 1");
            }
        }
        if (signatureRequired) {
            requireNotBlank(request.getSignature(), action, "signature");
        }
        if (paySigRequired) {
            requireNotBlank(request.getPaySig(), action, "pay_sig");
        }
        return config;
    }

    private WechatVirtualPaymentResponse execute(String action, WechatVirtualPaymentRequest request,
                                                 WechatVirtualPaymentConfig config, boolean envIncluded) {
        String accessToken = accessTokenService.getAccessToken();
        if (isBlank(accessToken)) {
            log.error("DefaultWechatVirtualPaymentService.{} 获取到空 access_token", action);
            throw new WechatVirtualPaymentException("获取微信虚拟支付失败：access_token 为空");
        }
        HttpUrl.Builder urlBuilder = HttpUrl.get(request.getRequestUrl()).newBuilder()
                .addQueryParameter("access_token", accessToken);
        if (!isBlank(request.getSignature())) {
            urlBuilder.addQueryParameter("signature", request.getSignature());
        }
        if (!isBlank(request.getPaySig())) {
            urlBuilder.addQueryParameter("pay_sig", request.getPaySig());
        }
        JsonObject requestJson = WechatPayUtils.fromJson(WechatPayUtils.toJson(request), JsonObject.class);
        if (!envIncluded) {
            requestJson.remove("env");
        }
        String requestBody = WechatPayUtils.toJson(requestJson);
        Request httpRequest = buildHttpRequest(urlBuilder.build(), request.getRequestMethod(), requestBody)
                .url(urlBuilder.build())
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .build();
        log.info("DefaultWechatVirtualPaymentService.{} 开始调用微信虚拟支付接口，env={}", action, request.getEnv());
        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body() == null ? "" : response.body().string();
            return parseResponse(action, responseBody, response.code(), response.isSuccessful());
        } catch (IOException e) {
            log.error("DefaultWechatVirtualPaymentService.{} 调用微信虚拟支付接口异常，env={}", action,
                    request.getEnv(), e);
            throw new UncheckedIOException("调用微信虚拟支付接口异常", e);
        }
    }

    private Request.Builder buildHttpRequest(HttpUrl url, WechatHttpMethod requestMethod, String requestBody) {
        Request.Builder builder = new Request.Builder().url(url);
        return switch (requestMethod) {
            case GET -> builder.get();
            case DELETE -> builder.delete();
            case POST, PUT, PATCH -> builder.method(requestMethod.name(),
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), requestBody));
        };
    }

    private WechatVirtualPaymentResponse parseResponse(String action, String responseBody, int httpStatus,
                                                       boolean httpSuccess) {
        try {
            JsonObject jsonObject = WechatPayUtils.fromJson(responseBody, JsonObject.class);
            Integer errcode = jsonObject == null || !jsonObject.has("errcode")
                    ? null : jsonObject.get("errcode").getAsInt();
            String errmsg = jsonObject == null || !jsonObject.has("errmsg")
                    ? "" : jsonObject.get("errmsg").getAsString();
            if (!httpSuccess) {
                log.error("DefaultWechatVirtualPaymentService.{} 请求微信接口失败，code={}, responseBody={}",
                        action, httpStatus, abbreviate(responseBody));
                throw new WechatVirtualPaymentException(
                        "请求微信虚拟支付接口失败，HTTP 状态码=" + httpStatus, errcode, httpStatus);
            }
            if (errcode == null) {
                log.error("DefaultWechatVirtualPaymentService.{} 微信响应缺少 errcode，responseBody={}",
                        action, abbreviate(responseBody));
                throw new WechatVirtualPaymentException("微信虚拟支付响应格式异常", null, httpStatus);
            }
            if (errcode != 0) {
                log.error("DefaultWechatVirtualPaymentService.{} 微信接口业务返回失败，errcode={}, errmsg={}",
                        action, errcode, errmsg);
                throw new WechatVirtualPaymentException(
                        "微信虚拟支付业务返回失败，errcode=" + errcode + ", errmsg=" + errmsg,
                        errcode, httpStatus);
            }
            return WechatPayUtils.fromJson(responseBody, WechatVirtualPaymentResponse.class);
        } catch (JsonParseException | IllegalStateException e) {
            log.error("DefaultWechatVirtualPaymentService.{} 解析微信虚拟支付响应失败，responseBody={}",
                    action, abbreviate(responseBody), e);
            throw new WechatVirtualPaymentException("解析微信虚拟支付响应失败", e);
        }
    }

    private String getAction(WechatVirtualPaymentRequest request) {
        return request == null || request.getRequestApi() == null ? "unknown" : request.getRequestApi().getAction();
    }

    private void requireNotBlank(String value, String action, String fieldName) {
        if (isBlank(value)) {
            log.error("DefaultWechatVirtualPaymentService.{} 请求参数为空，fieldName={}", action, fieldName);
            throw new WechatVirtualPaymentException("请求参数不能为空: " + fieldName);
        }
    }

    private void requireOneOf(String first, String second, String action, String fieldName) {
        if (isBlank(first) && isBlank(second)) {
            log.error("DefaultWechatVirtualPaymentService.{} 请求参数缺少必要字段，fieldName={}", action, fieldName);
            throw new WechatVirtualPaymentException("请求参数至少需要一个: " + fieldName);
        }
    }

    private void requirePositive(Long value, String action, String fieldName) {
        if (value == null || value <= 0) {
            log.error("DefaultWechatVirtualPaymentService.{} 数值参数非法，fieldName={}, value={}",
                    action, fieldName, value);
            throw new WechatVirtualPaymentException(fieldName + " 必须大于 0");
        }
    }

    private void requireList(List<?> value, String action, String fieldName) {
        if (value == null || value.isEmpty()) {
            log.error("DefaultWechatVirtualPaymentService.{} 列表参数为空，fieldName={}", action, fieldName);
            throw new WechatVirtualPaymentException(fieldName + " 不能为空");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String abbreviate(String value) {
        return value == null || value.length() <= 512 ? value : value.substring(0, 512);
    }
}
