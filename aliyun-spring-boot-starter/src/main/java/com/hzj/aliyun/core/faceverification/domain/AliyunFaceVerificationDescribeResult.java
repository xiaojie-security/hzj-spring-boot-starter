package com.hzj.aliyun.core.faceverification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DescribeFaceVerify 获取认证结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunFaceVerificationDescribeResult {

    /** 阿里云返回码。 */
    private String code;

    /** 阿里云返回消息。 */
    private String message;

    /** 阿里云请求 ID。 */
    private String requestId;

    /** 设备风险标签。 */
    private String deviceRisk;

    /** 设备 Token。 */
    private String deviceToken;

    /** 身份信息 JSON。 */
    private String identityInfo;

    /** 认证材料信息 JSON。 */
    private String materialInfo;

    /** 是否通过，T 表示通过，F 表示未通过。 */
    private String passed;

    /** 认证结果子码。 */
    private String subCode;

    /** 接口业务是否成功。 */
    private String success;

    /** 用户信息 JSON。 */
    private String userInfo;
}
