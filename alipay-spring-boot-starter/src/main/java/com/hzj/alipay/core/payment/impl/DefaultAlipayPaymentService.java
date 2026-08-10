package com.hzj.alipay.core.payment.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.hzj.alipay.core.AbstractAlipayService;
import com.hzj.alipay.core.payment.AlipayPaymentService;
import com.hzj.alipay.core.payment.domain.AliPayRefundParam;
import com.hzj.alipay.core.payment.domain.AliPayRefundQueryParam;
import com.hzj.alipay.core.payment.domain.AliPayRefundQueryResult;
import com.hzj.alipay.core.payment.domain.AliPayRefundResult;
import com.hzj.alipay.core.payment.enums.AlipayPaymentType;
import com.hzj.alipay.core.payment.enums.AlipayRefundStatus;
import com.hzj.alipay.core.payment.enums.AlipayTradeStatus;
import com.hzj.alipay.core.payment.domain.AliPayPaymentParam;
import com.hzj.alipay.core.payment.domain.AliPayPaymentResult;
import com.hzj.alipay.core.payment.domain.AliPayTradeCloseParam;
import com.hzj.alipay.core.payment.domain.AliPayTradeCloseResult;
import com.hzj.alipay.core.payment.domain.AliPayTradeQueryParam;
import com.hzj.alipay.core.payment.domain.AliPayTradeQueryResult;
import com.hzj.alipay.core.AliPayException;
import com.hzj.alipay.provider.AlipayConfigProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DefaultAlipayPaymentService extends AbstractAlipayService implements AlipayPaymentService {
    private static final String APP_PRODUCT_CODE = "QUICK_MSECURITY_PAY";
    private static final String PAGE_PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";
    private static final String PRECREATE_PRODUCT_CODE = "FACE_TO_FACE_PAYMENT";
    private static final String WAP_PRODUCT_CODE = "QUICK_WAP_WAY";
    private static final String PAGE_EXECUTE_METHOD = "POST";
    private static final String DEFAULT_INTEGRATION_TYPE = "PCWEB";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AlipayConfigProvider provider;

    @Override
    protected AlipayClient getAlipayClient() {
        return createAlipayClient();
    }

    @Override
    protected AlipayConfigProvider getAlipayConfigProvider() {
        return provider;
    }

    /**
     * 统一收单线下交易预创建。
     *
     * @param paymentParam 支付参数
     * @return 统一支付结果
     */
    @Override
    public AliPayPaymentResult precreate(AliPayPaymentParam paymentParam) {
        validatePaymentParam(paymentParam);

        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();

        model.setOutTradeNo(paymentParam.getOutTradeNo());
        model.setTotalAmount(formatAmount(paymentParam.getTotalAmount()));
        model.setSubject(paymentParam.getSubject());
        model.setBody(paymentParam.getBody());
        model.setProductCode(resolveProductCode(paymentParam.getProductCode(), PRECREATE_PRODUCT_CODE));
        model.setTimeExpire(resolveTimeExpire(paymentParam));
        model.setTimeoutExpress(paymentParam.getTimeoutExpress());
        model.setSellerId(resolveSellerId(paymentParam.getSellerId()));
        model.setStoreId(paymentParam.getStoreId());
        model.setMerchantOrderNo(paymentParam.getMerchantOrderNo());
        model.setPassbackParams(paymentParam.getPassbackParams());
        model.setEnablePayChannels(paymentParam.getEnablePayChannels());
        model.setDisablePayChannels(paymentParam.getDisablePayChannels());
        model.setBuyerLogonId(paymentParam.getBuyerLogonId());
        model.setCodeType(paymentParam.getCodeType());
        model.setOperatorId(paymentParam.getOperatorId());
        model.setTerminalId(paymentParam.getTerminalId());
        model.setPaymentType(paymentParam.getPaymentType());
        model.setDiscountableAmount(formatNullableAmount(paymentParam.getDiscountableAmount()));
        model.setUndiscountableAmount(formatNullableAmount(paymentParam.getUndiscountableAmount()));
        model.setGoodsDetail(toList(paymentParam.getGoodsDetail()));
        model.setQueryOptions(toList(paymentParam.getQueryOptions()));
        model.setExtUserInfo(paymentParam.getExtUserInfo());
        model.setExtendParams(paymentParam.getExtendParams());
        model.setSubMerchant(paymentParam.getSubMerchant());
        model.setSettleInfo(paymentParam.getSettleInfo());
        model.setRoyaltyInfo(paymentParam.getRoyaltyInfo());

        request.setNotifyUrl(resolveNotifyUrl(paymentParam.getNotifyUrl()));
        request.setBizModel(model);

        try {
            AlipayTradePrecreateResponse response = execute(request);
            return AliPayPaymentResult.builder()
                    .success(response.isSuccess())
                    .paymentType(AlipayPaymentType.PRECREATE)
                    .apiMethod(AlipayPaymentType.PRECREATE.getApiMethod())
                    .code(response.getCode())
                    .msg(response.getMsg())
                    .subCode(response.getSubCode())
                    .subMsg(response.getSubMsg())
                    .outTradeNo(response.getOutTradeNo())
                    .sellerId(resolveSellerId(paymentParam.getSellerId()))
                    .totalAmount(formatAmount(paymentParam.getTotalAmount()))
                    .payData(response.getQrCode())
                    .qrCode(response.getQrCode())
                    .prepayId(response.getPrepayId())
                    .shareCode(response.getShareCode())
                    .build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayPaymentService.precreate 支付宝预创建订单失败, outTradeNo={}, errCode={}, errMsg={}",
                    paymentParam.getOutTradeNo(), e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.REQUEST_PAY_ERROR;
        }
    }

    /**
     * APP 支付。
     *
     * @param paymentParam 支付参数
     * @return 统一支付结果
     */
    @Override
    public AliPayPaymentResult appPay(AliPayPaymentParam paymentParam) {
        validatePaymentParam(paymentParam);

        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();

        model.setOutTradeNo(paymentParam.getOutTradeNo());
        model.setTotalAmount(formatAmount(paymentParam.getTotalAmount()));
        model.setSubject(paymentParam.getSubject());
        model.setBody(paymentParam.getBody());
        model.setProductCode(resolveProductCode(paymentParam.getProductCode(), APP_PRODUCT_CODE));
        model.setTimeExpire(resolveTimeExpire(paymentParam));
        model.setTimeoutExpress(paymentParam.getTimeoutExpress());
        model.setSellerId(resolveSellerId(paymentParam.getSellerId()));
        model.setStoreId(paymentParam.getStoreId());
        model.setMerchantOrderNo(paymentParam.getMerchantOrderNo());
        model.setPassbackParams(paymentParam.getPassbackParams());
        model.setBusinessParams(paymentParam.getBusinessParams());
        model.setPromoParams(paymentParam.getPromoParams());
        model.setGoodsType(paymentParam.getGoodsType());
        model.setEnablePayChannels(paymentParam.getEnablePayChannels());
        model.setDisablePayChannels(paymentParam.getDisablePayChannels());
        model.setSpecifiedChannel(paymentParam.getSpecifiedChannel());
        model.setGoodsDetail(toList(paymentParam.getGoodsDetail()));
        model.setQueryOptions(toList(paymentParam.getQueryOptions()));
        model.setExtUserInfo(paymentParam.getExtUserInfo());
        model.setExtendParams(paymentParam.getExtendParams());
        model.setSubMerchant(paymentParam.getSubMerchant());
        model.setSettleInfo(paymentParam.getSettleInfo());
        model.setRoyaltyInfo(paymentParam.getRoyaltyInfo());

        request.setNotifyUrl(resolveNotifyUrl(paymentParam.getNotifyUrl()));
        request.setReturnUrl(paymentParam.getReturnUrl());
        request.setBizModel(model);

        try {
            AlipayTradeAppPayResponse response = getAlipayClient().sdkExecute(request);
            return buildSdkOrPageResult(response, paymentParam, AlipayPaymentType.APP);
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayPaymentService.appPay 支付宝 APP 支付下单失败, outTradeNo={}, errCode={}, errMsg={}",
                    paymentParam.getOutTradeNo(), e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.REQUEST_PAY_ERROR;
        }
    }

    /**
     * 手机网站支付。
     *
     * @param paymentParam 支付参数
     * @return 统一支付结果
     */
    @Override
    public AliPayPaymentResult wapPay(AliPayPaymentParam paymentParam) {
        validatePaymentParam(paymentParam);

        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();

        model.setOutTradeNo(paymentParam.getOutTradeNo());
        model.setTotalAmount(formatAmount(paymentParam.getTotalAmount()));
        model.setSubject(paymentParam.getSubject());
        model.setBody(paymentParam.getBody());
        model.setProductCode(resolveProductCode(paymentParam.getProductCode(), WAP_PRODUCT_CODE));
        model.setTimeExpire(resolveTimeExpire(paymentParam));
        model.setTimeoutExpress(paymentParam.getTimeoutExpress());
        model.setSellerId(resolveSellerId(paymentParam.getSellerId()));
        model.setStoreId(paymentParam.getStoreId());
        model.setMerchantOrderNo(paymentParam.getMerchantOrderNo());
        model.setPassbackParams(paymentParam.getPassbackParams());
        model.setBusinessParams(paymentParam.getBusinessParams());
        model.setPromoParams(paymentParam.getPromoParams());
        model.setGoodsType(paymentParam.getGoodsType());
        model.setEnablePayChannels(paymentParam.getEnablePayChannels());
        model.setDisablePayChannels(paymentParam.getDisablePayChannels());
        model.setSpecifiedChannel(paymentParam.getSpecifiedChannel());
        model.setAuthToken(paymentParam.getAuthToken());
        model.setQuitUrl(paymentParam.getQuitUrl());
        model.setGoodsDetail(toList(paymentParam.getGoodsDetail()));
        model.setQueryOptions(toList(paymentParam.getQueryOptions()));
        model.setExtUserInfo(paymentParam.getExtUserInfo());
        model.setExtendParams(paymentParam.getExtendParams());
        model.setSubMerchant(paymentParam.getSubMerchant());
        model.setSettleInfo(paymentParam.getSettleInfo());
        model.setRoyaltyInfo(paymentParam.getRoyaltyInfo());

        request.setNotifyUrl(resolveNotifyUrl(paymentParam.getNotifyUrl()));
        request.setReturnUrl(paymentParam.getReturnUrl());
        request.setBizModel(model);

        try {
            AlipayTradeWapPayResponse response = getAlipayClient().pageExecute(request, PAGE_EXECUTE_METHOD);
            return buildSdkOrPageResult(response, paymentParam, AlipayPaymentType.WAP);
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayPaymentService.wapPay 支付宝 H5 支付下单失败, outTradeNo={}, errCode={}, errMsg={}",
                    paymentParam.getOutTradeNo(), e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.REQUEST_PAY_ERROR;
        }
    }

    /**
     * PC 端支付。
     *
     * @param paymentParam 支付参数
     * @return 统一支付结果
     */
    @Override
    public AliPayPaymentResult pagePay(AliPayPaymentParam paymentParam) {
        validatePaymentParam(paymentParam);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();

        model.setOutTradeNo(paymentParam.getOutTradeNo());
        model.setTotalAmount(formatAmount(paymentParam.getTotalAmount()));
        model.setSubject(paymentParam.getSubject());
        model.setBody(paymentParam.getBody());
        model.setProductCode(resolveProductCode(paymentParam.getProductCode(), PAGE_PRODUCT_CODE));
        model.setTimeExpire(resolveTimeExpire(paymentParam));
        model.setTimeoutExpress(paymentParam.getTimeoutExpress());
        model.setStoreId(paymentParam.getStoreId());
        model.setMerchantOrderNo(paymentParam.getMerchantOrderNo());
        model.setPassbackParams(paymentParam.getPassbackParams());
        model.setBusinessParams(paymentParam.getBusinessParams());
        model.setPromoParams(paymentParam.getPromoParams());
        model.setGoodsType(paymentParam.getGoodsType());
        model.setEnablePayChannels(paymentParam.getEnablePayChannels());
        model.setDisablePayChannels(paymentParam.getDisablePayChannels());
        model.setIntegrationType(resolveIntegrationType(paymentParam.getIntegrationType()));
        model.setQrPayMode(paymentParam.getQrPayMode() == null ? null : String.valueOf(paymentParam.getQrPayMode()));
        model.setQrcodeWidth(paymentParam.getQrcodeWidth());
        model.setRequestFromUrl(paymentParam.getRequestFromUrl());
        model.setGoodsDetail(toList(paymentParam.getGoodsDetail()));
        model.setQueryOptions(toList(paymentParam.getQueryOptions()));
        model.setExtUserInfo(paymentParam.getExtUserInfo());
        model.setExtendParams(paymentParam.getExtendParams());
        model.setSubMerchant(paymentParam.getSubMerchant());
        model.setSettleInfo(paymentParam.getSettleInfo());
        model.setRoyaltyInfo(paymentParam.getRoyaltyInfo());

        request.setNotifyUrl(resolveNotifyUrl(paymentParam.getNotifyUrl()));
        request.setReturnUrl(paymentParam.getReturnUrl());
        request.setBizModel(model);

        try {
            AlipayTradePagePayResponse response = getAlipayClient().pageExecute(request, PAGE_EXECUTE_METHOD);
            return buildSdkOrPageResult(response, paymentParam, AlipayPaymentType.PAGE);
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayPaymentService.pagePay 支付宝 PC 支付下单失败, outTradeNo={}, errCode={}, errMsg={}",
                    paymentParam.getOutTradeNo(), e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.REQUEST_PAY_ERROR;
        }
    }

    /**
     * 关闭交易。
     *
     * @param closeParam 关闭参数
     * @return 统一支付结果
     */
    @Override
    public AliPayTradeCloseResult close(AliPayTradeCloseParam closeParam) {
        validateTradeIdentity(closeParam == null ? null : closeParam.getOutTradeNo(),
                closeParam == null ? null : closeParam.getTradeNo());

        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        AlipayTradeCloseModel model = new AlipayTradeCloseModel();

        model.setOutTradeNo(closeParam.getOutTradeNo());
        model.setTradeNo(closeParam.getTradeNo());
        model.setOperatorId(closeParam.getOperatorId());
        request.setBizModel(model);

        try {
            AlipayTradeCloseResponse response = execute(request);
            return AliPayTradeCloseResult.builder()
                    .success(response.isSuccess())
                    .apiMethod(request.getApiMethodName())
                    .code(response.getCode())
                    .msg(response.getMsg())
                    .subCode(response.getSubCode())
                    .subMsg(response.getSubMsg())
                    .outTradeNo(response.getOutTradeNo())
                    .tradeNo(response.getTradeNo())
                    .build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayPaymentService.close 支付宝关闭交易失败, outTradeNo={}, tradeNo={}, errCode={}, errMsg={}",
                    closeParam.getOutTradeNo(), closeParam.getTradeNo(), e.getErrCode(), e.getErrMsg(), e);
            throw new AliPayException("关闭订单失败");
        }
    }

    /**
     * 交易退款。
     *
     * @param refundParam 退款参数
     * @return 统一支付结果
     */
    @Override
    public AliPayRefundResult refund(AliPayRefundParam refundParam) {
        validateRefundParam(refundParam);

        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();

        model.setOutTradeNo(refundParam.getOutTradeNo());
        model.setTradeNo(refundParam.getTradeNo());
        model.setRefundAmount(formatAmount(refundParam.getRefundAmount()));
        model.setRefundReason(refundParam.getRefundReason());
        model.setOutRequestNo(refundParam.getOutRequestNo());
        model.setOperatorId(refundParam.getOperatorId());
        model.setOrgPid(refundParam.getOrgPid());
        model.setStoreId(refundParam.getStoreId());
        model.setTerminalId(refundParam.getTerminalId());
        model.setQueryOptions(toList(refundParam.getQueryOptions()));
        model.setGoodsDetail(toList(refundParam.getGoodsDetail()));
        model.setRefundGoodsDetail(toList(refundParam.getRefundGoodsDetail()));
        model.setRefundRoyaltyParameters(toList(refundParam.getRefundRoyaltyParameters()));
        model.setRefundAdvanceAccount(refundParam.getRefundAdvanceAccount());
        model.setRefundAdvanceAccountType(refundParam.getRefundAdvanceAccountType());
        model.setRefundTransOut(refundParam.getRefundTransOut());
        model.setRefundTransOutType(refundParam.getRefundTransOutType());
        model.setRelatedSettleConfirmNo(refundParam.getRelatedSettleConfirmNo());
        model.setRefundCurrency(refundParam.getRefundCurrency());
        request.setBizModel(model);

        try {
            AlipayTradeRefundResponse response = execute(request);
            return buildRefundResult(response, refundParam, request.getApiMethodName());
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayPaymentService.refund 支付宝交易退款失败, outTradeNo={}, tradeNo={}, outRequestNo={}, errCode={}, errMsg={}",
                    refundParam.getOutTradeNo(), refundParam.getTradeNo(), refundParam.getOutRequestNo(),
                    e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.REFUND_ERROR;
        }
    }

    /**
     * 退款查询。
     *
     * @param refundQueryParam 退款查询参数
     * @return 统一支付结果
     */
    @Override
    public AliPayRefundQueryResult queryRefund(AliPayRefundQueryParam refundQueryParam) {
        validateTradeIdentity(refundQueryParam == null ? null : refundQueryParam.getOutTradeNo(),
                refundQueryParam == null ? null : refundQueryParam.getTradeNo());

        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();

        model.setOutTradeNo(refundQueryParam.getOutTradeNo());
        model.setTradeNo(refundQueryParam.getTradeNo());
        model.setOutRequestNo(refundQueryParam.getOutRequestNo());
        model.setOrgPid(refundQueryParam.getOrgPid());
        model.setQueryOptions(toList(refundQueryParam.getQueryOptions()));
        request.setBizModel(model);

        try {
            AlipayTradeFastpayRefundQueryResponse response = execute(request);
            return buildRefundQueryResult(response, request.getApiMethodName());
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayPaymentService.queryRefund 支付宝退款查询失败, outTradeNo={}, tradeNo={}, outRequestNo={}, errCode={}, errMsg={}",
                    refundQueryParam.getOutTradeNo(), refundQueryParam.getTradeNo(), refundQueryParam.getOutRequestNo(),
                    e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.QUERY_REFUND_ERROR;
        }
    }

    /**
     * 交易查询。
     *
     * @param queryParam 交易查询参数
     * @return 统一支付结果
     */
    @Override
    public AliPayTradeQueryResult query(AliPayTradeQueryParam queryParam) {
        validateTradeIdentity(queryParam == null ? null : queryParam.getOutTradeNo(),
                queryParam == null ? null : queryParam.getTradeNo());

        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();

        model.setOutTradeNo(queryParam.getOutTradeNo());
        model.setTradeNo(queryParam.getTradeNo());
        model.setOrgPid(queryParam.getOrgPid());
        model.setQueryOptions(toList(queryParam.getQueryOptions()));
        request.setBizModel(model);

        try {
            AlipayTradeQueryResponse response = execute(request);
            return buildTradeQueryResult(response, request.getApiMethodName());
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayPaymentService.query 支付宝交易查询失败, outTradeNo={}, tradeNo={}, errCode={}, errMsg={}",
                    queryParam.getOutTradeNo(), queryParam.getTradeNo(), e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.QUERY_ERROR;
        }
    }

    private AliPayPaymentResult buildSdkOrPageResult(AlipayResponse response, AliPayPaymentParam paymentParam,
                                                     AlipayPaymentType paymentType) {
        return AliPayPaymentResult.builder()
                .success(response.isSuccess())
                .paymentType(paymentType)
                .apiMethod(paymentType.getApiMethod())
                .code(response.getCode())
                .msg(response.getMsg())
                .subCode(response.getSubCode())
                .subMsg(response.getSubMsg())
                .outTradeNo(paymentParam.getOutTradeNo())
                .sellerId(resolveSellerId(paymentParam.getSellerId()))
                .totalAmount(formatAmount(paymentParam.getTotalAmount()))
                .payData(response.getBody())
                .build();
    }

    private AliPayTradeQueryResult buildTradeQueryResult(AlipayTradeQueryResponse response, String apiMethod) {
        return AliPayTradeQueryResult.builder()
                .success(response.isSuccess())
                .apiMethod(apiMethod)
                .code(response.getCode())
                .msg(response.getMsg())
                .subCode(response.getSubCode())
                .subMsg(response.getSubMsg())
                .outTradeNo(response.getOutTradeNo())
                .tradeNo(response.getTradeNo())
                .tradeStatus(AlipayTradeStatus.fromStatus(response.getTradeStatus()))
                .tradeStatusCode(response.getTradeStatus())
                .buyerLogonId(response.getBuyerLogonId())
                .buyerUserId(response.getBuyerUserId())
                .buyerPayAmount(response.getBuyerPayAmount())
                .receiptAmount(response.getReceiptAmount())
                .invoiceAmount(response.getInvoiceAmount())
                .sendPayDate(response.getSendPayDate())
                .totalAmount(response.getTotalAmount())
                .build();
    }

    private AliPayRefundResult buildRefundResult(AlipayTradeRefundResponse response, AliPayRefundParam refundParam,
                                                 String apiMethod) {
        return AliPayRefundResult.builder()
                .success(response.isSuccess())
                .apiMethod(apiMethod)
                .code(response.getCode())
                .msg(response.getMsg())
                .subCode(response.getSubCode())
                .subMsg(response.getSubMsg())
                .outTradeNo(response.getOutTradeNo())
                .tradeNo(response.getTradeNo())
                .outRequestNo(refundParam.getOutRequestNo())
                .buyerLogonId(response.getBuyerLogonId())
                .buyerUserId(response.getBuyerUserId())
                .refundFee(response.getRefundFee())
                .refundAmount(firstNonBlank(response.getRefundFee(), formatNullableAmount(refundParam.getRefundAmount())))
                .refundReason(refundParam.getRefundReason())
                .fundChange(response.getFundChange())
                .gmtRefundPay(response.getGmtRefundPay())
                .build();
    }

    private AliPayRefundQueryResult buildRefundQueryResult(AlipayTradeFastpayRefundQueryResponse response,
                                                           String apiMethod) {
        return AliPayRefundQueryResult.builder()
                .success(response.isSuccess())
                .apiMethod(apiMethod)
                .code(response.getCode())
                .msg(response.getMsg())
                .subCode(response.getSubCode())
                .subMsg(response.getSubMsg())
                .outTradeNo(response.getOutTradeNo())
                .tradeNo(response.getTradeNo())
                .outRequestNo(response.getOutRequestNo())
                .refundStatus(AlipayRefundStatus.fromStatus(response.getRefundStatus()))
                .refundStatusCode(response.getRefundStatus())
                .refundAmount(response.getRefundAmount())
                .sendBackFee(response.getSendBackFee())
                .refundReason(response.getRefundReason())
                .gmtRefundPay(response.getGmtRefundPay())
                .totalAmount(response.getTotalAmount())
                .build();
    }

    private void validatePaymentParam(AliPayPaymentParam paymentParam) {
        if (paymentParam == null) {
            throw new IllegalArgumentException("支付参数不能为空");
        }
        if (!StringUtils.hasText(paymentParam.getOutTradeNo())) {
            throw new IllegalArgumentException("商户订单号不能为空");
        }
        if (paymentParam.getTotalAmount() == null || paymentParam.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("订单总金额必须大于0");
        }
        if (!StringUtils.hasText(paymentParam.getSubject())) {
            throw new IllegalArgumentException("订单标题不能为空");
        }
    }

    private void validateRefundParam(AliPayRefundParam refundParam) {
        if (refundParam == null) {
            throw new IllegalArgumentException("退款参数不能为空");
        }
        validateTradeIdentity(refundParam.getOutTradeNo(), refundParam.getTradeNo());
        if (refundParam.getRefundAmount() == null || refundParam.getRefundAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("退款金额必须大于0");
        }
    }

    private void validateTradeIdentity(String outTradeNo, String tradeNo) {
        if (!StringUtils.hasText(outTradeNo) && !StringUtils.hasText(tradeNo)) {
            throw new IllegalArgumentException("商户订单号和支付宝交易号不能同时为空");
        }
    }

    private String resolveProductCode(String productCode, String defaultProductCode) {
        return StringUtils.hasText(productCode) ? productCode : defaultProductCode;
    }

    private String resolveSellerId(String sellerId) {
        if (StringUtils.hasText(sellerId)) {
            return sellerId;
        }
        return getCurrentConfig().getSellerId();
    }

    /**
     * 解析异步通知地址。
     * 优先使用支付参数中的 notifyUrl，未传时回退到全局配置 paymentNotifyUrl。
     *
     * @param notifyUrl 支付参数中的异步通知地址
     * @return 最终异步通知地址
     */
    private String resolveNotifyUrl(String notifyUrl) {
        if (StringUtils.hasText(notifyUrl)) {
            return notifyUrl;
        }
        return getCurrentConfig().getPaymentNotifyUrl();
    }

    private String resolveIntegrationType(String integrationType) {
        return StringUtils.hasText(integrationType) ? integrationType : DEFAULT_INTEGRATION_TYPE;
    }

    private String resolveTimeExpire(AliPayPaymentParam paymentParam) {
        if (StringUtils.hasText(paymentParam.getTimeExpire())) {
            return paymentParam.getTimeExpire();
        }
        Long validityTime = getCurrentConfig().getValidityTime();
        if (validityTime == null || validityTime <= 0) {
            return null;
        }
        return LocalDateTime.now().plus(validityTime, ChronoUnit.MILLIS)
                .format(TIME_FORMATTER);
    }

    private String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String formatNullableAmount(BigDecimal amount) {
        return amount == null ? null : formatAmount(amount);
    }

    private String firstNonBlank(String first, String second) {
        return StringUtils.hasText(first) ? first : second;
    }

    private <T> List<T> toList(T[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        return Arrays.asList(array);
    }
}
