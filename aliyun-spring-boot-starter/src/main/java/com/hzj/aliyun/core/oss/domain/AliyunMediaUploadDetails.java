package com.hzj.aliyun.core.oss.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * OSS 媒体上传结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunMediaUploadDetails {

    /** 文件存储桶名。 */
    private String bucket;

    /** 文件存储路径。 */
    private String objectName;

    /** 原始文件名。 */
    private String originFileName;

    /** 最终处理后的文件名。 */
    private String finalFileName;

    /** 文件 MD5 校验值。 */
    private String md5;

    /** 文件内容类型。 */
    private String contentType;

    /** 文件所属的 OSS 地域。 */
    private String region;

    /** 文件访问域名。 */
    private String endpoint;

    /** 文件访问 URI。 */
    private String uri;
}
