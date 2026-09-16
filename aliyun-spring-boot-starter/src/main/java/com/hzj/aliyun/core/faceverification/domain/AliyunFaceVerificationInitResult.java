package com.hzj.aliyun.core.faceverification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InitFaceVerify 发起认证结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunFaceVerificationInitResult {

    /** 阿里云返回码。 */
    private String code;

    /** 阿里云返回消息。 */
    private String message;

    /** 阿里云请求 ID。 */
    private String requestId;

    /** 本次认证唯一标识。 */
    private String certifyId;

    /** H5 认证链接。 */
    private String certifyUrl;
}
