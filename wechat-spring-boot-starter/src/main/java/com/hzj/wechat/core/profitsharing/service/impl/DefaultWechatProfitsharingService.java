package com.hzj.wechat.core.profitsharing.service.impl;

import com.hzj.wechat.core.profitsharing.domain.DeleteProfitsharingReceiverRequest;
import com.hzj.wechat.core.profitsharing.domain.ProfitsharingAmountEntity;
import com.hzj.wechat.core.profitsharing.domain.ProfitsharingBillDownloadEntity;
import com.hzj.wechat.core.profitsharing.domain.ProfitsharingBillRequest;
import com.hzj.wechat.core.profitsharing.domain.ProfitsharingOrderEntity;
import com.hzj.wechat.core.profitsharing.domain.ProfitsharingOrderRequest;
import com.hzj.wechat.core.profitsharing.domain.ProfitsharingReceiverRequest;
import com.hzj.wechat.core.profitsharing.domain.ProfitsharingReturnOrderEntity;
import com.hzj.wechat.core.profitsharing.domain.ProfitsharingReturnOrderRequest;
import com.hzj.wechat.core.profitsharing.domain.QueryProfitsharingAmountRequest;
import com.hzj.wechat.core.profitsharing.domain.QueryProfitsharingOrderRequest;
import com.hzj.wechat.core.profitsharing.domain.QueryProfitsharingReturnOrderRequest;
import com.hzj.wechat.core.profitsharing.domain.UnfreezeProfitsharingOrderRequest;
import com.hzj.wechat.core.profitsharing.service.WechatProfitsharingService;
import com.hzj.wechat.core.enums.WechatHttpMethod;
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
public class DefaultWechatProfitsharingService implements WechatProfitsharingService {
    private final WechatPaymentConfigProvider provider;
    private final OkHttpClient client = new OkHttpClient.Builder().build();

    @Override
    public ProfitsharingOrderEntity createOrder(ProfitsharingOrderRequest request) {
        WechatPaymentConfig config = getConfig();

        if (isBlank(request.appid)) {
            request.appid = config.getAppid();
        }

        encryptReceiverNames(config, request);
        String reqBody = WechatPayUtils.toJson(request);
        return executeJsonRequest(config, request.requestHost, request.requestMethod, request.requestPath, reqBody, ProfitsharingOrderEntity.class);
    }

    @Override
    public ProfitsharingOrderEntity queryOrderByOutOrderNo(QueryProfitsharingOrderRequest request) {
        WechatPaymentConfig config = getConfig();

        String uri = request.requestPath.replace("{out_order_no}", WechatPayUtils.urlEncode(request.outOrderNo));
        Map<String, Object> args = new HashMap<>();
        args.put("transaction_id", request.transactionId);
        String queryString = WechatPayUtils.urlEncode(args);
        if (!queryString.isEmpty()) {
            uri = uri + "?" + queryString;
        }
        return executeJsonRequest(config, request.requestHost, request.requestMethod, uri, null, ProfitsharingOrderEntity.class);
    }

    @Override
    public ProfitsharingReturnOrderEntity createReturnOrder(ProfitsharingReturnOrderRequest request) {
        WechatPaymentConfig config = getConfig();

        if (isBlank(request.returnMchid)) {
            request.returnMchid = config.getMchid();
        }

        String reqBody = WechatPayUtils.toJson(request);
        return executeJsonRequest(config, request.requestHost, request.requestMethod, request.requestPath, reqBody, ProfitsharingReturnOrderEntity.class);
    }

    @Override
    public ProfitsharingReturnOrderEntity queryReturnOrderByOutReturnNo(QueryProfitsharingReturnOrderRequest request) {
        WechatPaymentConfig config = getConfig();

        String uri = request.requestPath.replace("{out_return_no}", WechatPayUtils.urlEncode(request.outReturnNo));
        return executeJsonRequest(config, request.requestHost, request.requestMethod, uri, null, ProfitsharingReturnOrderEntity.class);
    }

    @Override
    public ProfitsharingOrderEntity unfreezeRemainingFunds(UnfreezeProfitsharingOrderRequest request) {
        WechatPaymentConfig config = getConfig();

        String reqBody = WechatPayUtils.toJson(request);
        return executeJsonRequest(config, request.requestHost, request.requestMethod, request.requestPath, reqBody, ProfitsharingOrderEntity.class);
    }

    @Override
    public ProfitsharingAmountEntity queryRemainingAmount(QueryProfitsharingAmountRequest request) {
        WechatPaymentConfig config = getConfig();

        String uri = request.requestPath.replace("{transaction_id}", WechatPayUtils.urlEncode(request.transactionId));
        return executeJsonRequest(config, request.requestHost, request.requestMethod, uri, null, ProfitsharingAmountEntity.class);
    }

    @Override
    public void addReceiver(ProfitsharingReceiverRequest request) {
        WechatPaymentConfig config = getConfig();

        if (isBlank(request.appid)) {
            request.appid = config.getAppid();
        }
        if (!isBlank(request.name)) {
            request.name = WechatPayUtils.encrypt(config.getWechatPayPublicKey(), request.name);
        }

        String reqBody = WechatPayUtils.toJson(request);
        executeNoContentRequest(config, request.requestHost, request.requestMethod, request.requestPath, reqBody);
    }

