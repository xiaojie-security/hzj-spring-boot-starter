package com.hzj.wechat.provider.wechat.qrcode.entity;

import com.hzj.wechat.core.qrcode.enums.WechatXcxEnvVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatQrCodeConfig {

    /**
     * 小程序二维码激活版本
     */
    private WechatXcxEnvVersion envVersion;

}
