package com.hzj.wechat.core.payment.domain;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 虚拟支付道具上传信息。
 */
@Data
public class WechatVirtualPaymentGoods {

    /**
     * 道具 ID。
     */
    private String id;

    /**
     * 道具名称。
     */
    private String name;

    /**
     * 道具单价，单位为分。
     */
    private Long price;

    /**
     * 道具备注。
     */
    private String remark;

    /**
     * 道具图片地址。
     */
    @SerializedName("item_url")
    private String itemUrl;
}
