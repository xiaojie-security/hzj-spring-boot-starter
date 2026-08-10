package com.hzj.aliyun.core.imm.exception;

/**
 * IMM 业务异常。
 */
public class AliyunImmException extends RuntimeException {

    /** 创建无消息异常。 */
    public AliyunImmException() {
    }

    /**
     * 创建指定消息的异常。
     *
     * @param message 异常消息
     */
    public AliyunImmException(String message) {
        super(message);
    }

    /** 文件名称错误。 */
    public static final AliyunImmException FILE_NAME_ERROR = new AliyunImmException("文件名称错误");
}
