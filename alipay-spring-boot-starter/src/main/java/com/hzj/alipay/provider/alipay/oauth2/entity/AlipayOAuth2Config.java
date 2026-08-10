package com.hzj.alipay.provider.alipay.oauth2.entity;

import com.hzj.alipay.provider.alipay.entity.AlipayBaseConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 支付宝 OAuth2 配置快照。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class AlipayOAuth2Config extends AlipayBaseConfig {
}
