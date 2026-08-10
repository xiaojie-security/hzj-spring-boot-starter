package com.hzj.aliyun.core.oss.exception;

/**
 * OSS 业务异常。
 */
public class AliyunOssException extends RuntimeException {

    /** 创建无消息异常。 */
    public AliyunOssException() {
    }

    /**
     * 创建指定消息的异常。
     *
     * @param message 异常消息
     */
    public AliyunOssException(String message) {
        super(message);
    }

    /** 系统异常。 */
    public static final AliyunOssException SYSTEM_ERROR = new AliyunOssException("系统异常");
    /** 上传文件路径不能为空。 */
    public static final AliyunOssException UPLOAD_FILE_PATH_NOT_NULL = new AliyunOssException("上传文件路径不能为空");
    /** 上传文件不能为空。 */
    public static final AliyunOssException UPLOAD_FILE_NOT_NULL = new AliyunOssException("上传文件不能为空");
    /** 保存文件路径不能为空。 */
    public static final AliyunOssException SAVE_FILE_PATH_NOT_NULL = new AliyunOssException("保存文件路径不能为空");
    /** 文件名称错误。 */
    public static final AliyunOssException FILE_NAME_ERROR = new AliyunOssException("文件名称错误");
    /** 文件下载失败。 */
    public static final AliyunOssException FILE_DOWNLOAD_ERROR = new AliyunOssException("文件下载到本地失败");
    /** 文件读取异常。 */
    public static final AliyunOssException FILE_READ_ERROR = new AliyunOssException("文件读取异常");
    /** 分片上传失败。 */
    public static final AliyunOssException UPLOAD_ERROR = new AliyunOssException("分片上传失败");
    /** 文件散列校验失败。 */
    public static final AliyunOssException FILE_HASH_ERROR = new AliyunOssException("文件散列校验失败");
}
