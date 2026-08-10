package com.aliyun.core.imm.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * IMM 视频转码结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunTransCodeResult {

    /** 转码后视频文件的存储路径。 */
    private String transcodeVideoObjectName;

    /** 转码后视频封面文件的存储路径。 */
    private String transcodeVideoCoverObjectName;
}
