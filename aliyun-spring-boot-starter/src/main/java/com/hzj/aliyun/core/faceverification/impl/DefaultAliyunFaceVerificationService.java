package com.hzj.aliyun.core.faceverification.impl;

import com.aliyun.cloudauth20190307.models.ContrastFaceVerifyAdvanceRequest;
import com.aliyun.cloudauth20190307.models.ContrastFaceVerifyRequest;
import com.aliyun.cloudauth20190307.models.ContrastFaceVerifyResponse;
import com.aliyun.cloudauth20190307.models.DescribeFaceVerifyRequest;
import com.aliyun.cloudauth20190307.models.DescribeFaceVerifyResponse;
import com.aliyun.cloudauth20190307.models.InitFaceVerifyRequest;
import com.aliyun.cloudauth20190307.models.InitFaceVerifyResponse;
import com.hzj.aliyun.core.faceverification.AliyunFaceVerificationService;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationContrastParam;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationContrastResult;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationDescribeParam;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationDescribeResult;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationInitParam;
import com.hzj.aliyun.core.faceverification.domain.AliyunFaceVerificationInitResult;
import com.hzj.aliyun.provider.aliyun.faceverification.AliyunFaceVerificationRuntimeConfigProvider;
import com.hzj.aliyun.provider.aliyun.faceverification.entity.AliyunFaceVerificationRuntimeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云金融级实人认证默认实现。
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultAliyunFaceVerificationService implements AliyunFaceVerificationService {

    private final AliyunFaceVerificationRuntimeConfigProvider configProvider;

    private final com.aliyun.cloudauth20190307.Client client;

    @Override
    public AliyunFaceVerificationInitResult initFaceVerify(AliyunFaceVerificationInitParam param) {
        requireParam(param, "InitFaceVerify 参数");
        AliyunFaceVerificationRuntimeConfig config = getRuntimeConfig();
        Long sceneId = resolveSceneId(param.getSceneId(), config, "InitFaceVerify");
        String productCode = resolveText(param.getProductCode(), config.getInitProductCode(),
                "InitFaceVerify productCode");
        requireText(param.getOuterOrderNo(), "InitFaceVerify outerOrderNo");

        InitFaceVerifyRequest request = new InitFaceVerifyRequest()
                .setSceneId(sceneId)
                .setOuterOrderNo(param.getOuterOrderNo())
                .setProductCode(productCode)
                .setCertName(param.getCertName())
                .setCertNo(param.getCertNo())
                .setCertType(resolveText(param.getCertType(), config.getCertType(), "InitFaceVerify certType"))
                .setMetaInfo(param.getMetaInfo())
                .setReturnUrl(param.getReturnUrl())
                .setCallbackUrl(param.getCallbackUrl())
                .setCallbackToken(param.getCallbackToken())
                .setUserId(param.getUserId())
                .setMobile(param.getMobile())
                .setIp(param.getIp())
                .setModel(param.getModel())
                .setMode(param.getMode())
                .setCertifyUrlType(param.getCertifyUrlType())
                .setCertifyUrlStyle(param.getCertifyUrlStyle())
                .setFaceContrastPicture(param.getFaceContrastPicture())
                .setFaceContrastPictureUrl(param.getFaceContrastPictureUrl())
                .setOssBucketName(param.getOssBucketName())
                .setOssObjectName(param.getOssObjectName())
                .setBirthday(param.getBirthday())
                .setValidityDate(param.getValidityDate())
                .setReadImg(param.getReadImg())
                .setSuitableType(param.getSuitableType())
                .setUiCustomUrl(param.getUiCustomUrl())
                .setCrop(param.getCrop())
                .setCameraSelection(param.getCameraSelection())
                .setEnableBeauty(param.getEnableBeauty())
                .setNeedMultiFaceCheck(param.getNeedMultiFaceCheck())
                .setFaceGuardOutput(param.getFaceGuardOutput())
                .setAppQualityCheck(param.getAppQualityCheck())
                .setH5DegradeConfirmBtn(param.getH5DegradeConfirmBtn())
                .setProcedurePriority(param.getProcedurePriority())
                .setRarelyCharacters(param.getRarelyCharacters())
                .setVideoEvidence(param.getVideoEvidence())
                .setVoluntaryCustomizedContent(param.getVoluntaryCustomizedContent())
                .setEncryptType(param.getEncryptType())
                .setAuthId(param.getAuthId());
        try {
            InitFaceVerifyResponse response = client.initFaceVerifyWithOptions(request, runtimeOptions());
            if (response == null || response.getBody() == null) {
                throw new IllegalStateException("InitFaceVerify 返回结果为空");
            }
            return toInitResult(response.getBody());
        } catch (Exception e) {
            log.error("DefaultAliyunFaceVerificationService.initFaceVerify 发起认证失败, outerOrderNo={}",
                    param.getOuterOrderNo(), e);
            throw new IllegalStateException("发起金融级实人认证失败", e);
        }
    }

    @Override
    public AliyunFaceVerificationDescribeResult describeFaceVerify(AliyunFaceVerificationDescribeParam param) {
        requireParam(param, "DescribeFaceVerify 参数");
        AliyunFaceVerificationRuntimeConfig config = getRuntimeConfig();
        Long sceneId = resolveSceneId(param.getSceneId(), config, "DescribeFaceVerify");
        requireText(param.getCertifyId(), "DescribeFaceVerify certifyId");
        String pictureReturnType = resolveText(param.getPictureReturnType(), config.getPictureReturnType(),
                "DescribeFaceVerify pictureReturnType");

        DescribeFaceVerifyRequest request = new DescribeFaceVerifyRequest()
                .setSceneId(sceneId)
                .setCertifyId(param.getCertifyId())
                .setPictureReturnType(pictureReturnType);
        try {
            DescribeFaceVerifyResponse response = client.describeFaceVerifyWithOptions(request, runtimeOptions());
            if (response == null || response.getBody() == null) {
                throw new IllegalStateException("DescribeFaceVerify 返回结果为空");
            }
            return toDescribeResult(response.getBody());
        } catch (Exception e) {
            log.error("DefaultAliyunFaceVerificationService.describeFaceVerify 获取认证结果失败, certifyId={}",
                    param.getCertifyId(), e);
            throw new IllegalStateException("获取金融级实人认证结果失败", e);
        }
    }

    @Override
    public AliyunFaceVerificationContrastResult contrastFaceVerifyPhoto(
            AliyunFaceVerificationContrastParam param) {
        validateContrastParam(param, false);
        ContrastFaceVerifyRequest request = createContrastRequest(param);
        try {
            ContrastFaceVerifyResponse response = client.contrastFaceVerifyWithOptions(request, runtimeOptions());
            if (response == null || response.getBody() == null) {
                throw new IllegalStateException("ContrastFaceVerify 返回结果为空");
            }
            return toContrastResult(response.getBody());
        } catch (Exception e) {
            log.error("DefaultAliyunFaceVerificationService.contrastFaceVerifyPhoto 照片认证失败, outerOrderNo={}",
                    param.getOuterOrderNo(), e);
            throw new IllegalStateException("照片实人认证失败", e);
        }
    }

    @Override
    public AliyunFaceVerificationContrastResult contrastFaceVerifyVideo(
            AliyunFaceVerificationContrastParam param) {
        validateContrastParam(param, true);
        try {
            ContrastFaceVerifyResponse response;
            if (param.getFaceContrastFileObject() == null) {
                response = client.contrastFaceVerifyWithOptions(createContrastRequest(param), runtimeOptions());
            } else {
                response = client.contrastFaceVerifyAdvance(createContrastAdvanceRequest(param), runtimeOptions());
            }
            if (response == null || response.getBody() == null) {
                throw new IllegalStateException("ContrastFaceVerify 返回结果为空");
            }
            return toContrastResult(response.getBody());
        } catch (Exception e) {
            log.error("DefaultAliyunFaceVerificationService.contrastFaceVerifyVideo 视频认证失败, outerOrderNo={}",
                    param.getOuterOrderNo(), e);
            throw new IllegalStateException("视频实人认证失败", e);
        }
    }

    @Override
    public AliyunFaceVerificationContrastResult contrastFaceVerify(
            AliyunFaceVerificationContrastParam param) {
        requireParam(param, "ContrastFaceVerify 参数");
        if (param.getFaceContrastFileObject() != null || hasText(param.getFaceContrastFile())) {
            return contrastFaceVerifyVideo(param);
        }
        return contrastFaceVerifyPhoto(param);
    }

    private ContrastFaceVerifyRequest createContrastRequest(AliyunFaceVerificationContrastParam param) {
        AliyunFaceVerificationRuntimeConfig config = getRuntimeConfig();
        return new ContrastFaceVerifyRequest()
                .setSceneId(resolveSceneId(param.getSceneId(), config, "ContrastFaceVerify"))
                .setOuterOrderNo(param.getOuterOrderNo())
                .setProductCode(resolveText(param.getProductCode(), config.getContrastProductCode(),
                        "ContrastFaceVerify productCode"))
                .setCertName(param.getCertName())
                .setCertNo(param.getCertNo())
                .setCertType(resolveText(param.getCertType(), config.getCertType(), "ContrastFaceVerify certType"))
                .setCertifyId(param.getCertifyId())
                .setCrop(param.getCrop())
                .setDeviceToken(param.getDeviceToken())
                .setEncryptType(param.getEncryptType())
                .setFaceContrastFile(param.getFaceContrastFile())
                .setFaceContrastPicture(param.getFaceContrastPicture())
                .setFaceContrastPictureUrl(param.getFaceContrastPictureUrl())
                .setIp(param.getIp())
                .setMobile(param.getMobile())
                .setModel(param.getModel())
                .setOssBucketName(param.getOssBucketName())
                .setOssObjectName(param.getOssObjectName())
                .setUserId(param.getUserId());
    }

    private ContrastFaceVerifyAdvanceRequest createContrastAdvanceRequest(
            AliyunFaceVerificationContrastParam param) {
        AliyunFaceVerificationRuntimeConfig config = getRuntimeConfig();
        return new ContrastFaceVerifyAdvanceRequest()
                .setSceneId(resolveSceneId(param.getSceneId(), config, "ContrastFaceVerify"))
                .setOuterOrderNo(param.getOuterOrderNo())
                .setProductCode(resolveText(param.getProductCode(), config.getContrastProductCode(),
                        "ContrastFaceVerify productCode"))
                .setCertName(param.getCertName())
                .setCertNo(param.getCertNo())
                .setCertType(resolveText(param.getCertType(), config.getCertType(), "ContrastFaceVerify certType"))
                .setCertifyId(param.getCertifyId())
                .setCrop(param.getCrop())
                .setDeviceToken(param.getDeviceToken())
                .setEncryptType(param.getEncryptType())
                .setFaceContrastFileObject(param.getFaceContrastFileObject())
                .setFaceContrastPicture(param.getFaceContrastPicture())
                .setFaceContrastPictureUrl(param.getFaceContrastPictureUrl())
                .setIp(param.getIp())
                .setMobile(param.getMobile())
                .setModel(param.getModel())
                .setOssBucketName(param.getOssBucketName())
                .setOssObjectName(param.getOssObjectName())
                .setUserId(param.getUserId());
    }

    private void validateContrastParam(AliyunFaceVerificationContrastParam param, boolean video) {
        requireParam(param, "ContrastFaceVerify 参数");
        requireText(param.getOuterOrderNo(), "ContrastFaceVerify outerOrderNo");
        requireText(param.getCertName(), "ContrastFaceVerify certName");
        requireText(param.getCertNo(), "ContrastFaceVerify certNo");
        if (video) {
            if (param.getFaceContrastFileObject() == null && !hasText(param.getFaceContrastFile())) {
                reject("ContrastFaceVerify 视频文件不能为空", param.getOuterOrderNo());
            }
            return;
        }
        boolean imageProvided = hasText(param.getFaceContrastPicture())
                || hasText(param.getFaceContrastPictureUrl())
                || hasText(param.getCertifyId())
                || (hasText(param.getOssBucketName()) && hasText(param.getOssObjectName()));
        if (!imageProvided) {
            reject("ContrastFaceVerify 照片材料不能为空", param.getOuterOrderNo());
        }
    }

    private AliyunFaceVerificationInitResult toInitResult(
            com.aliyun.cloudauth20190307.models.InitFaceVerifyResponseBody body) {
        com.aliyun.cloudauth20190307.models.InitFaceVerifyResponseBody.InitFaceVerifyResponseBodyResultObject result =
                body.getResultObject();
        return AliyunFaceVerificationInitResult.builder()
                .code(body.getCode())
                .message(body.getMessage())
                .requestId(body.getRequestId())
                .certifyId(result == null ? null : result.getCertifyId())
                .certifyUrl(result == null ? null : result.getCertifyUrl())
                .build();
    }

    private AliyunFaceVerificationDescribeResult toDescribeResult(
            com.aliyun.cloudauth20190307.models.DescribeFaceVerifyResponseBody body) {
        com.aliyun.cloudauth20190307.models.DescribeFaceVerifyResponseBody.DescribeFaceVerifyResponseBodyResultObject result =
                body.getResultObject();
        return AliyunFaceVerificationDescribeResult.builder()
                .code(body.getCode())
                .message(body.getMessage())
                .requestId(body.getRequestId())
                .deviceRisk(result == null ? null : result.getDeviceRisk())
                .deviceToken(result == null ? null : result.getDeviceToken())
                .identityInfo(result == null ? null : result.getIdentityInfo())
                .materialInfo(result == null ? null : result.getMaterialInfo())
                .passed(result == null ? null : result.getPassed())
                .subCode(result == null ? null : result.getSubCode())
                .success(result == null ? null : result.getSuccess())
                .userInfo(result == null ? null : result.getUserInfo())
                .build();
    }

    private AliyunFaceVerificationContrastResult toContrastResult(
            com.aliyun.cloudauth20190307.models.ContrastFaceVerifyResponseBody body) {
        com.aliyun.cloudauth20190307.models.ContrastFaceVerifyResponseBody.ContrastFaceVerifyResponseBodyResultObject result =
                body.getResultObject();
        return AliyunFaceVerificationContrastResult.builder()
                .code(body.getCode())
                .message(body.getMessage())
                .requestId(body.getRequestId())
                .certifyId(result == null ? null : result.getCertifyId())
                .identityInfo(result == null ? null : result.getIdentityInfo())
                .materialInfo(result == null ? null : result.getMaterialInfo())
                .passed(result == null ? null : result.getPassed())
                .subCode(result == null ? null : result.getSubCode())
                .build();
    }

    private AliyunFaceVerificationRuntimeConfig getRuntimeConfig() {
        AliyunFaceVerificationRuntimeConfig config = configProvider.getConfig();
        if (config == null) {
            throw new IllegalStateException("AliyunFaceVerificationRuntimeConfigProvider 返回的配置不能为空");
        }
        return config;
    }

    private Long resolveSceneId(Long sceneId, AliyunFaceVerificationRuntimeConfig config, String operation) {
        if (sceneId != null) {
            return sceneId;
        }
        if (config.getSceneId() != null) {
            return config.getSceneId();
        }
        reject(operation + " sceneId 不能为空", null);
        return null;
    }

    private String resolveText(String value, String defaultValue, String fieldName) {
        if (hasText(value)) {
            return value;
        }
        if (hasText(defaultValue)) {
            return defaultValue;
        }
        reject(fieldName + " 不能为空", null);
        return null;
    }

    private void requireParam(Object param, String fieldName) {
        if (param == null) {
            reject(fieldName + "不能为空", null);
        }
    }

    private void requireText(String value, String fieldName) {
        if (!hasText(value)) {
            reject(fieldName + "不能为空", null);
        }
    }

    private void reject(String message, String outerOrderNo) {
        log.error("DefaultAliyunFaceVerificationService.reject 认证参数无效, message={}, outerOrderNo={}",
                message, outerOrderNo);
        throw new IllegalArgumentException(message);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private com.aliyun.teautil.models.RuntimeOptions runtimeOptions() {
        return new com.aliyun.teautil.models.RuntimeOptions();
    }
}
