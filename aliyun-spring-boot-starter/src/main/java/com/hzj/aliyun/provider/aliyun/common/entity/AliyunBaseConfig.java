package com.hzj.aliyun.provider.aliyun.common.entity;

import lombok.Data;

/**
 * 阿里云能力通用配置。
 *
 * <p>凭证属于当前能力本身，避免不同阿里云服务共享全局账号。</p>
 */
@Data
public class AliyunBaseConfig {

    /** 启动期读取的当前能力凭证配置。 */
    private AliyunCredentialConfig credential = new AliyunCredentialConfig();

    /**
     * 获取启动期凭证配置快照。
     *
     * @return 凭证配置快照
     */
    public AliyunCredentialConfig snapshotCredentialConfig() {
        return credential == null ? null : credential.copy();
    }

    /**
     * 判断当前服务是否使用 STS 临时凭证。
     *
     * @return true-使用 STS，false-使用固定 AK
     */
    public boolean useSts() {
        return credential != null && credential.useSts();
    }
}
