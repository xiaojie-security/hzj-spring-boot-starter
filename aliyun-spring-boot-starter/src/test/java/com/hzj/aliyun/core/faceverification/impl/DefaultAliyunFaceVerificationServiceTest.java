package com.hzj.aliyun.core.faceverification.impl;

import com.aliyun.cloudauth20190307.models.ContrastFaceVerifyAdvanceRequest;
import com.aliyun.cloudauth20190307.models.ContrastFaceVerifyResponse;
import com.aliyun.cloudauth20190307.models.ContrastFaceVerifyResponseBody;
import com.aliyun.cloudauth20190307.models.InitFaceVerifyRequest;
import com.aliyun.cloudauth20190307.models.InitFaceVerifyResponse;
import com.aliyun.cloudauth20190307.models.InitFaceVerifyResponseBody;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationContrastParam;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationContrastResult;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationInitParam;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationInitResult;
import com.hzj.aliyun.provider.aliyun.faceverification.AliyunFaceVerificationRuntimeConfigProvider;
import com.hzj.aliyun.provider.aliyun.faceverification.entity.AliyunFaceVerificationRuntimeConfig;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 阿里云金融级实人认证服务测试。
 */
class DefaultAliyunFaceVerificationServiceTest {

    @Test
    void shouldMapInitFaceVerifyRequestAndResponse() throws Exception {
        AliyunFaceVerificationRuntimeConfig runtimeConfig = new AliyunFaceVerificationRuntimeConfig();
        runtimeConfig.setSceneId(1000000006L);
        AliyunFaceVerificationRuntimeConfigProvider configProvider = mock(
                AliyunFaceVerificationRuntimeConfigProvider.class);
        when(configProvider.getConfig()).thenReturn(runtimeConfig);

        com.aliyun.cloudauth20190307.Client client = mock(com.aliyun.cloudauth20190307.Client.class);
        InitFaceVerifyResponseBody body = new InitFaceVerifyResponseBody()
                .setCode("200")
                .setMessage("success")
                .setRequestId("request-id")
                .setResultObject(new InitFaceVerifyResponseBody.InitFaceVerifyResponseBodyResultObject()
                        .setCertifyId("certify-id")
                        .setCertifyUrl("https://t.aliyun.com/certify"));
        when(client.initFaceVerifyWithOptions(any(InitFaceVerifyRequest.class), any()))
                .thenReturn(new InitFaceVerifyResponse().setBody(body));

        DefaultAliyunFaceVerificationService service = new DefaultAliyunFaceVerificationService(
                configProvider, client);
        AliyunFaceVerificationInitResult result = service.initFaceVerify(
                AliyunFaceVerificationInitParam.builder()
                        .outerOrderNo("order-001")
                        .certName("张三")
                        .certNo("110101199001010011")
                        .metaInfo("{\"apdidToken\":\"token\"}")
                        .build());

        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getCertifyId()).isEqualTo("certify-id");
        assertThat(result.getCertifyUrl()).isEqualTo("https://t.aliyun.com/certify");
        verify(client).initFaceVerifyWithOptions(any(InitFaceVerifyRequest.class), any());
    }

    @Test
    void shouldUseAdvanceApiForVideoInputStream() throws Exception {
        AliyunFaceVerificationRuntimeConfig runtimeConfig = new AliyunFaceVerificationRuntimeConfig();
        runtimeConfig.setSceneId(1000000006L);
        AliyunFaceVerificationRuntimeConfigProvider configProvider = mock(
                AliyunFaceVerificationRuntimeConfigProvider.class);
        when(configProvider.getConfig()).thenReturn(runtimeConfig);

        com.aliyun.cloudauth20190307.Client client = mock(com.aliyun.cloudauth20190307.Client.class);
        ContrastFaceVerifyResponseBody body = new ContrastFaceVerifyResponseBody()
                .setCode("200")
                .setMessage("success")
                .setRequestId("request-id")
                .setResultObject(new ContrastFaceVerifyResponseBody.ContrastFaceVerifyResponseBodyResultObject()
                        .setCertifyId("certify-id")
                        .setPassed("T"));
        when(client.contrastFaceVerifyAdvance(any(ContrastFaceVerifyAdvanceRequest.class), any()))
                .thenReturn(new ContrastFaceVerifyResponse().setBody(body));

        DefaultAliyunFaceVerificationService service = new DefaultAliyunFaceVerificationService(
                configProvider, client);
        AliyunFaceVerificationContrastResult result = service.contrastFaceVerifyVideo(
                AliyunFaceVerificationContrastParam.builder()
                        .outerOrderNo("order-002")
                        .certName("张三")
                        .certNo("110101199001010011")
                        .faceContrastFileObject(new ByteArrayInputStream(new byte[]{1, 2, 3}))
                        .build());

        assertThat(result.getPassed()).isEqualTo("T");
        assertThat(result.getCertifyId()).isEqualTo("certify-id");
        verify(client).contrastFaceVerifyAdvance(any(ContrastFaceVerifyAdvanceRequest.class), any());
        verify(client, never()).contrastFaceVerifyWithOptions(any(), any());
    }
}
