package com.hzj.wechat.core.payment.service;

import com.hzj.wechat.core.payment.domain.WechatVirtualPaymentResponse;
import com.hzj.wechat.core.payment.domain.WechatCancelCurrencyPayRequest;
import com.hzj.wechat.core.payment.domain.WechatCreateWithdrawOrderRequest;
import com.hzj.wechat.core.payment.domain.WechatCurrencyPayRequest;
import com.hzj.wechat.core.payment.domain.WechatNotifyProvideGoodsRequest;
import com.hzj.wechat.core.payment.domain.WechatPresentCurrencyRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryOrderRequest;
import com.hzj.wechat.core.payment.domain.WechatQuerySubscribeContractRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryUserBalanceRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryWithdrawOrderRequest;
import com.hzj.wechat.core.payment.domain.WechatRefundOrderRequest;
import com.hzj.wechat.core.payment.domain.WechatSendSubscribePrePaymentRequest;
import com.hzj.wechat.core.payment.domain.WechatSubmitSubscribePayOrderRequest;
import com.hzj.wechat.core.payment.domain.WechatCancelSubscribeContractRequest;
import com.hzj.wechat.core.payment.domain.WechatBindTransferAccountRequest;
import com.hzj.wechat.core.payment.domain.WechatCompleteComplaintRequest;
import com.hzj.wechat.core.payment.domain.WechatCreateFundsBillRequest;
import com.hzj.wechat.core.payment.domain.WechatDownloadAdverfundsOrderRequest;
import com.hzj.wechat.core.payment.domain.WechatDownloadBillRequest;
import com.hzj.wechat.core.payment.domain.WechatDownloadIosSettlementBillRequest;
import com.hzj.wechat.core.payment.domain.WechatGetComplaintDetailRequest;
import com.hzj.wechat.core.payment.domain.WechatGetComplaintListRequest;
import com.hzj.wechat.core.payment.domain.WechatGetNegotiationHistoryRequest;
import com.hzj.wechat.core.payment.domain.WechatGetUploadFileSignRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryAdverFundsRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryBizBalanceRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryDownloadOrderRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryFundsBillRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryPunishmentReasonsRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryPublishGoodsRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryRecoverBillRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryTransferAccountRequest;
import com.hzj.wechat.core.payment.domain.WechatQueryUploadGoodsRequest;
import com.hzj.wechat.core.payment.domain.WechatResponseComplaintRequest;
import com.hzj.wechat.core.payment.domain.WechatStartDownloadOrderRequest;
import com.hzj.wechat.core.payment.domain.WechatStartPublishGoodsRequest;
import com.hzj.wechat.core.payment.domain.WechatStartUploadGoodsRequest;
import com.hzj.wechat.core.payment.domain.WechatUploadVpFileRequest;

/**
 * 微信小程序虚拟支付服务。
 */
public interface WechatVirtualPaymentService {

    /**
     * 查询用户代币余额。
     *
     * @param request 请求参数
     * @return 余额响应
     */
    WechatVirtualPaymentResponse queryUserBalance(WechatQueryUserBalanceRequest request);

    /**
     * 扣减用户代币。
     *
     * @param request 请求参数
     * @return 扣减结果
     */
    WechatVirtualPaymentResponse currencyPay(WechatCurrencyPayRequest request);

    /**
     * 查询虚拟支付订单。
     *
     * @param request 请求参数
     * @return 订单响应
     */
    WechatVirtualPaymentResponse queryOrder(WechatQueryOrderRequest request);

    /**
     * 取消代币支付并退款。
     *
     * @param request 请求参数
     * @return 退款结果
     */
    WechatVirtualPaymentResponse cancelCurrencyPay(WechatCancelCurrencyPayRequest request);

    /**
     * 通知微信侧发货。
     *
     * @param request 请求参数
     */
    void notifyProvideGoods(WechatNotifyProvideGoodsRequest request);

    /**
     * 赠送用户代币。
     *
     * @param request 请求参数
     * @return 赠送结果
     */
    WechatVirtualPaymentResponse presentCurrency(WechatPresentCurrencyRequest request);

    /**
     * 退款订单。
     *
     * @param request 请求参数
     * @return 退款结果
     */
    WechatVirtualPaymentResponse refundOrder(WechatRefundOrderRequest request);

    /**
     * 创建提现单。
     *
     * @param request 请求参数
     * @return 提现结果
     */
    WechatVirtualPaymentResponse createWithdrawOrder(WechatCreateWithdrawOrderRequest request);

    /**
     * 查询提现单。
     *
     * @param request 请求参数
     * @return 提现结果
     */
    WechatVirtualPaymentResponse queryWithdrawOrder(WechatQueryWithdrawOrderRequest request);

    /**
     * 发送订阅扣款预通知。
     *
     * @param request 请求参数
     * @return 接口响应
     */
    WechatVirtualPaymentResponse sendSubscribePrePayment(WechatSendSubscribePrePaymentRequest request);

    /**
     * 提交订阅扣款订单。
     *
     * @param request 请求参数
     * @return 接口响应
     */
    WechatVirtualPaymentResponse submitSubscribePayOrder(WechatSubmitSubscribePayOrderRequest request);

    /**
     * 查询订阅协议。
     *
     * @param request 请求参数
     * @return 协议响应
     */
    WechatVirtualPaymentResponse querySubscribeContract(WechatQuerySubscribeContractRequest request);

