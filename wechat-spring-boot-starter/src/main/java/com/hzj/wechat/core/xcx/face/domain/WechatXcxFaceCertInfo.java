package com.hzj.wechat.core.xcx.face.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.xcx.face.enums.WechatXcxFaceCertType;

/**
 * 微信人脸核身用户身份信息。
 */
public class WechatXcxFaceCertInfo {

    /**
     * 证件类型，身份证填 IDENTITY_CARD。
     */
    @SerializedName("cert_type")
    public WechatXcxFaceCertType certType;

    /**
     * 证件姓名。
     */
    @SerializedName("cert_name")
    public String certName;

    /**
     * 证件号码。
     */
    @SerializedName("cert_no")
    public String certNo;
}
