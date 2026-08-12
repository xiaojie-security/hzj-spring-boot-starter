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

    private static final String XPAY_URL = "https://api.weixin.qq.com/xpay/";

    private static final String QUERY_USER_BALANCE = "query_user_balance";

    private static final String CURRENCY_PAY = "currency_pay";

    private static final String QUERY_ORDER = "query_order";

    private static final String CANCEL_CURRENCY_PAY = "cancel_currency_pay";

    private static final String NOTIFY_PROVIDE_GOODS = "notify_provide_goods";

    private static final String PRESENT_CURRENCY = "present_currency";

    private static final String REFUND_ORDER = "refund_order";

    private static final String CREATE_WITHDRAW_ORDER = "create_withdraw_order";

    private static final String QUERY_WITHDRAW_ORDER = "query_withdraw_order";

    private static final String SEND_SUBSCRIBE_PRE_PAYMENT = "send_subscribe_pre_payment";

    private static final String SUBMIT_SUBSCRIBE_PAY_ORDER = "submit_subscribe_pay_order";

    private static final String QUERY_SUBSCRIBE_CONTRACT = "query_subscribe_contract";

    private static final String CANCEL_SUBSCRIBE_CONTRACT = "cancel_subscribe_contract";

    private static final String DOWNLOAD_BILL = "download_bill";

    private static final String START_UPLOAD_GOODS = "start_upload_goods";

    private static final String QUERY_UPLOAD_GOODS = "query_upload_goods";

    private static final String START_PUBLISH_GOODS = "start_publish_goods";

    private static final String QUERY_PUBLISH_GOODS = "query_publish_goods";

    private static final String QUERY_BIZ_BALANCE = "query_biz_balance";

    private static final String QUERY_TRANSFER_ACCOUNT = "query_transfer_account";

    private static final String QUERY_ADVER_FUNDS = "query_adver_funds";

    private static final String CREATE_FUNDS_BILL = "create_funds_bill";

    private static final String BIND_TRANSFER_ACCOUT = "bind_transfer_accout";

    private static final String QUERY_FUNDS_BILL = "query_funds_bill";

    private static final String QUERY_RECOVER_BILL = "query_recover_bill";

    private static final String GET_COMPLAINT_LIST = "get_complaint_list";

    private static final String GET_COMPLAINT_DETAIL = "get_complaint_detail";

    private static final String GET_NEGOTIATION_HISTORY = "get_negotiation_history";

    private static final String RESPONSE_COMPLAINT = "response_complaint";

    private static final String COMPLETE_COMPLAINT = "complete_complaint";

    private static final String UPLOAD_VP_FILE = "upload_vp_file";

    private static final String GET_UPLOAD_FILE_SIGN = "get_upload_file_sign";

    private static final String DOWNLOAD_ADVERFUNDS_ORDER = "download_adverfunds_order";

    private static final String START_DOWNLOAD_ORDER = "start_download_order";

    private static final String QUERY_DOWNLOAD_ORDER = "query_download_order";

    private static final String DOWNLOAD_IOS_SETTLEMENT_BILL = "download_ios_settlement_bill";

    private static final String QUERY_PUNISHMENT_REASONS = "query_punishment_reasons";

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
        WechatVirtualPaymentConfig config = prepare(request, QUERY_USER_BALANCE, true, true, true);
        requireNotBlank(request.getOpenid(), QUERY_USER_BALANCE, "openid");
        requireNotBlank(request.getUserIp(), QUERY_USER_BALANCE, "user_ip");
        return execute(QUERY_USER_BALANCE, request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse currencyPay(WechatCurrencyPayRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, CURRENCY_PAY, true, true, true);
        return execute(CURRENCY_PAY, request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryOrder(WechatQueryOrderRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, QUERY_ORDER, true, false, true);
        requireNotBlank(request.getOpenid(), QUERY_ORDER, "openid");
        requireOneOf(request.getOrderId(), request.getWxOrderId(), QUERY_ORDER, "order_id 或 wx_order_id");
        return execute(QUERY_ORDER, request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse cancelCurrencyPay(WechatCancelCurrencyPayRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, CANCEL_CURRENCY_PAY, true, true, true);
        requireNotBlank(request.getOpenid(), CANCEL_CURRENCY_PAY, "openid");
        requireNotBlank(request.getUserIp(), CANCEL_CURRENCY_PAY, "user_ip");
        requireNotBlank(request.getPayOrderId(), CANCEL_CURRENCY_PAY, "pay_order_id");
        requireNotBlank(request.getOrderId(), CANCEL_CURRENCY_PAY, "order_id");
        requirePositive(request.getAmount(), CANCEL_CURRENCY_PAY, "amount");
        return execute(CANCEL_CURRENCY_PAY, request, config, true);
    }

    @Override
    public void notifyProvideGoods(WechatNotifyProvideGoodsRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, NOTIFY_PROVIDE_GOODS, true, false, false);
        requireOneOf(request.getOrderId(), request.getWxOrderId(), NOTIFY_PROVIDE_GOODS,
                "order_id 或 wx_order_id");
        execute(NOTIFY_PROVIDE_GOODS, request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse presentCurrency(WechatPresentCurrencyRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, PRESENT_CURRENCY, true, false, false);
        requireNotBlank(request.getOpenid(), PRESENT_CURRENCY, "openid");
        requireNotBlank(request.getOrderId(), PRESENT_CURRENCY, "order_id");
        requirePositive(request.getAmount(), PRESENT_CURRENCY, "amount");
        return execute(PRESENT_CURRENCY, request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse refundOrder(WechatRefundOrderRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, REFUND_ORDER, true, false, true);
        requireNotBlank(request.getOpenid(), REFUND_ORDER, "openid");
        requireOneOf(request.getOrderId(), request.getWxOrderId(), REFUND_ORDER, "order_id 或 wx_order_id");
        requireNotBlank(request.getRefundOrderId(), REFUND_ORDER, "refund_order_id");
        requirePositive(request.getLeftFee(), REFUND_ORDER, "left_fee");
        requirePositive(request.getRefundFee(), REFUND_ORDER, "refund_fee");
        requireNotBlank(request.getReqFrom(), REFUND_ORDER, "req_from");
        return execute(REFUND_ORDER, request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse createWithdrawOrder(WechatCreateWithdrawOrderRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, CREATE_WITHDRAW_ORDER, true, false, true);
        requireNotBlank(request.getWithdrawNo(), CREATE_WITHDRAW_ORDER, "withdraw_no");
        return execute(CREATE_WITHDRAW_ORDER, request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryWithdrawOrder(WechatQueryWithdrawOrderRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, QUERY_WITHDRAW_ORDER, true, false, true);
        requireNotBlank(request.getWithdrawNo(), QUERY_WITHDRAW_ORDER, "withdraw_no");
        return execute(QUERY_WITHDRAW_ORDER, request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse sendSubscribePrePayment(WechatSendSubscribePrePaymentRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, SEND_SUBSCRIBE_PRE_PAYMENT, false, false, true);
        requireNotBlank(request.getOpenid(), SEND_SUBSCRIBE_PRE_PAYMENT, "openid");
        requirePositive(request.getDeductPrice(), SEND_SUBSCRIBE_PRE_PAYMENT, "deduct_price");
        requireNotBlank(request.getProductId(), SEND_SUBSCRIBE_PRE_PAYMENT, "product_id");
        requireNotBlank(request.getOutContractCode(), SEND_SUBSCRIBE_PRE_PAYMENT, "out_contract_code");
        return execute(SEND_SUBSCRIBE_PRE_PAYMENT, request, config, false);
    }

    @Override
    public WechatVirtualPaymentResponse submitSubscribePayOrder(WechatSubmitSubscribePayOrderRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, SUBMIT_SUBSCRIBE_PAY_ORDER, true, false, false);
        if (isBlank(request.getOfferId())) {
            request.setOfferId(config.getOfferId());
        }
        if (isBlank(request.getCurrencyType())) {
            request.setCurrencyType(config.getCurrencyType());
        }
        if (request.getBuyQuantity() == null) {
            request.setBuyQuantity(1);
        }
        requireNotBlank(request.getOpenid(), SUBMIT_SUBSCRIBE_PAY_ORDER, "openid");
        requireNotBlank(request.getOfferId(), SUBMIT_SUBSCRIBE_PAY_ORDER, "offer_id");
        requireNotBlank(request.getCurrencyType(), SUBMIT_SUBSCRIBE_PAY_ORDER, "currency_type");
        requireNotBlank(request.getProductId(), SUBMIT_SUBSCRIBE_PAY_ORDER, "product_id");
        requirePositive(request.getDeductPrice(), SUBMIT_SUBSCRIBE_PAY_ORDER, "deduct_price");
        requireNotBlank(request.getOrderId(), SUBMIT_SUBSCRIBE_PAY_ORDER, "order_id");
        return execute(SUBMIT_SUBSCRIBE_PAY_ORDER, request, config, true);
    }

    @Override
    public WechatVirtualPaymentResponse querySubscribeContract(WechatQuerySubscribeContractRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, QUERY_SUBSCRIBE_CONTRACT, false, false, true);
        requireNotBlank(request.getOpenid(), QUERY_SUBSCRIBE_CONTRACT, "openid");
        requireNotBlank(request.getProductId(), QUERY_SUBSCRIBE_CONTRACT, "product_id");
        requireNotBlank(request.getOutContractCode(), QUERY_SUBSCRIBE_CONTRACT, "out_contract_code");
        return execute(QUERY_SUBSCRIBE_CONTRACT, request, config, false);
    }

    @Override
    public WechatVirtualPaymentResponse cancelSubscribeContract(WechatCancelSubscribeContractRequest request) {
        WechatVirtualPaymentConfig config = prepare(request, CANCEL_SUBSCRIBE_CONTRACT, false, false, true);
        requireNotBlank(request.getOpenid(), CANCEL_SUBSCRIBE_CONTRACT, "openid");
        requireNotBlank(request.getTerminationReason(), CANCEL_SUBSCRIBE_CONTRACT, "termination_reason");
        requireNotBlank(request.getProductId(), CANCEL_SUBSCRIBE_CONTRACT, "product_id");
        requireNotBlank(request.getOutContractCode(), CANCEL_SUBSCRIBE_CONTRACT, "out_contract_code");
        return execute(CANCEL_SUBSCRIBE_CONTRACT, request, config, false);
    }

    @Override
    public WechatVirtualPaymentResponse downloadBill(WechatDownloadBillRequest request) {
        if (request != null) {
            requirePositive(request.getBeginDs(), DOWNLOAD_BILL, "begin_ds");
            requirePositive(request.getEndDs(), DOWNLOAD_BILL, "end_ds");
        }
        return executeAdditional(DOWNLOAD_BILL, request, false, false);
    }

    @Override
    public WechatVirtualPaymentResponse startUploadGoods(WechatStartUploadGoodsRequest request) {
        if (request != null) {
            requireList(request.getUploadItem(), START_UPLOAD_GOODS, "upload_item");
        }
        return executeAdditional(START_UPLOAD_GOODS, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryUploadGoods(WechatQueryUploadGoodsRequest request) {
        return executeAdditional(QUERY_UPLOAD_GOODS, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse startPublishGoods(WechatStartPublishGoodsRequest request) {
        if (request != null) {
            requireList(request.getPublishItem(), START_PUBLISH_GOODS, "publish_item");
        }
        return executeAdditional(START_PUBLISH_GOODS, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryPublishGoods(WechatQueryPublishGoodsRequest request) {
        return executeAdditional(QUERY_PUBLISH_GOODS, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryBizBalance(WechatQueryBizBalanceRequest request) {
        return executeAdditional(QUERY_BIZ_BALANCE, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryTransferAccount(WechatQueryTransferAccountRequest request) {
        return executeAdditionalWithoutPaySignature(QUERY_TRANSFER_ACCOUNT, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryAdverFunds(WechatQueryAdverFundsRequest request) {
        return executeAdditionalWithoutPaySignature(QUERY_ADVER_FUNDS, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse createFundsBill(WechatCreateFundsBillRequest request) {
        return executeAdditionalWithoutPaySignature(CREATE_FUNDS_BILL, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse bindTransferAccount(WechatBindTransferAccountRequest request) {
        return executeAdditionalWithoutPaySignature(BIND_TRANSFER_ACCOUT, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryFundsBill(WechatQueryFundsBillRequest request) {
        return executeAdditionalWithoutPaySignature(QUERY_FUNDS_BILL, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryRecoverBill(WechatQueryRecoverBillRequest request) {
        return executeAdditionalWithoutPaySignature(QUERY_RECOVER_BILL, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse getComplaintList(WechatGetComplaintListRequest request) {
        return executeAdditional(GET_COMPLAINT_LIST, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse getComplaintDetail(WechatGetComplaintDetailRequest request) {
        if (request != null) {
            requireNotBlank(request.getComplaintId(), GET_COMPLAINT_DETAIL, "complaint_id");
        }
        return executeAdditional(GET_COMPLAINT_DETAIL, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse getNegotiationHistory(WechatGetNegotiationHistoryRequest request) {
        if (request != null) {
            requireNotBlank(request.getComplaintId(), GET_NEGOTIATION_HISTORY, "complaint_id");
        }
        return executeAdditional(GET_NEGOTIATION_HISTORY, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse responseComplaint(WechatResponseComplaintRequest request) {
        if (request != null) {
            requireNotBlank(request.getComplaintId(), RESPONSE_COMPLAINT, "complaint_id");
            requireNotBlank(request.getResponseContent(), RESPONSE_COMPLAINT, "response_content");
            requireList(request.getResponseImages(), RESPONSE_COMPLAINT, "response_images");
        }
        return executeAdditional(RESPONSE_COMPLAINT, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse completeComplaint(WechatCompleteComplaintRequest request) {
        if (request != null) {
            requireNotBlank(request.getComplaintId(), COMPLETE_COMPLAINT, "complaint_id");
        }
        return executeAdditional(COMPLETE_COMPLAINT, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse uploadVpFile(WechatUploadVpFileRequest request) {
        if (request != null) {
            requireNotBlank(request.getFileName(), UPLOAD_VP_FILE, "file_name");
            if (isBlank(request.getBase64Img()) && isBlank(request.getImgUrl())) {
                log.error("DefaultWechatVirtualPaymentService.{} 缺少图片内容，base64_img 和 img_url 均为空",
                        UPLOAD_VP_FILE);
                throw new WechatVirtualPaymentException("base64_img 和 img_url 至少需要填写一个");
            }
        }
        return executeAdditional(UPLOAD_VP_FILE, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse getUploadFileSign(WechatGetUploadFileSignRequest request) {
        if (request != null) {
            requireNotBlank(request.getWxpayUrl(), GET_UPLOAD_FILE_SIGN, "wxpay_url");
            requireNotBlank(request.getComplaintId(), GET_UPLOAD_FILE_SIGN, "complaint_id");
        }
        return executeAdditional(GET_UPLOAD_FILE_SIGN, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse downloadAdverFundsOrder(WechatDownloadAdverfundsOrderRequest request) {
        if (request != null) {
            requireNotBlank(request.getFundId(), DOWNLOAD_ADVERFUNDS_ORDER, "fund_id");
        }
        return executeAdditionalWithoutPaySignature(DOWNLOAD_ADVERFUNDS_ORDER, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse startDownloadOrder(WechatStartDownloadOrderRequest request) {
        return executeAdditional(START_DOWNLOAD_ORDER, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse queryDownloadOrder(WechatQueryDownloadOrderRequest request) {
        if (request != null) {
            requireNotBlank(request.getTaskId(), QUERY_DOWNLOAD_ORDER, "task_id");
        }
        return executeAdditional(QUERY_DOWNLOAD_ORDER, request, true, true);
    }

    @Override
    public WechatVirtualPaymentResponse downloadIosSettlementBill(WechatDownloadIosSettlementBillRequest request) {
        if (request != null) {
            requireNotBlank(request.getStartMonth(), DOWNLOAD_IOS_SETTLEMENT_BILL, "start_month");
            requireNotBlank(request.getEndMonth(), DOWNLOAD_IOS_SETTLEMENT_BILL, "end_month");
        }
        return executeAdditional(DOWNLOAD_IOS_SETTLEMENT_BILL, request, false, false);
    }

    @Override
    public WechatVirtualPaymentResponse queryPunishmentReasons(WechatQueryPunishmentReasonsRequest request) {
        return executeAdditional(QUERY_PUNISHMENT_REASONS, request, false, false);
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
        if (isBlank(request.getRequestUrl())) {
            request.setRequestUrl(XPAY_URL + action);
        }
        if (request.getRequestMethod() == null) {
            request.setRequestMethod(WechatHttpMethod.POST);
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
