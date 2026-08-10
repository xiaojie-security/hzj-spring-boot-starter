package com.hzj.aliyun.provider.aliyun.oss.enums;

/**
 * OSS 对象访问权限。
 */
public enum AliyunOssPermission {

    /** 私有，访问对象时需要签名 URL。 */
    PRIVATE,

    /** 公共读。 */
    PUBLIC_READ,

    /** 公共读写。 */
    PUBLIC_READ_WRITE
}
