package com.hzj.wechat.core.xcx.face.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 获取用户人脸核身会话唯一标识请求参数。
 */
public class WechatXcxFaceGetVerifyIdRequest extends WechatXcxFaceApiRequest {

    /**
     * 创建获取人脸核身会话唯一标识请求参数。
     */
    public WechatXcxFaceGetVerifyIdRequest() {
        requestPath = "/cityservice/face/identify/getverifyid";
        requestMethod = WechatHttpMethod.POST;
    }

    /**
     * 业务方系统内部流水号。
     * 要求 5-32 个字符，只能包含数字、大小写字母和 _- 字符，且在同一个 appid 下唯一。
     */
    @SerializedName("out_seq_no")
    public String outSeqNo;

    /**
     * 用户身份信息。
     */
    @SerializedName("cert_info")
    public WechatXcxFaceCertInfo certInfo;

    /**
     * 用户身份标识。
     * 必须与前端调用 wx.requestFacialVerify 时的用户身份保持一致。
     */
    @SerializedName("openid")
    public String openid;
}
