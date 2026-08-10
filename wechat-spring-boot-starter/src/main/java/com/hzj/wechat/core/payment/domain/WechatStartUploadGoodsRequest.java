package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 启动批量上传道具任务请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatStartUploadGoodsRequest extends WechatVirtualPaymentRequest {

    /**
     * 待上传道具列表。
     */
    @SerializedName("upload_item")
    private List<WechatVirtualPaymentGoods> uploadItem;
}
