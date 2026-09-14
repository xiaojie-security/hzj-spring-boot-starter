package com.hzj.wechat.core.xcx.face.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;

/**
 * 查询用户人脸核身真实验证结果请求参数。
 */
public class WechatXcxFaceQueryVerifyInfoRequest extends WechatXcxFaceApiRequest {

    /**
     * 创建查询人脸核身真实验证结果请求参数。
     */
    public WechatXcxFaceQueryVerifyInfoRequest() {
        requestPath = "/cityservice/face/identify/queryverifyinfo";
        requestMethod = WechatHttpMethod.POST;
    }

    /**
     * getVerifyId 接口返回的人脸核身会话唯一标识。
     */
    @SerializedName("verify_id")
    public String verifyId;

    /**
     * 业务方系统外部流水号，必须和 getVerifyId 接口传入的一致。
     */
    @SerializedName("out_seq_no")
    public String outSeqNo;

    /**
     * 根据 getVerifyId 中传入的证件信息生成的信息摘要。
     * 计算方式见 {@link com.hzj.wechat.core.xcx.face.WechatXcxFaceCertHashUtils}。
     */
    @SerializedName("cert_hash")
    public String certHash;

    /**
     * 用户身份标识，必须和 getVerifyId 接口传入的一致。
     */
    @SerializedName("openid")
    public String openid;
}
