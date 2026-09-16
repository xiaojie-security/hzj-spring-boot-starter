package com.hzj.aliyun.provider.aliyun.common.entity;

import com.hzj.aliyun.provider.aliyun.common.enums.AliyunCredentialMode;
import lombok.Data;

/**
 * 阿里云客户端启动期凭证配置。
 *
 * <p>该配置在客户端创建时读取并生成快照，运行时不应修改。</p>
 */
@Data
public class AliyunCredentialConfig {

    /** 访问凭证模式。 */
    private AliyunCredentialMode credentialMode = AliyunCredentialMode.AK;

    /** AccessKey ID；STS 模式下作为扮演角色的源凭证。 */
    private String accessKeyId;

    /** AccessKey Secret；STS 模式下作为扮演角色的源凭证。 */
    private String accessKeySecret;

    /** STS 模式下需要扮演的 RAM 角色 ARN。 */
    private String ramRoleArn;

    /** 获取 STS 凭证使用的接入点。 */
    private String stsEndpoint = "sts.aliyuncs.com";

    /** STS 临时凭证有效期，单位为秒。 */
    private Long stsDurationSeconds = 3600L;

    /**
     * 判断当前是否使用 STS 临时凭证。
     *
     * @return true-使用 STS，false-使用固定 AK
     */
    public boolean useSts() {
        return credentialMode == AliyunCredentialMode.STS;
    }

    /**
     * 创建当前凭证配置的独立副本。
     *
     * @return 凭证配置副本
     */
    public AliyunCredentialConfig copy() {
        AliyunCredentialConfig copy = new AliyunCredentialConfig();
        copy.setCredentialMode(credentialMode);
        copy.setAccessKeyId(accessKeyId);
        copy.setAccessKeySecret(accessKeySecret);
        copy.setRamRoleArn(ramRoleArn);
        copy.setStsEndpoint(stsEndpoint);
        copy.setStsDurationSeconds(stsDurationSeconds);
        return copy;
    }
}
