package com.hzj.aliyun.core.sts.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * STS 临时安全凭证。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliyunStsSecurityCredential {

    /** 安全令牌。 */
    private String securityToken;

    /** 访问密钥 Secret。 */
    private String accessKeySecret;

    /** 访问密钥 ID。 */
    private String accessKeyId;

    /** 凭证过期时间。 */
    private String expiration;
}
