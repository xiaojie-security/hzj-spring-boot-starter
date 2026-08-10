package com.hzj.alipay.provider.alipay.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付宝客户端通用配置。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlipayBaseConfig {

    /** 应用 ID。 */
    private String appId;

    /** 支付宝网关地址。 */
    private String gateWay;

    /** 应用私钥。 */
    private String privateKey;

    /** 支付宝公钥。 */
    private String publicKey;

    /** 应用公钥证书路径。 */
    private String appCertPath;

    /** 支付宝公钥证书路径。 */
    private String alipayPublicCertPath;

    /** 支付宝根证书路径。 */
    private String rootCertPath;

    /** 是否启用证书模式。 */
    private Boolean certificates;

    /**
     * 判断是否启用证书模式。
     *
     * @return true-启用，false-未启用
     */
    public boolean isCertificates() {
        return Boolean.TRUE.equals(certificates);
    }
}
