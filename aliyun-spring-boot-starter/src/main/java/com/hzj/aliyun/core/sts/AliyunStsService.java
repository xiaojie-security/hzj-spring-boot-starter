package com.hzj.aliyun.core.sts;

import com.hzj.aliyun.core.sts.domain.AliyunStsSecurityCredential;
public interface AliyunStsService {

    /**
     * 使用指定 RAM 角色获取临时凭证。
     *
     * @param ramRoleArn RAM 角色 ARN
     * @return STS 临时凭证
     */
    AliyunStsSecurityCredential generateStsSecurityCredential(String ramRoleArn);
}
