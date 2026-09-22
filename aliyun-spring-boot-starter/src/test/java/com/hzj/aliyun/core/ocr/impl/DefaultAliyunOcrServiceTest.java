package com.hzj.aliyun.core.ocr.impl;

import com.aliyun.ocr_api20210707.models.RecognizeAllTextRequest;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextResponse;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextResponseBody;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralStructureRequest;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralStructureResponse;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralStructureResponseBody;
import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeAllTextParam;
import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeAllTextResult;
import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeGeneralStructureParam;
import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeGeneralStructureResult;
import com.hzj.aliyun.provider.aliyun.ocr.AliyunOcrRuntimeConfigProvider;
import com.hzj.aliyun.provider.aliyun.ocr.entity.AliyunOcrRuntimeConfig;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrCoordinate;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrInternationalBusinessLicenseCountry;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrInternationalIdCardCountry;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrLanguage;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrType;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 阿里云 OCR 服务测试。
 */
class DefaultAliyunOcrServiceTest {

    private AliyunOcrRuntimeConfigProvider runtimeConfigProvider(AliyunOcrRuntimeConfig config) {
        AliyunOcrRuntimeConfigProvider provider = mock(AliyunOcrRuntimeConfigProvider.class);
        when(provider.getConfig()).thenReturn(config);
        return provider;
    }

    @Test
    void shouldMapRecognizeAllTextRequestAndResponse() throws Exception {
        AliyunOcrRuntimeConfig runtimeConfig = new AliyunOcrRuntimeConfig();
        runtimeConfig.setDefaultType(AliyunOcrType.ADVANCED);
        AliyunOcrRuntimeConfigProvider provider = runtimeConfigProvider(runtimeConfig);

        RecognizeAllTextResponseBody body = new RecognizeAllTextResponseBody()
                .setCode("200")
                .setMessage("success")
                .setRequestId("request-id")
                .setData(new RecognizeAllTextResponseBody.RecognizeAllTextResponseBodyData()
                        .setHeight(1000)
                        .setWidth(2000)
                        .setSubImageCount(1)
                        .setContent("识别文本")
                        .setSubImages(Collections.singletonList(
                                new RecognizeAllTextResponseBody.RecognizeAllTextResponseBodyDataSubImages()
                                        .setSubImageId(0)
                                        .setAngle(0)
                                        .setType("Advanced")
                                        .setBlockInfo(
                                                new RecognizeAllTextResponseBody
                                                        .RecognizeAllTextResponseBodyDataSubImagesBlockInfo()
                                                        .setBlockCount(1)
                                                        .setBlockDetails(Collections.singletonList(
                                                                new RecognizeAllTextResponseBody
                                                                        .RecognizeAllTextResponseBodyDataSubImagesBlockInfoBlockDetails()
                                                                        .setBlockId(1)
                                                                        .setBlockContent("发票代码")
                                                                        .setBlockConfidence(99)))))));
        com.aliyun.ocr_api20210707.Client client = mock(com.aliyun.ocr_api20210707.Client.class);
        when(client.recognizeAllTextWithOptions(any(RecognizeAllTextRequest.class), any()))
                .thenReturn(new RecognizeAllTextResponse().setBody(body));

        DefaultAliyunOcrService service = new DefaultAliyunOcrService(provider, client);
        AliyunOcrRecognizeAllTextResult result = service.recognizeAllText(
                AliyunOcrRecognizeAllTextParam.builder()
                        .url("https://example.com/invoice.png")
                        .type(AliyunOcrType.INVOICE)
                        .build());

        assertThat(result.getCode()).isEqualTo("200");
        assertThat(result.getRequestId()).isEqualTo("request-id");
        assertThat(result.getHeight()).isEqualTo(1000);
        assertThat(result.getWidth()).isEqualTo(2000);
        assertThat(result.getContent()).isEqualTo("识别文本");
        assertThat(result.getSubImages()).hasSize(1);
        assertThat(result.getSubImages().get(0).getBlockCount()).isEqualTo(1);
        assertThat(result.getSubImages().get(0).getBlocks()).hasSize(1);
        assertThat(result.getSubImages().get(0).getBlocks().get(0).getBlockContent()).isEqualTo("发票代码");
        assertThat(result.getSubImages().get(0).getBlocks().get(0).getBlockConfidence()).isEqualTo(99);

        ArgumentCaptor<RecognizeAllTextRequest> captor = ArgumentCaptor.forClass(RecognizeAllTextRequest.class);
        verify(client).recognizeAllTextWithOptions(captor.capture(), any());
        assertThat(captor.getValue().getType()).isEqualTo(AliyunOcrType.INVOICE.getCode());
        assertThat(captor.getValue().getUrl()).isEqualTo("https://example.com/invoice.png");
        assertThat(captor.getValue().getAdvancedConfig()).isNull();
    }

