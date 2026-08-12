package com.hzj.wechat.core.payment.service.impl;

import com.hzj.wechat.core.payment.domain.AbnormalRefundRequest;
import com.hzj.wechat.core.payment.domain.BillDownloadEntity;
import com.hzj.wechat.core.payment.domain.FundFlowBillRequest;
import com.hzj.wechat.core.payment.domain.PaymentCloseOrderRequest;
import com.hzj.wechat.core.payment.domain.PaymentOrderEntity;
import com.hzj.wechat.core.payment.domain.PaymentPrepayRequest;
import com.hzj.wechat.core.payment.domain.PaymentPrepayResponse;
import com.hzj.wechat.core.payment.domain.QueryOrderByOutTradeNoRequest;
import com.hzj.wechat.core.payment.domain.QueryOrderByTransactionIdRequest;
import com.hzj.wechat.core.payment.domain.QueryRefundByOutRefundNoRequest;
import com.hzj.wechat.core.payment.domain.RefundEntity;
import com.hzj.wechat.core.payment.domain.RefundRequest;
import com.hzj.wechat.core.payment.domain.TradeBillRequest;
import com.hzj.wechat.core.payment.service.WechatPaymentService;
import com.hzj.wechat.provider.wechat.payment.WechatPaymentConfigProvider;
import com.hzj.wechat.provider.wechat.payment.entity.WechatPaymentConfig;
import com.hzj.wechat.utils.WechatPayUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DefaultWechatPaymentService implements WechatPaymentService {
    private final WechatPaymentConfigProvider provider;
    private final OkHttpClient client = new OkHttpClient.Builder().build();

    @Override
    public PaymentPrepayResponse jsapiPrepay(PaymentPrepayRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "POST";
        String path = "/v3/pay/transactions/jsapi";
        WechatPaymentConfig config = getConfig();

        if (isBlank(request.appid)) {
            request.appid = config.getAppid();
        }
        if (isBlank(request.mchid)) {
            request.mchid = config.getMchid();
        }
        if (isBlank(request.notifyUrl)) {
            request.notifyUrl = config.getPaymentNotifyUrl();
        }

        String reqBody = WechatPayUtils.toJson(request);
        return executeJsonRequest(config, host, method, path, reqBody, PaymentPrepayResponse.class);
    }

    @Override
    public PaymentPrepayResponse appPrepay(PaymentPrepayRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "POST";
        String path = "/v3/pay/transactions/app";
        WechatPaymentConfig config = getConfig();

        if (isBlank(request.appid)) {
            request.appid = config.getAppid();
        }
        if (isBlank(request.mchid)) {
            request.mchid = config.getMchid();
        }
        if (isBlank(request.notifyUrl)) {
            request.notifyUrl = config.getPaymentNotifyUrl();
        }

        String reqBody = WechatPayUtils.toJson(request);
        return executeJsonRequest(config, host, method, path, reqBody, PaymentPrepayResponse.class);
    }

    @Override
    public PaymentPrepayResponse h5Prepay(PaymentPrepayRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "POST";
        String path = "/v3/pay/transactions/h5";
        WechatPaymentConfig config = getConfig();

        if (isBlank(request.appid)) {
            request.appid = config.getAppid();
        }
        if (isBlank(request.mchid)) {
            request.mchid = config.getMchid();
        }
        if (isBlank(request.notifyUrl)) {
            request.notifyUrl = config.getPaymentNotifyUrl();
        }

        String reqBody = WechatPayUtils.toJson(request);
        return executeJsonRequest(config, host, method, path, reqBody, PaymentPrepayResponse.class);
    }

    @Override
    public PaymentPrepayResponse nativePrepay(PaymentPrepayRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "POST";
        String path = "/v3/pay/transactions/native";
        WechatPaymentConfig config = getConfig();

        if (isBlank(request.appid)) {
            request.appid = config.getAppid();
        }
        if (isBlank(request.mchid)) {
            request.mchid = config.getMchid();
        }
        if (isBlank(request.notifyUrl)) {
            request.notifyUrl = config.getPaymentNotifyUrl();
        }

        String reqBody = WechatPayUtils.toJson(request);
        return executeJsonRequest(config, host, method, path, reqBody, PaymentPrepayResponse.class);
    }

    @Override
    public PaymentOrderEntity queryOrderByTransactionId(QueryOrderByTransactionIdRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "GET";
        String path = "/v3/pay/transactions/id/{transaction_id}";
        WechatPaymentConfig config = getConfig();

        if (isBlank(request.mchid)) {
            request.mchid = config.getMchid();
        }

        String uri = path.replace("{transaction_id}", WechatPayUtils.urlEncode(request.transactionId));
        Map<String, Object> args = new HashMap<>();
        args.put("mchid", request.mchid);
        String queryString = WechatPayUtils.urlEncode(args);
        if (!queryString.isEmpty()) {
            uri = uri + "?" + queryString;
        }
        return executeJsonRequest(config, host, method, uri, null, PaymentOrderEntity.class);
    }

    @Override
    public PaymentOrderEntity queryOrderByOutTradeNo(QueryOrderByOutTradeNoRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "GET";
        String path = "/v3/pay/transactions/out-trade-no/{out_trade_no}";
        WechatPaymentConfig config = getConfig();

        if (isBlank(request.mchid)) {
            request.mchid = config.getMchid();
        }

        String uri = path.replace("{out_trade_no}", WechatPayUtils.urlEncode(request.outTradeNo));
        Map<String, Object> args = new HashMap<>();
        args.put("mchid", request.mchid);
        String queryString = WechatPayUtils.urlEncode(args);
        if (!queryString.isEmpty()) {
            uri = uri + "?" + queryString;
        }
        return executeJsonRequest(config, host, method, uri, null, PaymentOrderEntity.class);
    }

    @Override
    public void closeOrder(PaymentCloseOrderRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "POST";
        String path = "/v3/pay/transactions/out-trade-no/{out_trade_no}/close";
        WechatPaymentConfig config = getConfig();

        if (isBlank(request.mchid)) {
            request.mchid = config.getMchid();
        }

        String uri = path.replace("{out_trade_no}", WechatPayUtils.urlEncode(request.outTradeNo));
        String reqBody = WechatPayUtils.toJson(request);
        executeNoContentRequest(config, host, method, uri, reqBody);
    }

    @Override
    public RefundEntity createRefund(RefundRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "POST";
        String path = "/v3/refund/domestic/refunds";
        WechatPaymentConfig config = getConfig();

        String reqBody = WechatPayUtils.toJson(request);
        return executeJsonRequest(config, host, method, path, reqBody, RefundEntity.class);
    }

    @Override
    public RefundEntity queryRefundByOutRefundNo(QueryRefundByOutRefundNoRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "GET";
        String path = "/v3/refund/domestic/refunds/{out_refund_no}";
        WechatPaymentConfig config = getConfig();

        String uri = path.replace("{out_refund_no}", WechatPayUtils.urlEncode(request.outRefundNo));
        return executeJsonRequest(config, host, method, uri, null, RefundEntity.class);
    }

    @Override
    public RefundEntity createAbnormalRefund(AbnormalRefundRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "POST";
        String path = "/v3/refund/domestic/refunds/{refund_id}/apply-abnormal-refund";
        WechatPaymentConfig config = getConfig();

        String uri = path.replace("{refund_id}", WechatPayUtils.urlEncode(request.refundId));
        if (!isBlank(request.bankAccount)) {
            request.bankAccount = WechatPayUtils.encrypt(config.getWechatPayPublicKey(), request.bankAccount);
        }
        if (!isBlank(request.realName)) {
            request.realName = WechatPayUtils.encrypt(config.getWechatPayPublicKey(), request.realName);
        }
        String reqBody = WechatPayUtils.toJson(request);
        return executeJsonRequest(config, host, method, uri, reqBody, RefundEntity.class);
    }

    @Override
    public BillDownloadEntity getTradeBill(TradeBillRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "GET";
        String path = "/v3/bill/tradebill";
        WechatPaymentConfig config = getConfig();

        String uri = path;
        Map<String, Object> args = new HashMap<>();
        args.put("bill_date", request.billDate);
        args.put("bill_type", request.billType);
        args.put("tar_type", request.tarType);
        String queryString = WechatPayUtils.urlEncode(args);
        if (!queryString.isEmpty()) {
            uri = uri + "?" + queryString;
        }
        return executeJsonRequest(config, host, method, uri, null, BillDownloadEntity.class);
    }

    @Override
    public BillDownloadEntity getFundFlowBill(FundFlowBillRequest request) {
        String host = "https://api.mch.weixin.qq.com";
        String method = "GET";
        String path = "/v3/bill/fundflowbill";
        WechatPaymentConfig config = getConfig();

        String uri = path;
        Map<String, Object> args = new HashMap<>();
        args.put("bill_date", request.billDate);
        args.put("account_type", request.accountType);
        args.put("tar_type", request.tarType);
        String queryString = WechatPayUtils.urlEncode(args);
        if (!queryString.isEmpty()) {
            uri = uri + "?" + queryString;
        }
        return executeJsonRequest(config, host, method, uri, null, BillDownloadEntity.class);
    }

    private <T> T executeJsonRequest(WechatPaymentConfig config, String host, String method, String uri,
                                     String reqBody, Class<T> responseClass) {
        Request.Builder reqBuilder = new Request.Builder().url(host + uri);
        reqBuilder.addHeader("Accept", "application/json");
        reqBuilder.addHeader("Wechatpay-Serial", config.getWechatPayPublicKeyId());
        reqBuilder.addHeader("Authorization",
                WechatPayUtils.buildAuthorization(config.getMchid(), config.getCertificateSerialNo(),
                        config.getPrivateKey(), method, uri, reqBody));
        if (reqBody != null) {
            reqBuilder.addHeader("Content-Type", "application/json");
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqBody);
            reqBuilder.method(method, requestBody);
        } else {
            reqBuilder.method(method, null);
        }
        Request httpRequest = reqBuilder.build();

        try (Response httpResponse = client.newCall(httpRequest).execute()) {
            String respBody = WechatPayUtils.extractBody(httpResponse);
            if (httpResponse.code() >= 200 && httpResponse.code() < 300) {
                WechatPayUtils.validateResponse(config.getWechatPayPublicKeyId(), config.getWechatPayPublicKey(),
                        httpResponse.headers(), respBody);
                return WechatPayUtils.fromJson(respBody, responseClass);
            } else {
                log.error("DefaultWechatPaymentService.executeJsonRequest 请求微信支付接口失败，uri={}, code={}, respBody={}",
                        uri, httpResponse.code(), respBody);
                throw new WechatPayUtils.ApiException(httpResponse.code(), respBody, httpResponse.headers());
            }
        } catch (IOException e) {
            log.error("DefaultWechatPaymentService.executeJsonRequest 调用微信支付接口异常，uri={}", uri, e);
            throw new UncheckedIOException("Sending request to " + uri + " failed.", e);
        }
    }

    private void executeNoContentRequest(WechatPaymentConfig config, String host, String method, String uri,
                                         String reqBody) {
        Request.Builder reqBuilder = new Request.Builder().url(host + uri);
        reqBuilder.addHeader("Accept", "application/json");
        reqBuilder.addHeader("Wechatpay-Serial", config.getWechatPayPublicKeyId());
        reqBuilder.addHeader("Authorization",
                WechatPayUtils.buildAuthorization(config.getMchid(), config.getCertificateSerialNo(),
                        config.getPrivateKey(), method, uri, reqBody));
        reqBuilder.addHeader("Content-Type", "application/json");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqBody);
        reqBuilder.method(method, requestBody);
        Request httpRequest = reqBuilder.build();

        try (Response httpResponse = client.newCall(httpRequest).execute()) {
            if (httpResponse.code() >= 200 && httpResponse.code() < 300) {
                return;
            }
            String respBody = WechatPayUtils.extractBody(httpResponse);
            log.error("DefaultWechatPaymentService.executeNoContentRequest 请求微信支付接口失败，uri={}, code={}, respBody={}",
                    uri, httpResponse.code(), respBody);
            throw new WechatPayUtils.ApiException(httpResponse.code(), respBody, httpResponse.headers());
        } catch (IOException e) {
            log.error("DefaultWechatPaymentService.executeNoContentRequest 调用微信支付接口异常，uri={}", uri, e);
            throw new UncheckedIOException("Sending request to " + uri + " failed.", e);
        }
    }

    private WechatPaymentConfig getConfig() {
        WechatPaymentConfig config = provider.getConfig();
        if (config == null) {
            throw new IllegalStateException("未获取到微信支付配置");
        }
        return config;
    }

    private boolean isBlank(String value) {
        return value == null || value.isEmpty();
    }
}
