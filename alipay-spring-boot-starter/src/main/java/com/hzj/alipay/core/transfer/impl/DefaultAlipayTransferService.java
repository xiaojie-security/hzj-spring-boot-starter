package com.hzj.alipay.core.transfer.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayDataBillEreceiptApplyModel;
import com.alipay.api.domain.AlipayDataBillEreceiptQueryModel;
import com.alipay.api.domain.AlipayDataDataserviceBillDownloadurlQueryModel;
import com.alipay.api.domain.AlipayFundAccountQueryModel;
import com.alipay.api.domain.AlipayFundQuotaQueryModel;
import com.alipay.api.domain.AlipayFundTransCommonQueryModel;
import com.alipay.api.domain.AlipayFundTransUniTransferModel;
import com.alipay.api.domain.BankcardExtInfo;
import com.alipay.api.domain.BalanceAccountDetail;
import com.alipay.api.domain.ExtCardInfo;
import com.alipay.api.domain.MutipleCurrencyDetail;
import com.alipay.api.domain.Participant;
import com.alipay.api.domain.SignData;
import com.alipay.api.domain.TransferSceneReportInfo;
import com.alipay.api.request.AlipayDataBillEreceiptApplyRequest;
import com.alipay.api.request.AlipayDataBillEreceiptQueryRequest;
import com.alipay.api.request.AlipayDataDataserviceBillDownloadurlQueryRequest;
import com.alipay.api.request.AlipayFundAccountQueryRequest;
import com.alipay.api.request.AlipayFundQuotaQueryRequest;
import com.alipay.api.request.AlipayFundTransCommonQueryRequest;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.response.AlipayDataBillEreceiptApplyResponse;
import com.alipay.api.response.AlipayDataBillEreceiptQueryResponse;
import com.alipay.api.response.AlipayDataDataserviceBillDownloadurlQueryResponse;
import com.alipay.api.response.AlipayFundAccountQueryResponse;
import com.alipay.api.response.AlipayFundQuotaQueryResponse;
import com.alipay.api.response.AlipayFundTransCommonQueryResponse;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.hzj.alipay.core.AbstractAlipayService;
import com.hzj.alipay.core.transfer.AlipayTransferService;
import com.hzj.alipay.core.transfer.domain.AliPayBalanceAccountDetail;
import com.hzj.alipay.core.transfer.domain.AliPayBillDownloadUrlQueryParam;
import com.hzj.alipay.core.transfer.domain.AliPayBillDownloadUrlResult;
import com.hzj.alipay.core.transfer.domain.AliPayBankcardExtInfo;
import com.hzj.alipay.core.transfer.domain.AliPayEreceiptApplyParam;
import com.hzj.alipay.core.transfer.domain.AliPayEreceiptApplyResult;
import com.hzj.alipay.core.transfer.domain.AliPayEreceiptQueryParam;
import com.hzj.alipay.core.transfer.domain.AliPayEreceiptQueryResult;
import com.hzj.alipay.core.transfer.domain.AliPayExtCardInfo;
import com.hzj.alipay.core.transfer.domain.AliPayFundAccountQueryParam;
import com.hzj.alipay.core.transfer.domain.AliPayFundAccountQueryResult;
import com.hzj.alipay.core.transfer.domain.AliPayFundQuotaQueryParam;
import com.hzj.alipay.core.transfer.domain.AliPayFundQuotaQueryResult;
import com.hzj.alipay.core.transfer.domain.AliPayTransferMultiCurrencyDetail;
import com.hzj.alipay.core.transfer.domain.AliPayTransferParam;
import com.hzj.alipay.core.transfer.domain.AliPayTransferParticipant;
import com.hzj.alipay.core.transfer.domain.AliPayTransferQueryParam;
import com.hzj.alipay.core.transfer.domain.AliPayTransferQueryResult;
import com.hzj.alipay.core.transfer.domain.AliPayTransferResult;
import com.hzj.alipay.core.transfer.domain.AliPayTransferSceneReportInfo;
import com.hzj.alipay.core.transfer.domain.AliPayTransferSignData;
import com.hzj.alipay.core.transfer.enums.AlipayEreceiptStatus;
import com.hzj.alipay.core.transfer.enums.AlipayFundAccountType;
import com.hzj.alipay.core.transfer.enums.AlipayFundTransferBizScene;
import com.hzj.alipay.core.transfer.enums.AlipayFundTransferProductCode;
import com.hzj.alipay.core.transfer.enums.AlipayTransferParticipantIdentityType;
import com.hzj.alipay.core.transfer.enums.AlipayTransferStatus;
import com.hzj.alipay.core.AliPayException;
import com.hzj.alipay.provider.AlipayConfigProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DefaultAlipayTransferService extends AbstractAlipayService implements AlipayTransferService {
    private static final String DEFAULT_TRANSFER_BIZ_SCENE = AlipayFundTransferBizScene.DIRECT_TRANSFER.getCode();
    private static final String DEFAULT_TRANSFER_PRODUCT_CODE = AlipayFundTransferProductCode.TRANS_ACCOUNT_NO_PWD.getCode();
    private static final String DEFAULT_ACCOUNT_TYPE = AlipayFundAccountType.ACCTRANS_ACCOUNT.getCode();
    private static final String DEFAULT_PAYEE_IDENTITY_TYPE = AlipayTransferParticipantIdentityType.ALIPAY_OPEN_ID.getCode();

    private final AlipayConfigProvider provider;


    /**
     * 获取当前服务使用的支付宝客户端。
     *
     * @return 支付宝客户端
     */
    @Override
    protected AlipayClient getAlipayClient() {
        return createAlipayClient();
    }

    @Override
    protected AlipayConfigProvider getAlipayConfigProvider() {
        return provider;
    }

    @Override
    public AliPayFundAccountQueryResult accountQuery(AliPayFundAccountQueryParam queryParam) {
        validateAccountQueryParam(queryParam);

        AlipayFundAccountQueryRequest request = new AlipayFundAccountQueryRequest();
        AlipayFundAccountQueryModel model = new AlipayFundAccountQueryModel();

        model.setAccountProductCode(queryParam.getAccountProductCode());
        model.setAccountSceneCode(queryParam.getAccountSceneCode());
        model.setAccountType(resolveAccountType(queryParam.getAccountType()));
        model.setAlipayOpenId(queryParam.getAlipayOpenId());
        model.setAlipayUserId(queryParam.getAlipayUserId());
        model.setExtInfo(queryParam.getExtInfo());
        model.setMerchantUserId(queryParam.getMerchantUserId());
        request.setBizModel(model);

        try {
            AlipayFundAccountQueryResponse response = execute(request);
            return AliPayFundAccountQueryResult.builder()
                    .success(response.isSuccess())
                    .apiMethod(request.getApiMethodName())
                    .code(response.getCode())
                    .msg(response.getMsg())
                    .subCode(response.getSubCode())
                    .subMsg(response.getSubMsg())
                    .totalAmount(response.getTotalAmount())
                    .availableAmount(response.getAvailableAmount())
                    .freezeAmount(response.getFreezeAmount())
                    .amountDetail(buildAmountDetail(response.getAmountDetail()))
                    .extCardInfo(buildExtCardInfo(response.getExtCardInfo()))
                    .build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayTransferService.accountQuery 支付宝资金账户查询失败, accountType={}, alipayUserId={}, alipayOpenId={}, errCode={}, errMsg={}",
                    queryParam.getAccountType(), queryParam.getAlipayUserId(), queryParam.getAlipayOpenId(),
                    e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.TRANSFER_ERROR;
        }
    }

    @Override
    public AliPayFundQuotaQueryResult quotaQuery(AliPayFundQuotaQueryParam queryParam) {
        validateQuotaQueryParam(queryParam);

        AlipayFundQuotaQueryRequest request = new AlipayFundQuotaQueryRequest();
        AlipayFundQuotaQueryModel model = new AlipayFundQuotaQueryModel();

        model.setBizScene(resolveTransferBizScene(queryParam.getBizScene()));
        model.setProductCode(resolveTransferProductCode(queryParam.getProductCode()));
        request.setBizModel(model);

        try {
            AlipayFundQuotaQueryResponse response = execute(request);
            return AliPayFundQuotaQueryResult.builder()
                    .success(response.isSuccess())
                    .apiMethod(request.getApiMethodName())
                    .code(response.getCode())
                    .msg(response.getMsg())
                    .subCode(response.getSubCode())
                    .subMsg(response.getSubMsg())
                    .activeNewQuotaDailyRemainLimited(response.getActiveNewQuotaDailyRemainLimited())
                    .activeNewQuotaDailyRemainLimitType(response.getActiveNewQuotaDailyRemainLimitType())
                    .activeNewQuotaMonthlyRemainLimited(response.getActiveNewQuotaMonthlyRemainLimited())
                    .activeNewQuotaMonthlyRemainLimitType(response.getActiveNewQuotaMonthlyRemainLimitType())
                    .activeQuotaIsNew(response.getActiveQuotaIsNew())
                    .newQuotaDaily(response.getNewQuotaDaily())
                    .newQuotaDailyRemain(response.getNewQuotaDailyRemain())
                    .newQuotaMonthly(response.getNewQuotaMonthly())
                    .newQuotaMonthlyRemain(response.getNewQuotaMonthlyRemain())
                    .newQuotaSingleMax(response.getNewQuotaSingleMax())
                    .newQuotaSingleMin(response.getNewQuotaSingleMin())
                    .toCorporateDailyAvailableAmount(response.getToCorporateDailyAvailableAmount())
                    .toCorporateMonthlyAvailableAmount(response.getToCorporateMonthlyAvailableAmount())
                    .toPrivateDailyAvailableAmount(response.getToPrivateDailyAvailableAmount())
                    .toPrivateMonthlyAvailableAmount(response.getToPrivateMonthlyAvailableAmount())
                    .build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayTransferService.quotaQuery 支付宝转账额度查询失败, bizScene={}, productCode={}, errCode={}, errMsg={}",
                    queryParam.getBizScene(), queryParam.getProductCode(), e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.TRANSFER_ERROR;
        }
    }

    @Override
    public AliPayEreceiptApplyResult applyEreceipt(AliPayEreceiptApplyParam applyParam) {
        validateEreceiptApplyParam(applyParam);

        AlipayDataBillEreceiptApplyRequest request = new AlipayDataBillEreceiptApplyRequest();
        AlipayDataBillEreceiptApplyModel model = new AlipayDataBillEreceiptApplyModel();

        model.setBillUserId(applyParam.getBillUserId());
        model.setKey(applyParam.getKey());
        model.setType(applyParam.getType());
        request.setBizModel(model);

        try {
            AlipayDataBillEreceiptApplyResponse response = execute(request);
            return AliPayEreceiptApplyResult.builder()
                    .success(response.isSuccess())
                    .apiMethod(request.getApiMethodName())
                    .code(response.getCode())
                    .msg(response.getMsg())
                    .subCode(response.getSubCode())
                    .subMsg(response.getSubMsg())
                    .fileId(response.getFileId())
                    .build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayTransferService.applyEreceipt 支付宝电子回单申请失败, billUserId={}, type={}, key={}, errCode={}, errMsg={}",
                    applyParam.getBillUserId(), applyParam.getType(), applyParam.getKey(), e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.TRANSFER_ERROR;
        }
    }

    @Override
    public AliPayEreceiptQueryResult queryEreceipt(AliPayEreceiptQueryParam queryParam) {
        validateEreceiptQueryParam(queryParam);

        AlipayDataBillEreceiptQueryRequest request = new AlipayDataBillEreceiptQueryRequest();
        AlipayDataBillEreceiptQueryModel model = new AlipayDataBillEreceiptQueryModel();

        model.setFileId(queryParam.getFileId());
        request.setBizModel(model);

        try {
            AlipayDataBillEreceiptQueryResponse response = execute(request);
            return AliPayEreceiptQueryResult.builder()
                    .success(response.isSuccess())
                    .apiMethod(request.getApiMethodName())
                    .code(response.getCode())
                    .msg(response.getMsg())
                    .subCode(response.getSubCode())
                    .subMsg(response.getSubMsg())
                    .fileId(queryParam.getFileId())
                    .status(AlipayEreceiptStatus.fromCode(response.getStatus()))
                    .statusCode(response.getStatus())
                    .downloadUrl(response.getDownloadUrl())
                    .errorMessage(response.getErrorMessage())
                    .build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayTransferService.queryEreceipt 支付宝电子回单状态查询失败, fileId={}, errCode={}, errMsg={}",
                    queryParam.getFileId(), e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.TRANSFER_ERROR;
        }
    }

    @Override
    public AliPayTransferResult transfer(AliPayTransferParam transferParam) {
        validateTransferParam(transferParam);

        AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
        AlipayFundTransUniTransferModel model = new AlipayFundTransUniTransferModel();

        model.setBizScene(resolveTransferBizScene(transferParam.getBizScene()));
        model.setBusinessParams(transferParam.getBusinessParams());
        model.setMutipleCurrencyDetail(buildMultiCurrencyDetail(transferParam.getMutipleCurrencyDetail()));
        model.setOrderTitle(transferParam.getOrderTitle());
        model.setOriginalOrderId(transferParam.getOriginalOrderId());
        model.setOutBizNo(transferParam.getOutBizNo());
        model.setPassbackParams(transferParam.getPassbackParams());
        model.setPayeeInfo(buildParticipant(transferParam.getPayeeInfo(), true));
        model.setPayerInfo(buildParticipant(transferParam.getPayerInfo(), false));
        model.setProductCode(resolveTransferProductCode(transferParam.getProductCode()));
        model.setRemark(transferParam.getRemark());
        model.setSignData(buildSignData(transferParam.getSignData()));
        model.setTransAmount(formatAmount(transferParam.getTransAmount()));
        model.setTransferSceneName(transferParam.getTransferSceneName());
        model.setTransferSceneReportInfos(toList(buildTransferSceneReportInfos(transferParam.getTransferSceneReportInfos())));
        request.setBizModel(model);

        try {
            AlipayFundTransUniTransferResponse response = execute(request);
            return AliPayTransferResult.builder()
                    .success(response.isSuccess())
                    .apiMethod(request.getApiMethodName())
                    .code(response.getCode())
                    .msg(response.getMsg())
                    .subCode(response.getSubCode())
                    .subMsg(response.getSubMsg())
                    .amount(response.getAmount())
                    .link(response.getLink())
                    .orderId(response.getOrderId())
                    .outBizNo(response.getOutBizNo())
                    .payFundOrderId(response.getPayFundOrderId())
                    .settleSerialNo(response.getSettleSerialNo())
                    .status(AlipayTransferStatus.fromCode(response.getStatus()))
                    .statusCode(response.getStatus())
                    .subStatus(response.getSubStatus())
                    .transDate(response.getTransDate())
                    .build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayTransferService.transfer 支付宝单笔转账失败, outBizNo={}, transAmount={}, errCode={}, errMsg={}",
                    transferParam.getOutBizNo(), transferParam.getTransAmount(), e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.TRANSFER_ERROR;
        }
    }

    @Override
    public AliPayTransferQueryResult query(AliPayTransferQueryParam queryParam) {
        validateTransferQueryParam(queryParam);

        AlipayFundTransCommonQueryRequest request = new AlipayFundTransCommonQueryRequest();
        AlipayFundTransCommonQueryModel model = new AlipayFundTransCommonQueryModel();

        model.setBizScene(resolveQueryBizScene(queryParam));
        model.setOrderId(queryParam.getOrderId());
        model.setOutBizNo(queryParam.getOutBizNo());
        model.setPayFundOrderId(queryParam.getPayFundOrderId());
        model.setProductCode(resolveQueryProductCode(queryParam));
        request.setBizModel(model);

        try {
            AlipayFundTransCommonQueryResponse response = execute(request);
            return AliPayTransferQueryResult.builder()
                    .success(response.isSuccess())
                    .apiMethod(request.getApiMethodName())
                    .code(response.getCode())
                    .msg(response.getMsg())
                    .subCode(response.getSubCode())
                    .subMsg(response.getSubMsg())
                    .arrivalTimeEnd(response.getArrivalTimeEnd())
                    .deductBillInfo(response.getDeductBillInfo())
                    .errorCode(response.getErrorCode())
                    .failInstErrorCode(response.getFailInstErrorCode())
                    .failInstName(response.getFailInstName())
                    .failInstReason(response.getFailInstReason())
                    .failReason(response.getFailReason())
                    .inflowSettleSerialNo(response.getInflowSettleSerialNo())
                    .orderFee(response.getOrderFee())
                    .orderId(response.getOrderId())
                    .outBizNo(response.getOutBizNo())
                    .passbackParams(response.getPassbackParams())
                    .payDate(response.getPayDate())
                    .payFundOrderId(response.getPayFundOrderId())
                    .receiverOpenId(response.getReceiverOpenId())
                    .receiverUserId(response.getReceiverUserId())
                    .settleSerialNo(response.getSettleSerialNo())
                    .status(AlipayTransferStatus.fromCode(response.getStatus()))
                    .statusCode(response.getStatus())
                    .subOrderErrorCode(response.getSubOrderErrorCode())
                    .subOrderFailReason(response.getSubOrderFailReason())
                    .subOrderStatus(response.getSubOrderStatus())
                    .subStatus(response.getSubStatus())
                    .transAmount(response.getTransAmount())
                    .transferBillInfo(response.getTransferBillInfo())
                    .build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayTransferService.query 支付宝转账单据查询失败, outBizNo={}, orderId={}, payFundOrderId={}, errCode={}, errMsg={}",
                    queryParam.getOutBizNo(), queryParam.getOrderId(), queryParam.getPayFundOrderId(),
                    e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.TRANSFER_ERROR;
        }
    }

    @Override
    public AliPayBillDownloadUrlResult queryBillDownloadUrl(AliPayBillDownloadUrlQueryParam queryParam) {
        validateBillDownloadUrlQueryParam(queryParam);

        AlipayDataDataserviceBillDownloadurlQueryRequest request = new AlipayDataDataserviceBillDownloadurlQueryRequest();
        AlipayDataDataserviceBillDownloadurlQueryModel model = new AlipayDataDataserviceBillDownloadurlQueryModel();

        model.setBillDate(queryParam.getBillDate());
        model.setBillType(queryParam.getBillType());
        model.setSmid(queryParam.getSmid());
        request.setBizModel(model);

        try {
            AlipayDataDataserviceBillDownloadurlQueryResponse response = execute(request);
            return AliPayBillDownloadUrlResult.builder()
                    .success(response.isSuccess())
                    .apiMethod(request.getApiMethodName())
                    .code(response.getCode())
                    .msg(response.getMsg())
                    .subCode(response.getSubCode())
                    .subMsg(response.getSubMsg())
                    .billDownloadUrl(response.getBillDownloadUrl())
                    .billFileCode(response.getBillFileCode())
                    .build();
        } catch (AlipayApiException e) {
            log.error("DefaultAlipayTransferService.queryBillDownloadUrl 支付宝账单下载地址查询失败, billDate={}, billType={}, smid={}, errCode={}, errMsg={}",
                    queryParam.getBillDate(), queryParam.getBillType(), queryParam.getSmid(), e.getErrCode(), e.getErrMsg(), e);
            throw AliPayException.TRANSFER_ERROR;
        }
    }

    private void validateAccountQueryParam(AliPayFundAccountQueryParam queryParam) {
        if (queryParam == null) {
            throw new IllegalArgumentException("资金账户查询参数不能为空");
        }
        if (!StringUtils.hasText(queryParam.getAccountType())) {
            queryParam.setAccountType(DEFAULT_ACCOUNT_TYPE);
        }
    }

    private void validateQuotaQueryParam(AliPayFundQuotaQueryParam queryParam) {
        if (queryParam == null) {
            throw new IllegalArgumentException("转账额度查询参数不能为空");
        }
    }

    private void validateEreceiptApplyParam(AliPayEreceiptApplyParam applyParam) {
        if (applyParam == null) {
            throw new IllegalArgumentException("电子回单申请参数不能为空");
        }
        if (!StringUtils.hasText(applyParam.getBillUserId())) {
            throw new IllegalArgumentException("账单用户标识不能为空");
        }
        if (!StringUtils.hasText(applyParam.getKey())) {
            throw new IllegalArgumentException("电子回单业务键不能为空");
        }
        if (!StringUtils.hasText(applyParam.getType())) {
            throw new IllegalArgumentException("电子回单类型不能为空");
        }
    }

    private void validateEreceiptQueryParam(AliPayEreceiptQueryParam queryParam) {
        if (queryParam == null) {
            throw new IllegalArgumentException("电子回单状态查询参数不能为空");
        }
        if (!StringUtils.hasText(queryParam.getFileId())) {
            throw new IllegalArgumentException("电子回单申请 ID 不能为空");
        }
    }

    private void validateTransferParam(AliPayTransferParam transferParam) {
        if (transferParam == null) {
            throw new IllegalArgumentException("转账参数不能为空");
        }
        if (!StringUtils.hasText(transferParam.getOutBizNo())) {
            throw new IllegalArgumentException("商户转账单号不能为空");
        }
        if (transferParam.getTransAmount() == null || transferParam.getTransAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("转账金额必须大于0");
        }
        if (!StringUtils.hasText(transferParam.getOrderTitle())) {
            throw new IllegalArgumentException("转账标题不能为空");
        }
        if (transferParam.getPayeeInfo() == null) {
            throw new IllegalArgumentException("收款方信息不能为空");
        }
        validateParticipant(transferParam.getPayeeInfo(), true);
        validateParticipant(transferParam.getPayerInfo(), false);
    }

    private void validateTransferQueryParam(AliPayTransferQueryParam queryParam) {
        if (queryParam == null) {
            throw new IllegalArgumentException("转账查询参数不能为空");
        }
        if (!StringUtils.hasText(queryParam.getOutBizNo())
                && !StringUtils.hasText(queryParam.getOrderId())
                && !StringUtils.hasText(queryParam.getPayFundOrderId())) {
            throw new IllegalArgumentException("商户转账单号、支付宝转账单据号和支付宝支付资金流水号不能同时为空");
        }
    }

    private void validateBillDownloadUrlQueryParam(AliPayBillDownloadUrlQueryParam queryParam) {
        if (queryParam == null) {
            throw new IllegalArgumentException("账单下载地址查询参数不能为空");
        }
        if (!StringUtils.hasText(queryParam.getBillDate())) {
            throw new IllegalArgumentException("账单时间不能为空");
        }
        if (!StringUtils.hasText(queryParam.getBillType())) {
            throw new IllegalArgumentException("账单类型不能为空");
        }
    }

    private void validateParticipant(AliPayTransferParticipant participant, boolean payee) {
        if (participant == null) {
            return;
        }
        if (!StringUtils.hasText(participant.getIdentity())) {
            throw new IllegalArgumentException(payee ? "收款方标识不能为空" : "付款方标识不能为空");
        }
        if (!StringUtils.hasText(participant.getIdentityType())) {
            if (payee) {
                participant.setIdentityType(DEFAULT_PAYEE_IDENTITY_TYPE);
            } else {
                throw new IllegalArgumentException("付款方标识类型不能为空");
            }
        }
        if (AlipayTransferParticipantIdentityType.ALIPAY_LOGON_ID.getCode().equals(participant.getIdentityType())
                && !StringUtils.hasText(participant.getName())) {
            throw new IllegalArgumentException(payee ? "收款方姓名不能为空" : "付款方姓名不能为空");
        }
    }

    private String resolveAccountType(String accountType) {
        return StringUtils.hasText(accountType) ? accountType : DEFAULT_ACCOUNT_TYPE;
    }

    private String resolveTransferBizScene(String bizScene) {
        return StringUtils.hasText(bizScene) ? bizScene : DEFAULT_TRANSFER_BIZ_SCENE;
    }

    private String resolveTransferProductCode(String productCode) {
        return StringUtils.hasText(productCode) ? productCode : DEFAULT_TRANSFER_PRODUCT_CODE;
    }

    private String resolveQueryBizScene(AliPayTransferQueryParam queryParam) {
        if (StringUtils.hasText(queryParam.getBizScene())) {
            return queryParam.getBizScene();
        }
        return StringUtils.hasText(queryParam.getOutBizNo()) ? DEFAULT_TRANSFER_BIZ_SCENE : null;
    }

    private String resolveQueryProductCode(AliPayTransferQueryParam queryParam) {
        if (StringUtils.hasText(queryParam.getProductCode())) {
            return queryParam.getProductCode();
        }
        return StringUtils.hasText(queryParam.getOutBizNo()) ? DEFAULT_TRANSFER_PRODUCT_CODE : null;
    }

    private String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private AliPayBalanceAccountDetail buildAmountDetail(BalanceAccountDetail amountDetail) {
        if (amountDetail == null) {
            return null;
        }
        return AliPayBalanceAccountDetail.builder()
                .acs(amountDetail.getAcs())
                .bank(amountDetail.getBank())
                .build();
    }

    private AliPayExtCardInfo buildExtCardInfo(ExtCardInfo extCardInfo) {
        if (extCardInfo == null) {
            return null;
        }
        return AliPayExtCardInfo.builder()
                .bankAccName(extCardInfo.getBankAccName())
                .cardBank(extCardInfo.getCardBank())
                .cardBranch(extCardInfo.getCardBranch())
                .cardDeposit(extCardInfo.getCardDeposit())
                .cardLocation(extCardInfo.getCardLocation())
                .cardNo(extCardInfo.getCardNo())
                .status(extCardInfo.getStatus())
                .build();
    }

    private Participant buildParticipant(AliPayTransferParticipant participant, boolean payee) {
        if (participant == null) {
            return null;
        }
        Participant sdkParticipant = new Participant();
        sdkParticipant.setBankcardExtInfo(buildBankcardExtInfo(participant.getBankcardExtInfo()));
        sdkParticipant.setCertNo(participant.getCertNo());
        sdkParticipant.setCertType(participant.getCertType());
        sdkParticipant.setExtInfo(participant.getExtInfo());
        sdkParticipant.setIdentity(participant.getIdentity());
        sdkParticipant.setIdentityType(payee
                ? firstNonBlank(participant.getIdentityType(), DEFAULT_PAYEE_IDENTITY_TYPE)
                : participant.getIdentityType());
        sdkParticipant.setMerchantUserInfo(participant.getMerchantUserInfo());
        sdkParticipant.setName(participant.getName());
        return sdkParticipant;
    }

    private BankcardExtInfo buildBankcardExtInfo(AliPayBankcardExtInfo extInfo) {
        if (extInfo == null) {
            return null;
        }
        BankcardExtInfo sdkExtInfo = new BankcardExtInfo();
        sdkExtInfo.setAccountType(extInfo.getAccountType());
        sdkExtInfo.setBankCode(extInfo.getBankCode());
        sdkExtInfo.setInstBranchName(extInfo.getInstBranchName());
        sdkExtInfo.setInstCity(extInfo.getInstCity());
        sdkExtInfo.setInstName(extInfo.getInstName());
        sdkExtInfo.setInstProvince(extInfo.getInstProvince());
        return sdkExtInfo;
    }

    private MutipleCurrencyDetail buildMultiCurrencyDetail(AliPayTransferMultiCurrencyDetail detail) {
        if (detail == null) {
            return null;
        }
        MutipleCurrencyDetail sdkDetail = new MutipleCurrencyDetail();
        sdkDetail.setExtInfo(detail.getExtInfo());
        sdkDetail.setPaymentAmount(detail.getPaymentAmount());
        sdkDetail.setPaymentCurrency(detail.getPaymentCurrency());
        sdkDetail.setSettlementAmount(detail.getSettlementAmount());
        sdkDetail.setSettlementCurrency(detail.getSettlementCurrency());
        sdkDetail.setTransAmount(detail.getTransAmount());
        sdkDetail.setTransCurrency(detail.getTransCurrency());
        return sdkDetail;
    }

    private SignData buildSignData(AliPayTransferSignData signData) {
        if (signData == null) {
            return null;
        }
        SignData sdkSignData = new SignData();
        sdkSignData.setOriAppId(signData.getOriAppId());
        sdkSignData.setOriCharSet(signData.getOriCharSet());
        sdkSignData.setOriOutBizNo(signData.getOriOutBizNo());
        sdkSignData.setOriSign(signData.getOriSign());
        sdkSignData.setOriSignType(signData.getOriSignType());
        sdkSignData.setPartnerId(signData.getPartnerId());
        return sdkSignData;
    }

    private TransferSceneReportInfo[] buildTransferSceneReportInfos(AliPayTransferSceneReportInfo[] reportInfos) {
        if (reportInfos == null || reportInfos.length == 0) {
            return null;
        }
        TransferSceneReportInfo[] sdkReportInfos = new TransferSceneReportInfo[reportInfos.length];
        for (int i = 0; i < reportInfos.length; i++) {
            AliPayTransferSceneReportInfo reportInfo = reportInfos[i];
            if (reportInfo == null) {
                continue;
            }
            TransferSceneReportInfo sdkReportInfo = new TransferSceneReportInfo();
            sdkReportInfo.setInfoContent(reportInfo.getInfoContent());
            sdkReportInfo.setInfoType(reportInfo.getInfoType());
            sdkReportInfos[i] = sdkReportInfo;
        }
        return sdkReportInfos;
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
