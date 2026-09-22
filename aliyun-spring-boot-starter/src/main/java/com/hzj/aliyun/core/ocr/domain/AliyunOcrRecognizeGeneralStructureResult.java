package com.hzj.aliyun.core.ocr.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * RecognizeGeneralStructure（通用票证抽取）结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunOcrRecognizeGeneralStructureResult {

    /** 阿里云返回码；识别成功时通常不返回。 */
    private String code;

    /** 阿里云返回消息；识别成功时通常不返回。 */
    private String message;

    /** 阿里云请求 ID。 */
    private String requestId;

    /** 原图高度。 */
    private Integer height;

    /** 原图宽度。 */
    private Integer width;

    /** 图片包含的子图数量。 */
    private Integer subImageCount;

    /** 图片包含的子图信息。 */
    private List<AliyunOcrSubImageResult> subImages;

    /** 子图抽取结果。 */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AliyunOcrSubImageResult {

        /** 子图 ID，编号从 0 开始。 */
        private Integer subImageId;

        /** 子图顺时针旋转角度，范围 0～359 度。 */
        private Integer angle;

        /** 子图所包含结构化信息的键值对数量。 */
        private Integer kvCount;

        /** 结构化信息文字内容，字典类型，键为字段名称，值为识别结果。 */
        private Object kvData;
    }
}
