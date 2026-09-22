package com.hzj.aliyun.provider.aliyun.ocr.entity;

import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrCoordinate;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrType;
import lombok.Data;

/**
 * OCR 运行时业务配置。
 *
 * <p>识别类型与输出策略支持运行时刷新，不参与客户端初始化；
 * 均为接口级默认值，请求参数显式设置时以请求参数为准。</p>
 */
@Data
public class AliyunOcrRuntimeConfig {

    /** 默认图片类型，对应 Type 参数。 */
    private AliyunOcrType defaultType = AliyunOcrType.ADVANCED;

    /** 默认返回坐标格式；为空表示不返回坐标。 */
    private AliyunOcrCoordinate outputCoordinate;

    /** 是否返回原图坐标信息；仅当 coordinate 不为空时有意义。 */
    private Boolean outputOricoord;

    /** 是否返回图案检测结果。 */
    private Boolean outputFigure;

    /** 是否返回二维码检测结果。 */
    private Boolean outputQrcode;

    /** 是否返回条形码检测结果。 */
    private Boolean outputBarCode;

    /** 是否返回印章检测结果。 */
    private Boolean outputStamp;

    /** 是否把结构化信息转成 Excel 文件链接。 */
    private Boolean outputKVExcel;

    /**
     * 通用文字识别高精版默认返回成行结果。
     */
    private Boolean outputRow;

    /** 通用文字识别高精版默认返回分段结果。 */
    private Boolean outputParagraph;

    /** 通用文字识别高精版默认返回表格识别结果。 */
    private Boolean outputTable;

    /** 通用文字识别高精版默认返回单字识别结果。 */
    private Boolean outputCharInfo;

    /** 是否将表格结果导出成 Excel 文件链接。 */
    private Boolean outputTableExcel;

    /** 是否将表格结果导出成 Html 结果。 */
    private Boolean outputTableHtml;

    /** 是否为无线表格。 */
    private Boolean lineLessTable;

    /** 是否为手写表格。 */
    private Boolean handWritingTable;
}
