package com.hzj.wechat.core.xcx.sec_check.domain;

import com.google.gson.annotations.SerializedName;
import com.hzj.wechat.core.enums.WechatHttpMethod;
import com.hzj.wechat.core.xcx.sec_check.enums.WechatXcxSecCheckMediaType;
import com.hzj.wechat.core.xcx.sec_check.enums.WechatXcxSecCheckScene;

/**
 * 多媒体内容安全识别请求参数。
 */
public class WechatXcxSecCheckMediaRequest extends WechatXcxSecCheckApiRequest {

    /**
     * 接口版本号，2.0 版本固定为 2。
     */
    public static final int VERSION = 2;

    /**
     * 创建多媒体内容安全识别请求参数。
     */
    public WechatXcxSecCheckMediaRequest() {
        requestPath = "/wxa/media_check_async";
        requestMethod = WechatHttpMethod.POST;
        version = VERSION;
    }

    /**
     * 要检测的图片或音频的 url。
     * 支持图片格式 jpg、jpeg、png、bmp、gif（取首帧）；
     * 支持音频格式 mp3、aac、ac3、wma、flac、vorbis、opus、wav。
     * 单个文件大小不超过 10M，且需要保证可以被检测服务器下载。
     */
    @SerializedName("media_url")
    public String mediaUrl;

    /**
     * 多媒体类型：1 音频，2 图片。
     */
    @SerializedName("media_type")
    public WechatXcxSecCheckMediaType mediaType;

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
}
