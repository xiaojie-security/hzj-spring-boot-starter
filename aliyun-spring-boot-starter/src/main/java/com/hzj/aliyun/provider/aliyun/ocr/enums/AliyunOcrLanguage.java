package com.hzj.aliyun.provider.aliyun.ocr.enums;

/**
 * OCR 多语言文字（Type=MultiLang）支持的语言。
 *
 * <p>对应 {@code MultiLanConfig.Languages} 参数，多个语言用英文逗号分隔。
 * 若可确认图片语言，建议只传一种语言，识别效果更好。</p>
 */
public enum AliyunOcrLanguage {

    /** 中文。 */
    CHINESE("chn"),

    /** 英文。 */
    ENGLISH("eng"),

    /** 日文。 */
    JAPANESE("ja"),

    /** 拉丁文。 */
    LATIN("lading"),

    /** 韩文。 */
    KOREAN("kor"),

    /** 手写。 */
    HANDWRITING("sx"),

    /** 泰文。 */
    THAI("tai"),

    /** 俄文。 */
    RUSSIAN("rus"),

    /** 马来文。 */
    MALAY("mys"),

    /** 印尼文。 */
    INDONESIAN("idn"),

    /** 越南文。 */
    VIETNAMESE("viet"),

    /** 乌克兰文。 */
    UKRAINIAN("ukr");

    /** 阿里云 Languages 参数取值。 */
    private final String code;

    /**
     * 创建语言。
     *
     * @param code 阿里云 Languages 参数取值
     */
    AliyunOcrLanguage(String code) {
        this.code = code;
    }

    /**
     * 获取阿里云 Languages 参数取值。
     *
     * @return Languages 参数取值
     */
    public String getCode() {
        return code;
    }

    /**
     * 根据阿里云 Languages 参数取值解析枚举。
     *
     * @param code Languages 参数取值
     * @return 语言；无法匹配时返回 {@code null}
     */
    public static AliyunOcrLanguage fromCode(String code) {
        if (code == null) {
            return null;
        }
        for (AliyunOcrLanguage language : values()) {
            if (language.code.equalsIgnoreCase(code)) {
                return language;
            }
        }
        return null;
    }
}
