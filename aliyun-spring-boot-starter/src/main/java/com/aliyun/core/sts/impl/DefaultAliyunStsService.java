package com.aliyun.core.sts.impl;

import com.aliyun.core.sts.AliyunStsService;
import com.aliyun.core.sts.domain.AliyunStsSecurityCredential;
import com.aliyun.provider.aliyun.sts.AliyunStsConfigProvider;
import com.aliyun.sts20150401.models.AssumeRoleResponse;
import com.aliyun.sts20150401.models.AssumeRoleResponseBody;
import com.aliyun.tea.TeaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * 阿里云 STS 服务默认实现。
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAliyunStsService implements AliyunStsService {

    private final AliyunStsConfigProvider configProvider;
    private final com.aliyun.sts20150401.Client client;

    @Override
    public AliyunStsSecurityCredential generateStsSecurityCredential(String ramRoleArn) {
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        com.aliyun.sts20150401.models.AssumeRoleRequest request = new com.aliyun.sts20150401.models.AssumeRoleRequest()
                .setRoleArn(ramRoleArn)
                .setDurationSeconds(configProvider.getConfig().getExpire())
                .setRoleSessionName(UUID.randomUUID().toString());
        try {
            AssumeRoleResponse response = client.assumeRoleWithOptions(request, runtime);
            AssumeRoleResponseBody.AssumeRoleResponseBodyCredentials credentials = response.body.credentials;
            return new AliyunStsSecurityCredential(credentials.getSecurityToken(), credentials.getAccessKeySecret(),
                    credentials.getAccessKeyId(), credentials.getExpiration());
        } catch (TeaException error) {
            log.error("DefaultAliyunStsService.generateStsSecurityCredential STS 临时凭证获取失败, ramRoleArn={}", ramRoleArn, error);
            log.error("DefaultAliyunStsService.generateStsSecurityCredential 诊断建议={}", error.getData().get("Recommend"));
            com.aliyun.teautil.Common.assertAsString(error.message);
        } catch (Exception e) {
            log.error("DefaultAliyunStsService.generateStsSecurityCredential STS 临时凭证获取失败, ramRoleArn={}", ramRoleArn, e);
            throw new RuntimeException(e);
        }
        return null;
    }
}
