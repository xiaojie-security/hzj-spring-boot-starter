package com.aliyun.core.imm;

import com.aliyun.core.imm.domain.AliyunTransCodeResult;

public interface AliyunImmService {

    /**
     * 执行视频转码任务。
     *
     * <p>对指定路径的视频文件进行转码处理，并同步提取首帧封面图。</p>
     *
     * @param bucket OSS 桶名称
     * @param objectName 视频文件在 OSS 中的存储路径（不包含桶名）
     * @return 转码结果，转码文件的objectname
     */
    String transcode(String bucket, String objectName);

}
