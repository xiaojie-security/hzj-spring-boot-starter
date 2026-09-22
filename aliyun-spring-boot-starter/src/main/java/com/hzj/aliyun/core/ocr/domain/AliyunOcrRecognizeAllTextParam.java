package com.hzj.aliyun.core.ocr.domain;

import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrCoordinate;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrInternationalBusinessLicenseCountry;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrInternationalIdCardCountry;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrLanguage;
import com.hzj.aliyun.provider.aliyun.ocr.enums.AliyunOcrType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.List;

/**
 * RecognizeAllText（OCR 统一识别）请求参数。
 *
 * <p>{@code url} 与 {@code body} 二选一，不可同时透传或同时为空；
 * {@code type} 为必填参数，未设置时使用运行时配置默认值。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunOcrRecognizeAllTextParam {

    /** 图片链接，长度不超过 2048 字节，不支持 base64。 */
    private String url;

    /** 图片二进制文件，最大 10MB。 */
    private InputStream body;

    /** 图片类型，未设置时使用运行时配置默认值。 */
    private AliyunOcrType type;

    /** 指定识别的 PDF/OFD 页码。 */
    private Integer pageNo;

    /** 返回坐标格式；为空表示不返回坐标。 */
    private AliyunOcrCoordinate outputCoordinate;

    /** 是否需要返回原图坐标信息；仅当 outputCoordinate 不为空时有意义。 */
    private Boolean outputOricoord;

    /** 是否需要图案检测功能。 */
    private Boolean outputFigure;

    /** 是否需要二维码检测功能。 */
    private Boolean outputQrcode;

    /** 是否需要条形码检测功能。 */
    private Boolean outputBarCode;

    /** 是否需要印章检测功能。 */
    private Boolean outputStamp;

    /** 是否需要把结构化信息转成 Excel 文件链接。 */
    private Boolean outputKVExcel;

    /** 是否需要成行返回功能（Type=Advanced）。 */
    private Boolean outputRow;

    /** 是否需要分段功能（Type=Advanced）。 */
    private Boolean outputParagraph;

    /** 是否需要输出表格识别结果，包含单元格信息（Type=Advanced）。 */
    private Boolean outputTable;

    /** 是否需要输出单字识别结果（Type=Advanced）。 */
    private Boolean outputCharInfo;

    /** 是否将识别的表格结果导出成 Excel，并以文件链接形式返回。 */
    private Boolean outputTableExcel;

    /** 是否将识别的表格结果导出成 Html 格式结果，并以文件链接形式返回。 */
    private Boolean outputTableHtml;

    /** 是否为无线表格或表格只有横线没有竖线。 */
    private Boolean lineLessTable;

    /** 是否是手写表格。 */
    private Boolean handWritingTable;

    /** 身份证质量检测配置；仅 Type=IdCard 时生效。 */
    private Boolean outputIdCardQuality;

    /** 国际身份证国家；仅 Type=InternationalIdCard 时生效。 */
    private AliyunOcrInternationalIdCardCountry internationalIdCardCountry;

    /** 国际企业执照国家；仅 Type=InternationalBusinessLicense 时生效。 */
    private AliyunOcrInternationalBusinessLicenseCountry internationalBusinessLicenseCountry;

    /** 多语言文字支持的语言列表；仅 Type=MultiLang 时生效。 */
    private List<AliyunOcrLanguage> languages;
}
