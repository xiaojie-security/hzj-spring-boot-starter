package com.hzj.wechat.core.xcx.face.domain;

import com.google.gson.annotations.SerializedName;

/**
 * 获取用户人脸核身会话唯一标识响应。
 */
public class WechatXcxFaceGetVerifyIdResponse {

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
     * 微信侧生成的人脸核身会话唯一标识，长度不超过 256 字符。
     */
    @SerializedName("verify_id")
    public String verifyId;

    /**
     * verify_id 有效期，过期后无法发起核身，单位秒，默认 3600。
     */
    @SerializedName("expires_in")
    public Integer expiresIn;
}
