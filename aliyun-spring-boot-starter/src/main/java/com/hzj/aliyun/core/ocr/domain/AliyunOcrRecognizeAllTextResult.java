package com.hzj.aliyun.core.ocr.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * RecognizeAllText（OCR 统一识别）结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunOcrRecognizeAllTextResult {

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

    /** 是否混贴模式。 */
    private Boolean isMixedMode;

    /** 算法版本。 */
    private String algoVersion;

    /** 结构化信息导出的 Excel 文件链接，有效期一小时。 */
    private String kvExcelUrl;

    /** 图片内容，当 Type=General/Advanced 时按行返回识别文本。 */
    private String content;

    /** 图片包含的子图信息。 */
    private List<AliyunOcrSubImageResult> subImages;

    /** 子图识别结果。 */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AliyunOcrSubImageResult {

        /** 子图 ID，编号从 0 开始。 */
        private Integer subImageId;

        /** 子图顺时针旋转角度，范围 0～359 度。 */
        private Integer angle;

        /** 子图类型。 */
        private String type;

        /** 文字块数量。 */
        private Integer blockCount;

        /** 文字块信息。 */
        private List<AliyunOcrBlockResult> blocks;

        /** 结构化信息键值对数量。 */
        private Integer kvCount;

        /** 结构化信息文字内容，键为字段名称，值为字段对应的识别结果。 */
        private Object kvData;

        /** 段落数量。 */
        private Integer paragraphCount;

        /** 分段结果。 */
        private List<AliyunOcrParagraphResult> paragraphs;

        /** 表格数量。 */
        private Integer tableCount;

        /** 表格结果导出的 Html 内容。 */
        private String tableHtml;

        /** 表格结果导出的 Excel 文件链接，有效期一小时。 */
        private String tableExcel;

        /** 子图坐标 rect；仅当请求 outputCoordinate 不为空时返回。 */
        private Object subImageRect;

        /** 子图坐标 points；仅当请求 outputCoordinate 不为空时返回。 */
        private Object subImagePoints;
    }

    /** 文字块识别结果。 */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AliyunOcrBlockResult {

        /** 文字块 ID。 */
        private Integer blockId;

        /** 文字块内容。 */
        private String blockContent;

        /** 文字块置信度。 */
        private Integer blockConfidence;

        /** 文字块旋转角度。 */
        private Integer blockAngle;

        /** 文字块坐标 rect；仅当请求 outputCoordinate 不为空时返回。 */
        private Object blockRect;

        /** 文字块坐标 points；仅当请求 outputCoordinate 不为空时返回。 */
        private Object blockPoints;
    }

    /** 分段识别结果。 */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AliyunOcrParagraphResult {

        /** 段落 ID。 */
        private Integer paragraphId;

        /** 段落内容。 */
        private String paragraphContent;

        /** 段落包含的文字块 ID 列表。 */
        private List<Integer> blockList;
    }
}
