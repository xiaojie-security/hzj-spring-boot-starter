package com.hzj.wechat.provider.wechat.virtual.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信虚拟支付配置快照。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatVirtualPaymentConfig {

    /**
     * 米大师侧申请的应用 ID，对应虚拟支付接口的 offer_id。
     */
    private String offerId;

    /**
     * 虚拟支付环境，0 表示正式环境，1 表示沙箱环境。
     */
    private Integer env = 0;

    /**
     * 虚拟支付币种，当前官方接口固定使用 CNY。
     */
    private String currencyType = "CNY";
}
