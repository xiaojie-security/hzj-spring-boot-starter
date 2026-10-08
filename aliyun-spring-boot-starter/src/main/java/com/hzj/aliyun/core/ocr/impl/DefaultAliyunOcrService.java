package com.hzj.aliyun.core.ocr.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextRequest;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextResponse;
import com.aliyun.ocr_api20210707.models.RecognizeAllTextResponseBody;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralStructureRequest;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralStructureResponse;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralStructureResponseBody;
import com.hzj.aliyun.core.ocr.AliyunOcrService;
import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeAllTextParam;
import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeAllTextResult;
import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeGeneralStructureParam;
import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeGeneralStructureResult;
import com.hzj.aliyun.provider.aliyun.ocr.AliyunOcrRuntimeConfigProvider;
import com.hzj.aliyun.provider.aliyun.ocr.entity.AliyunOcrRuntimeConfig;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrCoordinate;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrLanguage;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 阿里云 OCR 服务默认实现。
 *
 * <p>运行时配置中的识别策略作为接口级默认值，请求参数优先。</p>
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAliyunOcrService implements AliyunOcrService {

    /** 通用票证抽取接口 30 秒超时，SDK 默认读超时 10 秒，此处放宽到 60 秒。 */
    private static final int LLM_SOCKET_TIMEOUT_MILLIS = 60000;

    /** 通用票证抽取 Key 数量上限。 */
    private static final int MAX_KEYS_SIZE = 30;

    /** 单个 Key 长度上限。 */
    private static final int MAX_KEY_LENGTH = 50;

    private final AliyunOcrRuntimeConfigProvider configProvider;

    private final com.aliyun.ocr_api20210707.Client client;

    @Override
    public AliyunOcrRecognizeAllTextResult recognizeAllText(AliyunOcrRecognizeAllTextParam param) {
        if (param == null) {
            reject("RecognizeAllText 参数不能为空");
        }
        validateImageInput(param.getUrl(), param.getBody(), "RecognizeAllText");
        AliyunOcrRuntimeConfig config = getRuntimeConfig();
        RecognizeAllTextRequest request = createAllTextRequest(param, config);
        try {
            RecognizeAllTextResponse response = client.recognizeAllTextWithOptions(request, runtimeOptions());
            if (response == null || response.getBody() == null) {
                throw new IllegalStateException("RecognizeAllText 返回结果为空");
            }
            return toAllTextResult(response.getBody());
        } catch (Exception e) {
            log.error("DefaultAliyunOcrService.recognizeAllText 统一识别失败, type={}",
                    resolveType(param.getType(), config), e);
            throw new IllegalStateException("OCR 统一识别失败", e);
        }
    }

    @Override
    public AliyunOcrRecognizeGeneralStructureResult recognizeGeneralStructure(
            AliyunOcrRecognizeGeneralStructureParam param) {
        if (param == null) {
            reject("RecognizeGeneralStructure 参数不能为空");
        }
        validateImageInput(param.getUrl(), param.getBody(), "RecognizeGeneralStructure");
        validateKeys(param.getKeys());
        try {
            RecognizeGeneralStructureResponse response = client.recognizeGeneralStructureWithOptions(
                    new RecognizeGeneralStructureRequest()
                            .setUrl(param.getUrl())
                            .setBody(param.getBody())
                            .setKeys(param.getKeys()),
                    runtimeOptions(LLM_SOCKET_TIMEOUT_MILLIS));
            if (response == null || response.getBody() == null) {
                throw new IllegalStateException("RecognizeGeneralStructure 返回结果为空");
            }
            return toGeneralStructureResult(response.getBody());
        } catch (Exception e) {
            log.error("DefaultAliyunOcrService.recognizeGeneralStructure 通用票证抽取失败, keys={}",
                    param.getKeys(), e);
            throw new IllegalStateException("通用票证抽取失败", e);
        }
    }

    /**
     * 获取当前生效的 OCR 运行时配置。
     *
     * @return OCR 运行时配置
     */
    private AliyunOcrRuntimeConfig getRuntimeConfig() {
        AliyunOcrRuntimeConfig config = configProvider.getConfig();
        if (config == null) {
            throw new IllegalStateException("AliyunOcrRuntimeConfigProvider 返回的配置不能为空");
        }
        return config;
    }

    /**
     * 构建 RecognizeAllText 请求。
     *
     * <p>请求参数优先于运行时配置默认值，未显式设置的字段保持 {@code null} 由服务端按默认策略处理。</p>
     *
     * @param param 统一识别参数
     * @param config OCR 运行时配置
     * @return SDK 请求对象
     */
    private RecognizeAllTextRequest createAllTextRequest(AliyunOcrRecognizeAllTextParam param,
                                                        AliyunOcrRuntimeConfig config) {
        AliyunOcrType type = resolveType(param.getType(), config);
        AliyunOcrCoordinate coordinate = resolveCoordinate(param.getOutputCoordinate(), config);
        RecognizeAllTextRequest request = new RecognizeAllTextRequest()
                .setUrl(param.getUrl())
                .setBody(param.getBody())
                .setType(type == null ? null : type.getCode())
                .setPageNo(param.getPageNo())
                .setOutputCoordinate(coordinate == null ? null : coordinate.getCode())
                .setOutputOricoord(resolveBoolean(param.getOutputOricoord(), config.getOutputOricoord()))
                .setOutputFigure(resolveBoolean(param.getOutputFigure(), config.getOutputFigure()))
                .setOutputQrcode(resolveBoolean(param.getOutputQrcode(), config.getOutputQrcode()))
                .setOutputBarCode(resolveBoolean(param.getOutputBarCode(), config.getOutputBarCode()))
                .setOutputStamp(resolveBoolean(param.getOutputStamp(), config.getOutputStamp()))
                .setOutputKVExcel(resolveBoolean(param.getOutputKVExcel(), config.getOutputKVExcel()));
        applyTypeSpecificConfig(request, param, config, type);
        return request;
    }

    /**
     * 按识别类型分发专有配置。
     *
     * <p>阿里云 RecognizeAllText 的各类专有配置仅在对应 Type 下合法，携带到其他 Type 会被服务端直接拒绝，
     * 例如 {@code Invalid input parameter: param (AdvancedConfig) is not valid for type (BusinessLicense)}。</p>
     *
     * <p>其中 AdvancedConfig 与 TableConfig 的取值来自运行时配置默认值，「非 null 即视为已设置」，
     * 因此不能以「参数是否为空」判断是否填充，必须按 Type 严格分发，否则默认类型之外的所有类型都会带上
     * 非法参数而调用失败。</p>
     *
     * @param request 请求对象
     * @param param 统一识别参数
     * @param config OCR 运行时配置
     * @param type 生效的识别类型
     */
    private void applyTypeSpecificConfig(RecognizeAllTextRequest request, AliyunOcrRecognizeAllTextParam param,
                                         AliyunOcrRuntimeConfig config, AliyunOcrType type) {
        if (type == null) {
            return;
        }
        switch (type) {
            case ADVANCED:
                applyAdvancedConfig(request, param, config);
                break;
            case TABLE:
                applyTableConfig(request, param, config);
                break;
            case ID_CARD:
                applyIdCardConfig(request, param);
                break;
            case INTERNATIONAL_ID_CARD:
                applyInternationalIdCardConfig(request, param);
                break;
            case INTERNATIONAL_BUSINESS_LICENSE:
                applyInternationalBusinessLicenseConfig(request, param);
                break;
            case MULTI_LANG:
                applyMultiLanConfig(request, param);
                break;
            default:
                break;
        }
    }

    /**
     * 填充通用文字识别高精版（Type=Advanced）专有配置。
     *
     * @param request 请求对象
     * @param param 统一识别参数
     * @param config OCR 运行时配置
     */
    private void applyAdvancedConfig(RecognizeAllTextRequest request, AliyunOcrRecognizeAllTextParam param,
                                     AliyunOcrRuntimeConfig config) {
        Boolean outputRow = resolveBoolean(param.getOutputRow(), config.getOutputRow());
        Boolean outputParagraph = resolveBoolean(param.getOutputParagraph(), config.getOutputParagraph());
        Boolean outputTable = resolveBoolean(param.getOutputTable(), config.getOutputTable());
        Boolean outputCharInfo = resolveBoolean(param.getOutputCharInfo(), config.getOutputCharInfo());
        Boolean outputTableExcel = resolveBoolean(param.getOutputTableExcel(), config.getOutputTableExcel());
        Boolean outputTableHtml = resolveBoolean(param.getOutputTableHtml(), config.getOutputTableHtml());
        Boolean lineLessTable = resolveBoolean(param.getLineLessTable(), config.getLineLessTable());
        Boolean handWritingTable = resolveBoolean(param.getHandWritingTable(), config.getHandWritingTable());
        if (outputRow == null && outputParagraph == null && outputTable == null && outputCharInfo == null
                && outputTableExcel == null && outputTableHtml == null && lineLessTable == null
                && handWritingTable == null) {
            return;
        }
        request.setAdvancedConfig(
                new RecognizeAllTextRequest.RecognizeAllTextRequestAdvancedConfig()
                        .setOutputRow(outputRow)
                        .setOutputParagraph(outputParagraph)
                        .setOutputTable(outputTable)
                        .setOutputCharInfo(outputCharInfo)
                        .setOutputTableExcel(outputTableExcel)
                        .setOutputTableHtml(outputTableHtml)
                        .setIsLineLessTable(lineLessTable)
                        .setIsHandWritingTable(handWritingTable));
    }

    /**
     * 填充身份证（Type=IdCard）质量检测配置。
     *
     * @param request 请求对象
     * @param param 统一识别参数
     */
    private void applyIdCardConfig(RecognizeAllTextRequest request, AliyunOcrRecognizeAllTextParam param) {
        if (param.getOutputIdCardQuality() == null) {
            return;
        }
        request.setIdCardConfig(new RecognizeAllTextRequest.RecognizeAllTextRequestIdCardConfig()
                .setOutputIdCardQuality(param.getOutputIdCardQuality()));
    }

    /**
     * 填充国际身份证（Type=InternationalIdCard）国家配置。
     *
     * @param request 请求对象
     * @param param 统一识别参数
     */
    private void applyInternationalIdCardConfig(RecognizeAllTextRequest request,
                                                AliyunOcrRecognizeAllTextParam param) {
        if (param.getInternationalIdCardCountry() == null) {
            return;
        }
        request.setInternationalIdCardConfig(
                new RecognizeAllTextRequest.RecognizeAllTextRequestInternationalIdCardConfig()
                        .setCountry(param.getInternationalIdCardCountry().getCode()));
    }

    /**
     * 填充国际企业执照（Type=InternationalBusinessLicense）国家配置。
     *
     * @param request 请求对象
     * @param param 统一识别参数
     */
    private void applyInternationalBusinessLicenseConfig(RecognizeAllTextRequest request,
                                                        AliyunOcrRecognizeAllTextParam param) {
        if (param.getInternationalBusinessLicenseCountry() == null) {
            return;
        }
        request.setInternationalBusinessLicenseConfig(
                new RecognizeAllTextRequest.RecognizeAllTextRequestInternationalBusinessLicenseConfig()
                        .setCountry(param.getInternationalBusinessLicenseCountry().getCode()));
    }

    /**
     * 填充多语言文字（Type=MultiLang）语言配置。
     *
     * @param request 请求对象
     * @param param 统一识别参数
     */
    private void applyMultiLanConfig(RecognizeAllTextRequest request, AliyunOcrRecognizeAllTextParam param) {
        if (CollUtil.isEmpty(param.getLanguages())) {
            return;
        }
        List<String> codes = new ArrayList<>(param.getLanguages().size());
        for (AliyunOcrLanguage language : param.getLanguages()) {
            if (language == null) {
                reject("RecognizeAllText languages 不能包含空元素");
            }
            codes.add(language.getCode());
        }
        request.setMultiLanConfig(new RecognizeAllTextRequest.RecognizeAllTextRequestMultiLanConfig()
                .setLanguages(String.join(",", codes)));
    }

    /**
     * 填充表格（Type=Table）专有配置。
     *
     * <p>Advanced 与 Table 存在同名参数，但由不同的子配置承载，此处仅在参数显式设置时填充。</p>
     *
     * @param request 请求对象
     * @param param 统一识别参数
     * @param config OCR 运行时配置
     */
    private void applyTableConfig(RecognizeAllTextRequest request, AliyunOcrRecognizeAllTextParam param,
                                  AliyunOcrRuntimeConfig config) {
        Boolean outputTableExcel = resolveBoolean(param.getOutputTableExcel(), config.getOutputTableExcel());
        Boolean outputTableHtml = resolveBoolean(param.getOutputTableHtml(), config.getOutputTableHtml());
        Boolean lineLessTable = resolveBoolean(param.getLineLessTable(), config.getLineLessTable());
        Boolean handWritingTable = resolveBoolean(param.getHandWritingTable(), config.getHandWritingTable());
        if (outputTableExcel == null && outputTableHtml == null && lineLessTable == null && handWritingTable == null) {
            return;
        }
        request.setTableConfig(new RecognizeAllTextRequest.RecognizeAllTextRequestTableConfig()
                .setOutputTableExcel(outputTableExcel)
                .setOutputTableHtml(outputTableHtml)
                .setIsLineLessTable(lineLessTable)
                .setIsHandWritingTable(handWritingTable));
    }

    /**
     * 解析图片类型，请求参数优先于运行时配置。
     *
     * @param type 请求参数图片类型
     * @param config OCR 运行时配置
     * @return 图片类型
     */
    private AliyunOcrType resolveType(AliyunOcrType type, AliyunOcrRuntimeConfig config) {
        return type != null ? type : config.getDefaultType();
    }

    /**
     * 解析坐标格式，请求参数优先于运行时配置。
     *
     * @param coordinate 请求参数坐标格式
     * @param config OCR 运行时配置
     * @return 坐标格式
     */
    private AliyunOcrCoordinate resolveCoordinate(AliyunOcrCoordinate coordinate, AliyunOcrRuntimeConfig config) {
        return coordinate != null ? coordinate : config.getOutputCoordinate();
    }

    /**
     * 解析布尔型配置，请求参数优先于运行时配置。
     *
     * @param value 请求参数值
     * @param defaultValue 运行时配置默认值
     * @return 生效值
     */
    private Boolean resolveBoolean(Boolean value, Boolean defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 校验图片入参，url 与 body 二选一。
     *
     * @param url 图片链接
     * @param body 图片二进制流
     * @param operation 操作名称
     */
    private void validateImageInput(String url, InputStream body, String operation) {
        boolean hasUrl = StrUtil.isNotBlank(url);
        if (hasUrl == (body != null)) {
            reject(operation + " url 与 body 必须二选一且不能同时为空");
        }
    }

    /**
     * 校验通用票证抽取 Key 数量与长度。
     *
     * @param keys 抽取 Key 列表
     */
    private void validateKeys(List<String> keys) {
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        if (keys.size() > MAX_KEYS_SIZE) {
            reject("RecognizeGeneralStructure keys 数量不能超过 " + MAX_KEYS_SIZE);
        }
        for (String key : keys) {
            if (key == null || key.length() > MAX_KEY_LENGTH) {
                reject("RecognizeGeneralStructure 单个 key 长度不能超过 " + MAX_KEY_LENGTH);
            }
        }
    }

    /**
     * 转换统一识别响应。
     *
     * @param body SDK 响应体
     * @return 统一识别结果
     */
    private AliyunOcrRecognizeAllTextResult toAllTextResult(RecognizeAllTextResponseBody body) {
        RecognizeAllTextResponseBody.RecognizeAllTextResponseBodyData data = body.getData();
        return AliyunOcrRecognizeAllTextResult.builder()
                .code(body.getCode())
                .message(body.getMessage())
                .requestId(body.getRequestId())
                .height(data == null ? null : data.getHeight())
                .width(data == null ? null : data.getWidth())
                .subImageCount(data == null ? null : data.getSubImageCount())
                .isMixedMode(data == null ? null : data.getIsMixedMode())
                .algoVersion(data == null ? null : data.getAlgoVersion())
                .kvExcelUrl(data == null ? null : data.getKvExcelUrl())
                .content(data == null ? null : data.getContent())
                .subImages(data == null ? null : toAllTextSubImages(data.getSubImages()))
                .build();
    }

    private List<AliyunOcrRecognizeAllTextResult.AliyunOcrSubImageResult> toAllTextSubImages(
            List<RecognizeAllTextResponseBody.RecognizeAllTextResponseBodyDataSubImages> subImages) {
        if (CollUtil.isEmpty(subImages)) {
            return null;
        }
        List<AliyunOcrRecognizeAllTextResult.AliyunOcrSubImageResult> results = new ArrayList<>(subImages.size());
        for (RecognizeAllTextResponseBody.RecognizeAllTextResponseBodyDataSubImages subImage : subImages) {
            AliyunOcrRecognizeAllTextResult.AliyunOcrSubImageResult result =
                    AliyunOcrRecognizeAllTextResult.AliyunOcrSubImageResult.builder()
                            .subImageId(subImage.getSubImageId())
                            .angle(subImage.getAngle())
                            .type(subImage.getType())
                            .subImageRect(subImage.getSubImageRect())
                            .subImagePoints(subImage.getSubImagePoints())
                            .build();
            applyBlockInfo(result, subImage.getBlockInfo());
            applyKvInfo(result, subImage.getKvInfo());
            applyParagraphInfo(result, subImage.getParagraphInfo());
            applyTableInfo(result, subImage.getTableInfo());
            results.add(result);
        }
        return results;
    }

    private void applyBlockInfo(AliyunOcrRecognizeAllTextResult.AliyunOcrSubImageResult result,
                                RecognizeAllTextResponseBody.RecognizeAllTextResponseBodyDataSubImagesBlockInfo blockInfo) {
        if (blockInfo == null) {
            return;
        }
        result.setBlockCount(blockInfo.getBlockCount());
        if (CollUtil.isEmpty(blockInfo.getBlockDetails())) {
            return;
        }
        result.setBlocks(mapList(blockInfo.getBlockDetails(),
                detail -> AliyunOcrRecognizeAllTextResult.AliyunOcrBlockResult.builder()
                        .blockId(detail.getBlockId())
                        .blockContent(detail.getBlockContent())
                        .blockConfidence(detail.getBlockConfidence())
                        .blockAngle(detail.getBlockAngle())
                        .blockRect(detail.getBlockRect())
                        .blockPoints(detail.getBlockPoints())
                        .build()));
    }

    private void applyKvInfo(AliyunOcrRecognizeAllTextResult.AliyunOcrSubImageResult result,
                             RecognizeAllTextResponseBody.RecognizeAllTextResponseBodyDataSubImagesKvInfo kvInfo) {
        if (kvInfo == null) {
            return;
        }
        result.setKvCount(kvInfo.getKvCount());
        result.setKvData(kvInfo.getKvDetails());
    }

    private void applyParagraphInfo(AliyunOcrRecognizeAllTextResult.AliyunOcrSubImageResult result,
                                    RecognizeAllTextResponseBody.RecognizeAllTextResponseBodyDataSubImagesParagraphInfo paragraphInfo) {
        if (paragraphInfo == null) {
            return;
        }
        result.setParagraphCount(paragraphInfo.getParagraphCount());
        if (CollUtil.isEmpty(paragraphInfo.getParagraphDetails())) {
            return;
        }
        result.setParagraphs(mapList(paragraphInfo.getParagraphDetails(),
                detail -> AliyunOcrRecognizeAllTextResult.AliyunOcrParagraphResult.builder()
                        .paragraphId(detail.getParagraphId())
                        .paragraphContent(detail.getParagraphContent())
                        .blockList(detail.getBlockList())
                        .build()));
    }

    private void applyTableInfo(AliyunOcrRecognizeAllTextResult.AliyunOcrSubImageResult result,
                                RecognizeAllTextResponseBody.RecognizeAllTextResponseBodyDataSubImagesTableInfo tableInfo) {
        if (tableInfo == null) {
            return;
        }
        result.setTableCount(tableInfo.getTableCount());
        result.setTableHtml(tableInfo.getTableHtml());
        result.setTableExcel(tableInfo.getTableExcel());
    }

    /**
     * 转换通用票证抽取响应。
     *
     * @param body SDK 响应体
     * @return 通用票证抽取结果
     */
    private AliyunOcrRecognizeGeneralStructureResult toGeneralStructureResult(
            RecognizeGeneralStructureResponseBody body) {
        RecognizeGeneralStructureResponseBody.RecognizeGeneralStructureResponseBodyData data = body.getData();
        return AliyunOcrRecognizeGeneralStructureResult.builder()
                .code(body.getCode())
                .message(body.getMessage())
                .requestId(body.getRequestId())
                .height(data == null ? null : data.getHeight())
                .width(data == null ? null : data.getWidth())
                .subImageCount(data == null ? null : data.getSubImageCount())
                .subImages(data == null ? null : toGeneralStructureSubImages(data.getSubImages()))
                .build();
    }

    private List<AliyunOcrRecognizeGeneralStructureResult.AliyunOcrSubImageResult> toGeneralStructureSubImages(
            List<RecognizeGeneralStructureResponseBody.RecognizeGeneralStructureResponseBodyDataSubImages> subImages) {
        if (CollUtil.isEmpty(subImages)) {
            return null;
        }
        List<AliyunOcrRecognizeGeneralStructureResult.AliyunOcrSubImageResult> results =
                new ArrayList<>(subImages.size());
        for (RecognizeGeneralStructureResponseBody.RecognizeGeneralStructureResponseBodyDataSubImages subImage
                : subImages) {
            RecognizeGeneralStructureResponseBody.RecognizeGeneralStructureResponseBodyDataSubImagesKvInfo kvInfo =
                    subImage.getKvInfo();
            results.add(AliyunOcrRecognizeGeneralStructureResult.AliyunOcrSubImageResult.builder()
                    .subImageId(subImage.getSubImageId())
                    .angle(subImage.getAngle())
                    .kvCount(kvInfo == null ? null : kvInfo.getKvCount())
                    .kvData(kvInfo == null ? null : kvInfo.getData())
                    .build());
        }
        return results;
    }

    private <S, T> List<T> mapList(List<S> source, Function<S, T> mapper) {
        List<T> target = new ArrayList<>(source.size());
        for (S item : source) {
            target.add(mapper.apply(item));
        }
        return target;
    }

    private void reject(String message) {
        log.error("DefaultAliyunOcrService.reject 参数无效, message={}", message);
        throw new IllegalArgumentException(message);
    }

    private com.aliyun.teautil.models.RuntimeOptions runtimeOptions() {
        return new com.aliyun.teautil.models.RuntimeOptions();
    }

    private com.aliyun.teautil.models.RuntimeOptions runtimeOptions(int socketTimeoutMillis) {
        com.aliyun.teautil.models.RuntimeOptions runtimeOptions =
                new com.aliyun.teautil.models.RuntimeOptions();
        runtimeOptions.setReadTimeout(socketTimeoutMillis);
        return runtimeOptions;
    }
}
