package com.hzj.wechat.core.payment.enums;

import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 微信小程序虚拟支付接口定义。
 */
public enum WechatVirtualPaymentApi {

    QUERY_USER_BALANCE("query_user_balance"),
    CURRENCY_PAY("currency_pay"),
    QUERY_ORDER("query_order"),
    CANCEL_CURRENCY_PAY("cancel_currency_pay"),
    NOTIFY_PROVIDE_GOODS("notify_provide_goods"),
    PRESENT_CURRENCY("present_currency"),
    REFUND_ORDER("refund_order"),
    CREATE_WITHDRAW_ORDER("create_withdraw_order"),
    QUERY_WITHDRAW_ORDER("query_withdraw_order"),
    SEND_SUBSCRIBE_PRE_PAYMENT("send_subscribe_pre_payment"),
    SUBMIT_SUBSCRIBE_PAY_ORDER("submit_subscribe_pay_order"),
    QUERY_SUBSCRIBE_CONTRACT("query_subscribe_contract"),
    CANCEL_SUBSCRIBE_CONTRACT("cancel_subscribe_contract"),
    DOWNLOAD_BILL("download_bill"),
    START_UPLOAD_GOODS("start_upload_goods"),
    QUERY_UPLOAD_GOODS("query_upload_goods"),
    START_PUBLISH_GOODS("start_publish_goods"),
    QUERY_PUBLISH_GOODS("query_publish_goods"),
    QUERY_BIZ_BALANCE("query_biz_balance"),
    QUERY_TRANSFER_ACCOUNT("query_transfer_account"),
    QUERY_ADVER_FUNDS("query_adver_funds"),
    CREATE_FUNDS_BILL("create_funds_bill"),
    BIND_TRANSFER_ACCOUT("bind_transfer_accout"),
    QUERY_FUNDS_BILL("query_funds_bill"),
    QUERY_RECOVER_BILL("query_recover_bill"),
    GET_COMPLAINT_LIST("get_complaint_list"),
    GET_COMPLAINT_DETAIL("get_complaint_detail"),
    GET_NEGOTIATION_HISTORY("get_negotiation_history"),
    RESPONSE_COMPLAINT("response_complaint"),
    COMPLETE_COMPLAINT("complete_complaint"),
    UPLOAD_VP_FILE("upload_vp_file"),
    GET_UPLOAD_FILE_SIGN("get_upload_file_sign"),
    DOWNLOAD_ADVERFUNDS_ORDER("download_adverfunds_order"),
    START_DOWNLOAD_ORDER("start_download_order"),
    QUERY_DOWNLOAD_ORDER("query_download_order"),
    DOWNLOAD_IOS_SETTLEMENT_BILL("download_ios_settlement_bill"),
    QUERY_PUNISHMENT_REASONS("query_punishment_reasons");

    /**
     * 微信虚拟支付接口基础地址。
     */
    private static final String BASE_URL = "https://api.weixin.qq.com/xpay/";

    /**
     * 接口操作标识。
     */
    private final String action;

    WechatVirtualPaymentApi(String action) {
        this.action = action;
    }

    /**
     * 获取接口操作标识。
     *
     * @return 接口操作标识
     */
    public String getAction() {
        return action;
    }

    /**
     * 获取接口完整地址。
     *
     * @return 接口完整地址
     */
    public String getRequestUrl() {
        return BASE_URL + action;
    }

    /**
     * 获取接口默认请求方法。
     *
     * @return HTTP 请求方法
     */
    public WechatHttpMethod getRequestMethod() {
        return WechatHttpMethod.POST;
    }
}
