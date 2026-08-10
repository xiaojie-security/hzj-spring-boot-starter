package com.hzj.wechat.provider.wechat.transfer.entity;

import com.hzj.wechat.provider.wechat.payment.entity.WechatPaymentConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatTransferConfig extends WechatPaymentConfig {

    /**
     * 商家转账异步通知地址
     */
    private String transferNotifyUrl;

    /**
     * 免确认收款授权结果通知
     */
    private String authorizationNotifyUrl;

}
