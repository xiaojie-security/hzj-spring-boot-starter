package com.hzj.wechat.provider.wechat.qrcode.entity;

import com.hzj.wechat.core.xcx.qrcode.enums.WechatXcxEnvVersion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatQrCodeRuntimeConfig {

    /**
     * 小程序二维码激活版本
     */
    private WechatXcxEnvVersion envVersion;

}
