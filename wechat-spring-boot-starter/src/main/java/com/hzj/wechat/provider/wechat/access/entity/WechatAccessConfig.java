package com.hzj.wechat.provider.wechat.access.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatAccessConfig {

    /**
     * 微信应用唯一标识。
     */
    private String appid;

    /**
     * 微信应用密钥。
     */
    private String secret;

}
