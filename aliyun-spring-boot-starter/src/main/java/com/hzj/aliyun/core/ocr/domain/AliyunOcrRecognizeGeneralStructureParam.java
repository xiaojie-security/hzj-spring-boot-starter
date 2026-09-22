package com.hzj.aliyun.core.ocr.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.List;

/**
 * RecognizeGeneralStructure（通用票证抽取）请求参数。
 *
 * <p>{@code url} 与 {@code body} 二选一，不可同时透传或同时为空；
 * {@code keys} 为空时由大模型自动判断需要抽取的 Key，Key 数量上限为 30，单个 Key 长度上限为 50。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliyunOcrRecognizeGeneralStructureParam {

    /** 图片链接，长度不超过 2048 字节，不支持 base64。 */
    private String url;

    /** 图片二进制文件，最大 10MB。 */
    private InputStream body;

    /** 需要抽取的所有 Key，为空时由大模型自动判断。 */
    private List<String> keys;
}
