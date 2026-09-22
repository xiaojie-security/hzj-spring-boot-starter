package com.hzj.aliyun.core.ocr;

import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeAllTextParam;
import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeAllTextResult;
import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeGeneralStructureParam;
import com.hzj.aliyun.core.ocr.domain.AliyunOcrRecognizeGeneralStructureResult;

/**
 * 阿里云 OCR 服务。
 */
public interface AliyunOcrService {

    /**
     * OCR 统一识别。
     *
     * <p>通过 {@code type} 指定图片类型，支持通用文字、个人卡证、发票等多种类型，
     * 无须更换接口。</p>
     *
     * @param param 统一识别参数
     * @return 统一识别结果
     */
    AliyunOcrRecognizeAllTextResult recognizeAllText(AliyunOcrRecognizeAllTextParam param);

    /**
     * 通用票证抽取。
     *
     * <p>针对 OCR 不支持的长尾票据，抽取名称、地址、开票日期等关键字段的结构化结果。</p>
     *
     * @param param 通用票证抽取参数
     * @return 通用票证抽取结果
     */
    AliyunOcrRecognizeGeneralStructureResult recognizeGeneralStructure(
            AliyunOcrRecognizeGeneralStructureParam param);
}
