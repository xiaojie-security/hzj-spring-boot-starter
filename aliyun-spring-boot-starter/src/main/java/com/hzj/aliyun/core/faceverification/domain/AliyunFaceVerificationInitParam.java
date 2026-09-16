package com.hzj.aliyun.core.faceverification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * InitFaceVerify 发起认证请求参数。
 *
 * <p>该参数同时支持金融级实人认证的 H5 和 APP 接入场景。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunFaceVerificationInitParam {

    /** 认证场景 ID；为空时使用运行时配置默认值。 */
    private Long sceneId;

    /** 商户自定义请求流水号。 */
    private String outerOrderNo;

    /** 产品码；为空时使用运行时配置默认值。 */
    private String productCode;

    /** 真实姓名。 */
    private String certName;

    /** 证件号码。 */
    private String certNo;

    /** 证件类型；为空时使用运行时配置默认值。 */
    private String certType;

    /** 客户端采集的 MetaInfo。 */
    private String metaInfo;

    /** 认证结束后的页面跳转地址。 */
    private String returnUrl;

    /** 服务端认证结果回调地址。 */
    private String callbackUrl;

    /** 服务端回调校验令牌。 */
    private String callbackToken;

    /** 业务侧用户 ID。 */
    private String userId;

    /** 用户手机号。 */
    private String mobile;

    /** 用户 IP 地址。 */
    private String ip;

    /** 活体检测模式。 */
    private String model;

    /** 认证模式。 */
    private String mode;

    /** 认证链接类型。 */
    private String certifyUrlType;

    /** 认证链接样式。 */
    private String certifyUrlStyle;

    /** 用于比对的照片 Base64。 */
    private String faceContrastPicture;

    /** 用于比对的照片 URL。 */
    private String faceContrastPictureUrl;

    /** 授权 OSS 桶名称。 */
    private String ossBucketName;

    /** 授权 OSS 对象名称。 */
    private String ossObjectName;

    /** 用户生日。 */
    private String birthday;

    /** 证件有效期。 */
    private String validityDate;

    /** 是否开启读身份证。 */
    private String readImg;

    /** 适用人群类型。 */
    private String suitableType;

    /** 自定义 UI 地址。 */
    private String uiCustomUrl;

    /** 是否允许裁剪人脸。 */
    private String crop;

    /** 摄像头选择策略。 */
    private String cameraSelection;

    /** 是否开启美颜。 */
    private String enableBeauty;

    /** 是否开启多人脸检测。 */
    private String needMultiFaceCheck;

    /** 人脸安全检测输出配置。 */
    private String faceGuardOutput;

    /** APP 质量检测配置。 */
    private String appQualityCheck;

    /** H5 降级确认按钮配置。 */
    private String h5DegradeConfirmBtn;

    /** 流程优先级。 */
    private String procedurePriority;

    /** 生僻字处理配置。 */
    private String rarelyCharacters;

    /** 是否返回视频证据。 */
    private String videoEvidence;

    /** 个性化自愿内容。 */
    private String voluntaryCustomizedContent;

    /** 加密类型。 */
    private String encryptType;

    /** 外部认证 ID。 */
    private String authId;
}
