package com.hzj.alipay.provider.alipay.transfer.entity;

import com.hzj.alipay.provider.alipay.entity.AlipayBaseConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 支付宝转账配置快照。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AlipayTransferConfig extends AlipayBaseConfig {

    /** 卖家 ID。 */
    private String sellerId;
}
