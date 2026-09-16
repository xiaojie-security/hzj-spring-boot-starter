package com.hzj.wechat.provider.wechat.transfer.entity;

import com.hzj.wechat.provider.wechat.payment.entity.WechatPaymentRuntimeConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 微信商家转账运行时业务配置。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatTransferRuntimeConfig extends WechatPaymentRuntimeConfig {

    /** 商家转账异步通知地址。 */
    private String transferNotifyUrl;

    /** 免确认收款授权结果通知地址。 */
    private String authorizationNotifyUrl;
}
