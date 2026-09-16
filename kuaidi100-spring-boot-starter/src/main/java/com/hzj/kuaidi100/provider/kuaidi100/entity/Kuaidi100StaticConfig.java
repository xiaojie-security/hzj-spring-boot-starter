package com.hzj.kuaidi100.provider.kuaidi100.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 快递100启动期静态授权配置。
 *
 * <p>授权凭据用于签名和身份认证，应用启动后不支持动态刷新。</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Kuaidi100StaticConfig {

    /** 快递100授权 key。 */
    private String key;

    /** 实时查询接口 customer。 */
    private String customer;

    /** 企业接口签名 secret。 */
    private String secret;

    /** 快递100用户标识，部分账号体系使用。 */
    private String userId;
}
