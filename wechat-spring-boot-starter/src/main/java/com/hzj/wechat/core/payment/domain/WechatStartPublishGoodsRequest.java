package com.hzj.wechat.core.payment.domain;

import com.hzj.wechat.core.payment.enums.WechatVirtualPaymentApi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 启动批量发布道具任务请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatStartPublishGoodsRequest extends WechatVirtualPaymentRequest {
    public WechatStartPublishGoodsRequest() { super(WechatVirtualPaymentApi.START_PUBLISH_GOODS); }

    /**
     * 待发布道具列表。
     */
    @SerializedName("publish_item")
    private List<WechatVirtualPaymentPublishItem> publishItem;
}
