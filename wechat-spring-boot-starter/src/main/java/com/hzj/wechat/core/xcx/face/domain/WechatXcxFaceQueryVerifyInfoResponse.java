package com.hzj.wechat.core.xcx.face.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.xcx.face.enums.WechatXcxFaceVerifyResult;

/**
 * 查询用户人脸核身真实验证结果响应。
 */
public class WechatXcxFaceQueryVerifyInfoResponse {

    /**
     * 微信错误码，0 表示成功。
     */
    @SerializedName("errcode")
    public Integer errcode;

    /**
     * 微信错误信息。
     */
    @SerializedName("errmsg")
    public String errmsg;

    /**
     * 人脸核身验证结果。
     * 核身通过的判断条件为 errcode=0 且 verify_ret=10000。
     */
    @SerializedName("verify_ret")
    public WechatXcxFaceVerifyResult verifyRet;
}
