package com.hzj.wechat.core.xcx.sec_check.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;
import com.hzj.wechat.core.xcx.sec_check.enums.WechatXcxSecCheckScene;

/**
 * 文本内容安全识别请求参数。
 */
public class WechatXcxSecCheckMsgRequest extends WechatXcxSecCheckApiRequest {

    /**
     * 接口版本号，2.0 版本固定为 2。
     */
    public static final int VERSION = 2;

    /**
     * 创建文本内容安全识别请求参数。
     */
    public WechatXcxSecCheckMsgRequest() {
        requestPath = "/wxa/msg_sec_check";
        requestMethod = WechatHttpMethod.POST;
        version = VERSION;
    }

    /**
     * 需检测的文本内容，文本字数的上限为 2500 字，需使用 UTF-8 编码。
     */
    @SerializedName("content")
    public String content;

    /**
     * 接口版本号，2.0 版本为固定值 2。
     */
    @SerializedName("version")
    public Integer version;

    /**
     * 场景枚举值。
     * 未设置时使用配置提供者的默认值，仍为空时使用「资料」。
     */
    @SerializedName("scene")
    public WechatXcxSecCheckScene scene;

    /**
     * 用户的 openid，用户需在近两小时访问过小程序。
     */
    @SerializedName("openid")
    public String openid;

    /**
     * 文本标题，需使用 UTF-8 编码。
     */
    @SerializedName("title")
    public String title;

    /**
     * 用户昵称，需使用 UTF-8 编码。
     */
    @SerializedName("nickname")
    public String nickname;

    /**
     * 个性签名，该参数仅在资料类场景（scene=1）有效，需使用 UTF-8 编码。
     */
    @SerializedName("signature")
    public String signature;
}
