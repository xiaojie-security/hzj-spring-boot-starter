package com.hzj.aliyun.core.oss.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * OSS POST直传签名返回VO
 * 对应 generatePostSignatureForOssUpload 返回Map字段
 */
@Data
public class AliyunPostUploadSignature {

    /**
     * 签名版本 OSS4‑HMAC‑SHA256
     */
    @JsonProperty("version")
    private String version;

    /**
     * base64编码后的policy策略
     */
    @JsonProperty("policy")
    private String policy;

    /**
     * x‑oss‑credential
     */
    @JsonProperty("x_oss_credential")
    private String xOssCredential;

    /**
     * x‑oss‑date UTC时间 yyyyMMdd'T'HHmmss'Z'
     */
    @JsonProperty("x_oss_date")
    private String xOssDate;

    /**
     * 计算出来的签名signature
     */
    @JsonProperty("signature")
    private String signature;

    /**
     * securityToken 临时sts token
     */
    @JsonProperty("security_token")
    private String securityToken;

    /**
     * 文件上传目录前缀
     */
    @JsonProperty("dir")
    private String dir;

    /**
     * 上传host地址 https://bucket.endpoint
     */
    @JsonProperty("host")
    private String host;

    /**
     * base64编码callback回调配置
     */
    @JsonProperty("callback")
    private String callback;

    /**
     * 业务生成ossId（雪花ID，作为x:oss_id表单自定义变量，回调会带回）
     */
    @JsonProperty("ossId")
    private String ossId;
}