    @Override
    public void deleteReceiver(DeleteProfitsharingReceiverRequest request) {
        WechatPaymentConfig config = getConfig();

        if (isBlank(request.appid)) {
            request.appid = config.getAppid();
        }

        String reqBody = WechatPayUtils.toJson(request);
        executeNoContentRequest(config, request.requestHost, request.requestMethod, request.requestPath, reqBody);
    }

    @Override
    public ProfitsharingBillDownloadEntity getBill(ProfitsharingBillRequest request) {
        WechatPaymentConfig config = getConfig();

        String uri = request.requestPath;
        Map<String, Object> args = new HashMap<>();
        args.put("bill_date", request.billDate);
        args.put("tar_type", request.tarType);
        String queryString = WechatPayUtils.urlEncode(args);
        if (!queryString.isEmpty()) {
            uri = uri + "?" + queryString;
        }
        return executeJsonRequest(config, request.requestHost, request.requestMethod, uri, null, ProfitsharingBillDownloadEntity.class);
    }

    private <T> T executeJsonRequest(WechatPaymentConfig config, String host, WechatHttpMethod requestMethod, String uri,
                                     String reqBody, Class<T> responseClass) {
        Request.Builder reqBuilder = new Request.Builder().url(host + uri);
        reqBuilder.addHeader("Accept", "application/json");
        reqBuilder.addHeader("Wechatpay-Serial", config.getWechatPayPublicKeyId());
        reqBuilder.addHeader("Authorization",
                WechatPayUtils.buildAuthorization(config.getMchid(), config.getCertificateSerialNo(),
                        config.getPrivateKey(), requestMethod.name(), uri, reqBody));
        if (reqBody != null) {
            reqBuilder.addHeader("Content-Type", "application/json");
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqBody);
            reqBuilder.method(requestMethod.name(), requestBody);
        } else {
            reqBuilder.method(requestMethod.name(), null);
        }
        Request httpRequest = reqBuilder.build();

        try (Response httpResponse = client.newCall(httpRequest).execute()) {
            String respBody = WechatPayUtils.extractBody(httpResponse);
            if (httpResponse.code() >= 200 && httpResponse.code() < 300) {
                WechatPayUtils.validateResponse(config.getWechatPayPublicKeyId(), config.getWechatPayPublicKey(),
                        httpResponse.headers(), respBody);
                return WechatPayUtils.fromJson(respBody, responseClass);
            }
            log.error("DefaultWechatProfitsharingService.executeJsonRequest 请求微信分账接口失败，uri={}, code={}, respBody={}",
                    uri, httpResponse.code(), respBody);
            throw new WechatPayUtils.ApiException(httpResponse.code(), respBody, httpResponse.headers());
        } catch (IOException e) {
            log.error("DefaultWechatProfitsharingService.executeJsonRequest 调用微信分账接口异常，uri={}", uri, e);
            throw new UncheckedIOException("Sending request to " + uri + " failed.", e);
        }
    }

    private void executeNoContentRequest(WechatPaymentConfig config, String host, WechatHttpMethod requestMethod, String uri,
                                         String reqBody) {
        Request.Builder reqBuilder = new Request.Builder().url(host + uri);
        reqBuilder.addHeader("Accept", "application/json");
        reqBuilder.addHeader("Wechatpay-Serial", config.getWechatPayPublicKeyId());
        reqBuilder.addHeader("Authorization",
                WechatPayUtils.buildAuthorization(config.getMchid(), config.getCertificateSerialNo(),
                        config.getPrivateKey(), requestMethod.name(), uri, reqBody));
        reqBuilder.addHeader("Content-Type", "application/json");
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), reqBody);
        reqBuilder.method(requestMethod.name(), requestBody);
        Request httpRequest = reqBuilder.build();

        try (Response httpResponse = client.newCall(httpRequest).execute()) {
            if (httpResponse.code() >= 200 && httpResponse.code() < 300) {
                return;
            }
            String respBody = WechatPayUtils.extractBody(httpResponse);
            log.error("DefaultWechatProfitsharingService.executeNoContentRequest 请求微信分账接口失败，uri={}, code={}, respBody={}",
                    uri, httpResponse.code(), respBody);
            throw new WechatPayUtils.ApiException(httpResponse.code(), respBody, httpResponse.headers());
        } catch (IOException e) {
            log.error("DefaultWechatProfitsharingService.executeNoContentRequest 调用微信分账接口异常，uri={}", uri, e);
            throw new UncheckedIOException("Sending request to " + uri + " failed.", e);
        }
    }

    private void encryptReceiverNames(WechatPaymentConfig config, ProfitsharingOrderRequest request) {
        if (request.receivers == null || request.receivers.isEmpty()) {
            return;
        }
        for (ProfitsharingOrderRequest.Receiver receiver : request.receivers) {
            if (!isBlank(receiver.name)) {
                receiver.name = WechatPayUtils.encrypt(config.getWechatPayPublicKey(), receiver.name);
            }
        }
    }

    private WechatPaymentConfig getConfig() {
        WechatPaymentConfig config = provider.getConfig();
        if (config == null) {
            throw new IllegalStateException("未获取到微信支付分账配置");
        }
        return config;
    }

    private boolean isBlank(String value) {
        return value == null || value.isEmpty();
    }
}
