package com.hzj.aliyun.core.faceverification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

/**
 * ContrastFaceVerify 照片或视频实人认证参数。
 *
 * <p>照片认证使用照片、照片 URL、认证 ID 或 OSS 参数；视频认证可使用视频 URL，
 * 也可通过 {@link #faceContrastFileObject} 让 SDK 上传本地视频。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunFaceVerificationContrastParam {

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

    /** 已有认证唯一标识。 */
    private String certifyId;

    /** 是否允许裁剪人脸。 */
    private String crop;

    /** 设备风险识别 Token。 */
    private String deviceToken;

    /** 加密类型。 */
    private String encryptType;

    /** 视频文件 URL。 */
    private String faceContrastFile;

    /** 照片 Base64。 */
    private String faceContrastPicture;

    /** 照片 URL。 */
    private String faceContrastPictureUrl;

    /** 本地视频输入流；设置后使用 SDK 的高级视频上传接口。 */
    private InputStream faceContrastFileObject;

    /** 用户 IP 地址。 */
    private String ip;

    /** 用户手机号。 */
    private String mobile;

    /** 活体检测模式。 */
    private String model;

    /** 授权 OSS 桶名称。 */
    private String ossBucketName;

    /** 授权 OSS 对象名称。 */
    private String ossObjectName;

    /** 业务侧用户 ID。 */
    private String userId;
}