    @Test
    void shouldFallbackToRuntimeConfigAndApplyAdvancedConfig() throws Exception {
        AliyunOcrRuntimeConfig runtimeConfig = new AliyunOcrRuntimeConfig();
        runtimeConfig.setDefaultType(AliyunOcrType.ADVANCED);
        runtimeConfig.setOutputTable(true);
        runtimeConfig.setOutputCoordinate(AliyunOcrCoordinate.RECTANGLE);
        AliyunOcrRuntimeConfigProvider provider = runtimeConfigProvider(runtimeConfig);

        com.aliyun.ocr_api20210707.Client client = mock(com.aliyun.ocr_api20210707.Client.class);
        RecognizeAllTextResponseBody body = new RecognizeAllTextResponseBody()
                .setCode("200")
                .setData(new RecognizeAllTextResponseBody.RecognizeAllTextResponseBodyData()
                        .setHeight(100)
                        .setWidth(100));
        when(client.recognizeAllTextWithOptions(any(RecognizeAllTextRequest.class), any()))
                .thenReturn(new RecognizeAllTextResponse().setBody(body));

        DefaultAliyunOcrService service = new DefaultAliyunOcrService(provider, client);
        service.recognizeAllText(AliyunOcrRecognizeAllTextParam.builder()
                .body(new ByteArrayInputStream(new byte[]{1, 2, 3}))
                .build());

        ArgumentCaptor<RecognizeAllTextRequest> captor = ArgumentCaptor.forClass(RecognizeAllTextRequest.class);
        verify(client).recognizeAllTextWithOptions(captor.capture(), any());
        RecognizeAllTextRequest request = captor.getValue();
        assertThat(request.getType()).isEqualTo(AliyunOcrType.ADVANCED.getCode());
        assertThat(request.getOutputCoordinate()).isEqualTo(AliyunOcrCoordinate.RECTANGLE.getCode());
        assertThat(request.getBody()).isNotNull();
        assertThat(request.getUrl()).isNull();
        assertThat(request.getAdvancedConfig()).isNotNull();
        assertThat(request.getAdvancedConfig().getOutputTable()).isTrue();
    }

