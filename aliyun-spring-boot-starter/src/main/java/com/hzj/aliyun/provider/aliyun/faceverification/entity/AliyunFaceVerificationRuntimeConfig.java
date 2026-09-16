package com.hzj.aliyun.provider.aliyun.faceverification.entity;

import lombok.Data;

/**
 * 金融级实人认证运行时业务配置。
 *
 * <p>场景、产品和返回数据策略支持运行时刷新，不参与客户端初始化。</p>
 */
@Data
public class AliyunFaceVerificationRuntimeConfig {

    /** 默认认证场景 ID。 */
    private Long sceneId;

    /** InitFaceVerify 默认产品码。 */
    private String initProductCode = "ID_PRO";

    /** ContrastFaceVerify 默认产品码。 */
    private String contrastProductCode = "ID_MIN";

    /** 默认证件类型。 */
    private String certType = "IDENTITY_CARD";

    /** DescribeFaceVerify 默认返回图片类型。 */
    private String pictureReturnType = "JPG";
}
