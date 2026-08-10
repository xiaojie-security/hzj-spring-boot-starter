package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微信虚拟支付通用响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WechatVirtualPaymentResponse {

    /**
     * 微信业务错误码，0 表示成功。
     */
    private Integer errcode;

    /**
     * 微信业务错误信息。
     */
    private String errmsg;

    /**
     * 订单号。
     */
    @SerializedName("order_id")
    private String orderId;

    /**
     * 微信侧订单号。
     */
    @SerializedName("wx_order_id")
    private String wxOrderId;

    /**
     * 微信侧提现单号。
     */
    @SerializedName("wx_withdraw_no")
    private String wxWithdrawNo;

    /**
     * 商户提现单号。
     */
    @SerializedName("withdraw_no")
    private String withdrawNo;

    /**
     * 提现状态。
     */
    private Integer status;

    /**
     * 提现金额，单位元。
     */
    @SerializedName("withdraw_amount")
    private String withdrawAmount;

    /**
     * 提现成功时间。
     */
    @SerializedName("withdraw_success_timestamp")
    private String withdrawSuccessTimestamp;

    /**
     * 提现创建时间。
     */
    @SerializedName("withdraw_create_time")
    private String withdrawCreateTime;

    /**
     * 提现失败原因。
     */
    @SerializedName("fail_reason")
    private String failReason;

    /**
     * 用户代币总余额。
     */
    private Long balance;

    /**
     * 用户赠送代币余额。
     */
    @SerializedName("present_balance")
    private Long presentBalance;

    /**
     * 使用赠送代币数量。
     */
    @SerializedName("used_present_amount")
    private Long usedPresentAmount;

    /**
     * 累计有价代币充值数量。
     */
    @SerializedName("sum_save")
    private Long sumSave;

    /**
     * 累计赠送代币数量。
     */
    @SerializedName("sum_present")
    private Long sumPresent;

    /**
     * 历史增加代币总量。
     */
    @SerializedName("sum_balance")
    private Long sumBalance;

    /**
     * 历史消耗代币总量。
     */
    @SerializedName("sum_cost")
    private Long sumCost;

    /**
     * 是否满足首充活动。
     */
    @SerializedName("first_save_flag")
    private Boolean firstSaveFlag;

    /**
     * 退款单号。
     */
    @SerializedName("refund_order_id")
    private String refundOrderId;

    /**
     * 退款微信侧单号。
     */
    @SerializedName("refund_wx_order_id")
    private String refundWxOrderId;

    /**
     * 退款对应的支付单号。
     */
    @SerializedName("pay_order_id")
    private String payOrderId;

    /**
     * 退款对应的支付微信侧单号。
     */
    @SerializedName("pay_wx_order_id")
    private String payWxOrderId;

    /**
     * 订阅协议授权状态。
     */
    @SerializedName("authorization_state")
    private String authorizationState;

    /**
     * 账单或订单下载地址。
     */
    private String url;

    /**
     * 下载任务 ID。
     */
    @SerializedName("task_id")
    private String taskId;

    /**
     * 下载文件地址。
     */
    @SerializedName("download_url")
    private String downloadUrl;

    /**
     * 下载地址过期时间。
     */
    @SerializedName("expire_at")
    private Long expireAt;

    /**
     * 媒体文件 ID。
     */
    @SerializedName("file_id")
    private String fileId;

    /**
     * 微信支付图片请求 Authorization 头。
     */
    private String sign;

    /**
     * 转存到 COS 后的图片地址。
     */
    @SerializedName("cos_url")
    private String cosUrl;

    /**
     * 可提现余额。
     */
    @SerializedName("balance_available")
    private JsonObject balanceAvailable;

    /**
     * 广告金充值账户列表。
     */
    @SerializedName("acct_list")
    private List<JsonObject> accountList;

    /**
     * 批量上传道具任务明细。
     */
    @SerializedName("upload_item")
    private List<JsonObject> uploadItem;

    /**
     * 批量发布道具任务明细。
     */
    @SerializedName("publish_item")
    private List<JsonObject> publishItem;

    /**
     * 广告金发放记录列表。
     */
    @SerializedName("adver_funds_list")
    private List<JsonObject> adverFundsList;

    /**
     * 分页查询命中的总页数。
     */
    @SerializedName("total_page")
    private Integer totalPage;

    /**
     * 投诉总数。
     */
    private Integer total;

    /**
     * 投诉列表。
     */
    private List<JsonObject> complaints;

    /**
     * 投诉详情。
     */
    private JsonObject complaint;

    /**
     * 协商历史。
     */
    private List<JsonObject> history;

    /**
     * 小程序 AppID。
     */
    private String appid;

    /**
     * 小程序昵称。
     */
    private String nickname;

    /**
     * 微信支付商户号。
     */
    @SerializedName("merchant_code")
    private String merchantCode;

    /**
     * 商户被管控的能力列表。
     */
    @SerializedName("limited_functions")
    private List<String> limitedFunctions;

    /**
     * 商户其他被管控能力描述。
     */
    @SerializedName("other_limited_functions")
    private String otherLimitedFunctions;

    /**
     * 被管控原因及解脱路径列表。
     */
    @SerializedName("recovery_specifications")
    private List<JsonObject> recoverySpecifications;

    /**
     * iOS 月结账单列表。
     */
    @SerializedName("bill_list")
    private List<JsonObject> billList;

    /**
     * 订单详情。
     */
    private WechatVirtualPaymentOrder order;
}