    /**
     * 取消订阅协议。
     *
     * @param request 请求参数
     * @return 解约响应
     */
    WechatVirtualPaymentResponse cancelSubscribeContract(WechatCancelSubscribeContractRequest request);

    /**
     * 下载小程序账单。
     *
     * @param request 请求参数
     * @return 账单下载地址
     */
    WechatVirtualPaymentResponse downloadBill(WechatDownloadBillRequest request);

    /**
     * 启动批量上传道具任务。
     *
     * @param request 请求参数
     * @return 接口响应
     */
    WechatVirtualPaymentResponse startUploadGoods(WechatStartUploadGoodsRequest request);

    /**
     * 查询批量上传道具任务。
     *
     * @param request 请求参数
     * @return 任务响应
     */
    WechatVirtualPaymentResponse queryUploadGoods(WechatQueryUploadGoodsRequest request);

    /**
     * 启动批量发布道具任务。
     *
     * @param request 请求参数
     * @return 接口响应
     */
    WechatVirtualPaymentResponse startPublishGoods(WechatStartPublishGoodsRequest request);

    /**
     * 查询批量发布道具任务。
     *
     * @param request 请求参数
     * @return 任务响应
     */
    WechatVirtualPaymentResponse queryPublishGoods(WechatQueryPublishGoodsRequest request);

    /**
     * 查询商户可提现余额。
     *
     * @param request 请求参数
     * @return 余额响应
     */
    WechatVirtualPaymentResponse queryBizBalance(WechatQueryBizBalanceRequest request);

    /**
     * 查询广告金充值账户。
     *
     * @param request 请求参数
     * @return 账户响应
     */
    WechatVirtualPaymentResponse queryTransferAccount(WechatQueryTransferAccountRequest request);

    /**
     * 查询广告金发放记录。
     *
     * @param request 请求参数
     * @return 发放记录响应
     */
    WechatVirtualPaymentResponse queryAdverFunds(WechatQueryAdverFundsRequest request);

    /**
     * 创建广告金充值单。
     *
     * @param request 请求参数
     * @return 充值单响应
     */
    WechatVirtualPaymentResponse createFundsBill(WechatCreateFundsBillRequest request);

    /**
     * 绑定广告金充值账户。
     *
     * @param request 请求参数
     * @return 接口响应
     */
    WechatVirtualPaymentResponse bindTransferAccount(WechatBindTransferAccountRequest request);

    /**
     * 查询广告金充值记录。
     *
     * @param request 请求参数
     * @return 充值记录响应
     */
    WechatVirtualPaymentResponse queryFundsBill(WechatQueryFundsBillRequest request);

    /**
     * 查询广告金回收记录。
     *
     * @param request 请求参数
     * @return 回收记录响应
     */
    WechatVirtualPaymentResponse queryRecoverBill(WechatQueryRecoverBillRequest request);

    /**
     * 获取投诉列表。
     *
     * @param request 请求参数
     * @return 投诉列表响应
     */
    WechatVirtualPaymentResponse getComplaintList(WechatGetComplaintListRequest request);

    /**
     * 获取投诉详情。
     *
     * @param request 请求参数
     * @return 投诉详情响应
     */
    WechatVirtualPaymentResponse getComplaintDetail(WechatGetComplaintDetailRequest request);

    /**
     * 获取投诉协商历史。
     *
     * @param request 请求参数
     * @return 协商历史响应
     */
    WechatVirtualPaymentResponse getNegotiationHistory(WechatGetNegotiationHistoryRequest request);

    /**
     * 回复投诉用户。
     *
     * @param request 请求参数
     * @return 接口响应
     */
    WechatVirtualPaymentResponse responseComplaint(WechatResponseComplaintRequest request);

    /**
     * 完成投诉处理。
     *
     * @param request 请求参数
     * @return 接口响应
     */
    WechatVirtualPaymentResponse completeComplaint(WechatCompleteComplaintRequest request);

    /**
     * 上传投诉媒体文件。
     *
     * @param request 请求参数
     * @return 媒体文件响应
     */
    WechatVirtualPaymentResponse uploadVpFile(WechatUploadVpFileRequest request);

    /**
     * 获取投诉图片请求签名。
     *
     * @param request 请求参数
     * @return 图片签名响应
     */
    WechatVirtualPaymentResponse getUploadFileSign(WechatGetUploadFileSignRequest request);

    /**
     * 下载广告金对应的商户订单信息。
     *
     * @param request 请求参数
     * @return 订单下载响应
     */
    WechatVirtualPaymentResponse downloadAdverFundsOrder(WechatDownloadAdverfundsOrderRequest request);

    /**
     * 启动支付订单下载任务。
     *
     * @param request 请求参数
     * @return 下载任务响应
     */
    WechatVirtualPaymentResponse startDownloadOrder(WechatStartDownloadOrderRequest request);

    /**
     * 查询支付订单下载任务。
     *
     * @param request 请求参数
     * @return 下载任务响应
     */
    WechatVirtualPaymentResponse queryDownloadOrder(WechatQueryDownloadOrderRequest request);

    /**
     * 下载 iOS 月结账单。
     *
     * @param request 请求参数
     * @return 账单响应
     */
    WechatVirtualPaymentResponse downloadIosSettlementBill(WechatDownloadIosSettlementBillRequest request);

    /**
     * 查询商户被管控原因。
     *
     * @param request 请求参数
     * @return 管控原因响应
     */
    WechatVirtualPaymentResponse queryPunishmentReasons(WechatQueryPunishmentReasonsRequest request);
}
