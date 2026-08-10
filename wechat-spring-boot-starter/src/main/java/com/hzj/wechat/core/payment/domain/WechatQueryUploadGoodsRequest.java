package com.hzj.wechat.core.payment.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询批量上传道具任务请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class WechatQueryUploadGoodsRequest extends WechatVirtualPaymentRequest {
}
