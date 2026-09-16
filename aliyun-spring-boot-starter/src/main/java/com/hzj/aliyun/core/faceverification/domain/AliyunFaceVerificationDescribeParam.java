package com.hzj.aliyun.core.faceverification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DescribeFaceVerify 获取认证结果参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunFaceVerificationDescribeParam {

    /** 认证场景 ID；为空时使用运行时配置默认值。 */
    private Long sceneId;

    /** InitFaceVerify 返回的认证唯一标识。 */
    private String certifyId;

    /** 返回图片类型；为空时使用运行时配置默认值。 */
    private String pictureReturnType;
}
