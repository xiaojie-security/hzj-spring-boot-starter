package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 上传虚拟支付投诉媒体文件请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatUploadVpFileRequest extends WechatVirtualPaymentRequest {
    public WechatUploadVpFileRequest() { super(WechatVirtualPaymentApi.UPLOAD_VP_FILE); }

    /**
     * Base64 编码的图片内容。
     */
    @SerializedName("base64_img")
    private String base64Img;

    /**
     * 可直接下载的图片地址。
     */
    @SerializedName("img_url")
    private String imgUrl;

    /**
     * 图片名称。
     */
    @SerializedName("file_name")
    private String fileName;
}