    @Test
    void shouldRejectWhenUrlAndBodyBothProvidedOrMissing() {
        AliyunOcrRuntimeConfigProvider provider = runtimeConfigProvider(new AliyunOcrRuntimeConfig());
        DefaultAliyunOcrService service = new DefaultAliyunOcrService(provider,
                mock(com.aliyun.ocr_api20210707.Client.class));

        assertThatThrownBy(() -> service.recognizeAllText(
                AliyunOcrRecognizeAllTextParam.builder().build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("二选一");

        assertThatThrownBy(() -> service.recognizeAllText(AliyunOcrRecognizeAllTextParam.builder()
                .url("https://example.com/a.png")
                .body(new ByteArrayInputStream(new byte[]{1}))
                .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("二选一");
    }

    @Test
    void shouldMapSubConfigEnumsToSdkRequest() throws Exception {
        AliyunOcrRuntimeConfigProvider provider = runtimeConfigProvider(new AliyunOcrRuntimeConfig());
        com.aliyun.ocr_api20210707.Client client = mock(com.aliyun.ocr_api20210707.Client.class);
        RecognizeAllTextResponseBody body = new RecognizeAllTextResponseBody()
                .setCode("200")
                .setData(new RecognizeAllTextResponseBody.RecognizeAllTextResponseBodyData());
        when(client.recognizeAllTextWithOptions(any(RecognizeAllTextRequest.class), any()))
                .thenReturn(new RecognizeAllTextResponse().setBody(body));

        DefaultAliyunOcrService service = new DefaultAliyunOcrService(provider, client);
        service.recognizeAllText(AliyunOcrRecognizeAllTextParam.builder()
                .url("https://example.com/a.png")
                .type(AliyunOcrType.MULTI_LANG)
                .outputCoordinate(AliyunOcrCoordinate.POINTS)
                .languages(Arrays.asList(AliyunOcrLanguage.ENGLISH, AliyunOcrLanguage.CHINESE))
                .internationalIdCardCountry(AliyunOcrInternationalIdCardCountry.VIETNAM)
                .internationalBusinessLicenseCountry(AliyunOcrInternationalBusinessLicenseCountry.KOREA)
                .build());

        ArgumentCaptor<RecognizeAllTextRequest> captor = ArgumentCaptor.forClass(RecognizeAllTextRequest.class);
        verify(client).recognizeAllTextWithOptions(captor.capture(), any());
        RecognizeAllTextRequest request = captor.getValue();
        assertThat(request.getMultiLanConfig().getLanguages()).isEqualTo("eng,chn");
        assertThat(request.getInternationalIdCardConfig().getCountry()).isEqualTo("Vietnam");
        assertThat(request.getInternationalBusinessLicenseConfig().getCountry()).isEqualTo("Korea");
        assertThat(request.getOutputCoordinate()).isEqualTo("points");
    }

    @Test
    void shouldExposeEnumCodesMatchingAliyunValues() {
        assertThat(AliyunOcrType.fromCode("IdCard")).isEqualTo(AliyunOcrType.ID_CARD);
        assertThat(AliyunOcrType.fromCode("MultiLang")).isEqualTo(AliyunOcrType.MULTI_LANG);
        assertThat(AliyunOcrType.fromCode("not-exist")).isNull();
        assertThat(AliyunOcrCoordinate.fromCode("RECTANGLE")).isEqualTo(AliyunOcrCoordinate.RECTANGLE);
        assertThat(AliyunOcrCoordinate.isSupported("circle")).isFalse();
        assertThat(AliyunOcrCoordinate.isSupported("points")).isTrue();
        assertThat(AliyunOcrLanguage.fromCode("chn")).isEqualTo(AliyunOcrLanguage.CHINESE);
        assertThat(AliyunOcrInternationalIdCardCountry.fromCode("Vietnam"))
                .isEqualTo(AliyunOcrInternationalIdCardCountry.VIETNAM);
        assertThat(AliyunOcrInternationalBusinessLicenseCountry.fromCode("India"))
                .isEqualTo(AliyunOcrInternationalBusinessLicenseCountry.INDIA);
    }

    @Test
    void shouldMapRecognizeGeneralStructureRequestAndResponse() throws Exception {
        AliyunOcrRuntimeConfigProvider provider = runtimeConfigProvider(new AliyunOcrRuntimeConfig());

        Map<String, Object> kvData = new HashMap<>();
        kvData.put("姓名", "张三");
        kvData.put("开票日期", "2026年01月01日");
        RecognizeGeneralStructureResponseBody body = new RecognizeGeneralStructureResponseBody()
                .setRequestId("request-id")
                .setData(new RecognizeGeneralStructureResponseBody.RecognizeGeneralStructureResponseBodyData()
                        .setHeight(2000)
                        .setWidth(1000)
                        .setSubImageCount(1)
                        .setSubImages(Collections.singletonList(
                                new RecognizeGeneralStructureResponseBody
                                        .RecognizeGeneralStructureResponseBodyDataSubImages()
                                        .setSubImageId(0)
                                        .setAngle(0)
                                        .setKvInfo(new RecognizeGeneralStructureResponseBody
                                                .RecognizeGeneralStructureResponseBodyDataSubImagesKvInfo()
                                                .setKvCount(2)
                                                .setData(kvData)))));
        com.aliyun.ocr_api20210707.Client client = mock(com.aliyun.ocr_api20210707.Client.class);
        when(client.recognizeGeneralStructureWithOptions(any(RecognizeGeneralStructureRequest.class), any()))
                .thenReturn(new RecognizeGeneralStructureResponse().setBody(body));

        DefaultAliyunOcrService service = new DefaultAliyunOcrService(provider, client);
        AliyunOcrRecognizeGeneralStructureResult result = service.recognizeGeneralStructure(
                AliyunOcrRecognizeGeneralStructureParam.builder()
                        .url("https://example.com/receipt.png")
                        .keys(Arrays.asList("姓名", "开票日期"))
                        .build());

        assertThat(result.getRequestId()).isEqualTo("request-id");
        assertThat(result.getSubImages()).hasSize(1);
        assertThat(result.getSubImages().get(0).getKvCount()).isEqualTo(2);
        assertThat(result.getSubImages().get(0).getKvData()).isEqualTo(kvData);

        ArgumentCaptor<RecognizeGeneralStructureRequest> captor =
                ArgumentCaptor.forClass(RecognizeGeneralStructureRequest.class);
        verify(client).recognizeGeneralStructureWithOptions(captor.capture(), any());
        assertThat(captor.getValue().getKeys()).containsExactly("姓名", "开票日期");
        assertThat(captor.getValue().getUrl()).isEqualTo("https://example.com/receipt.png");
    }

    @Test
    void shouldRejectTooManyKeys() {
        AliyunOcrRuntimeConfigProvider provider = runtimeConfigProvider(new AliyunOcrRuntimeConfig());
        DefaultAliyunOcrService service = new DefaultAliyunOcrService(provider,
                mock(com.aliyun.ocr_api20210707.Client.class));

        String[] keys = new String[31];
        Arrays.fill(keys, "key");

        assertThatThrownBy(() -> service.recognizeGeneralStructure(
                AliyunOcrRecognizeGeneralStructureParam.builder()
                        .url("https://example.com/receipt.png")
                        .keys(Arrays.asList(keys))
                        .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("30");
    }

    @Test
    void shouldRejectOverlongKey() {
        AliyunOcrRuntimeConfigProvider provider = runtimeConfigProvider(new AliyunOcrRuntimeConfig());
        DefaultAliyunOcrService service = new DefaultAliyunOcrService(provider,
                mock(com.aliyun.ocr_api20210707.Client.class));

        assertThatThrownBy(() -> service.recognizeGeneralStructure(
                AliyunOcrRecognizeGeneralStructureParam.builder()
                        .url("https://example.com/receipt.png")
                        .keys(Collections.singletonList("k".repeat(51)))
                        .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("50");
    }
}
