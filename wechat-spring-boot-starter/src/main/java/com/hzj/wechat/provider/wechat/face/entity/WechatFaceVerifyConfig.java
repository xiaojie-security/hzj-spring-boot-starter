package com.hzj.wechat.provider.wechat.face.entity;

import com.hzj.wechat.core.xcx.face.enums.WechatXcxFaceCertType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信人脸核身动态配置。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WechatFaceVerifyConfig {

    /**
     * 默认证件类型，未传时使用「身份证」。
     */
    private WechatXcxFaceCertType certType = WechatXcxFaceCertType.IDENTITY_CARD;
}
