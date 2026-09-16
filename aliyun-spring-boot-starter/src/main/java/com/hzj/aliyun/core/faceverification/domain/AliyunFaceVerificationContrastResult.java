package com.hzj.aliyun.core.faceverification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ContrastFaceVerify 照片或视频认证结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunFaceVerificationContrastResult {

    /** 阿里云返回码。 */
    private String code;

    /** 阿里云返回消息。 */
    private String message;

    /** 阿里云请求 ID。 */
    private String requestId;

    /** 本次认证唯一标识。 */
    private String certifyId;

    /** 身份信息 JSON。 */
    private String identityInfo;

    /** 认证材料信息 JSON。 */
    private String materialInfo;

    /** 是否通过，T 表示通过，F 表示未通过。 */
    private String passed;

    /** 认证结果子码。 */
    private String subCode;
}
