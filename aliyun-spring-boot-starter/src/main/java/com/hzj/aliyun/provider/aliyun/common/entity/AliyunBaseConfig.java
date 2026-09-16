package com.hzj.aliyun.provider.aliyun.common.entity;

import com.hzj.aliyun.provider.aliyun.common.enums.AliyunCredentialMode;
import lombok.Data;

/**
 * 阿里云能力通用配置。
 *
 * <p>凭证字段属于当前能力本身，避免不同阿里云服务共享全局账号。</p>
 */
@Data
public class AliyunBaseConfig {

    /** 当前服务使用的访问凭证模式。 */
    private AliyunCredentialMode credentialMode = AliyunCredentialMode.AK;

    /** 当前服务使用的 AccessKey ID；STS 模式下作为扮演角色的源凭证。 */
    private String accessKeyId;

    /** 当前服务使用的 AccessKey Secret；STS 模式下作为扮演角色的源凭证。 */
    private String accessKeySecret;

    /** 当前服务使用的 RAM 角色 ARN。 */
    private String ramRoleArn;

    /** 当前服务获取 STS 凭证时使用的 STS 接入点。 */
    private String stsEndpoint = "sts.aliyuncs.com";

    /** 当前服务临时凭证有效期，单位为秒。 */
    private Long expire = 3600L;

    /**
     * 判断当前服务是否使用 STS 临时凭证。
     *
     * @return true-使用 STS，false-使用固定 AK
     */
    public boolean useSts() {
        return credentialMode == AliyunCredentialMode.STS;
    }
}
