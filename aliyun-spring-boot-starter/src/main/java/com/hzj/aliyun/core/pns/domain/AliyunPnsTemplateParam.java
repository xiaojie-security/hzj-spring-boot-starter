package com.hzj.aliyun.core.pns.domain;

import lombok.Data;

/**
 * 号码认证服务模板参数。
 */
@Data
public class AliyunPnsTemplateParam {

    /** 验证码。 */
    private String code;
    /** 验证码有效时间。 */
    private String min;

    /**
     * 创建模板参数。
     *
     * @param code 验证码
     * @param min 有效时间
     */
    public AliyunPnsTemplateParam(String code, String min) {
        this.code = code;
        this.min = min;
    }

    /**
     * 创建动态验证码模板参数。
     */
    public AliyunPnsTemplateParam() {
        this.code = "##code##";
        this.min = "5";
    }

    /**
     * 创建指定验证码和有效时间的模板参数。
     *
     * @param code 验证码
     * @param min 有效时间
     * @return 模板参数
     */
    public static AliyunPnsTemplateParam create(String code, String min) {
        return new AliyunPnsTemplateParam(code, min);
    }

    /**
     * 创建指定验证码的模板参数。
     *
     * @param code 验证码
     * @return 模板参数
     */
    public static AliyunPnsTemplateParam create(String code) {
        return new AliyunPnsTemplateParam(code, "5");
    }

    /**
     * 创建默认模板参数。
     *
     * @return 默认模板参数
     */
    public static AliyunPnsTemplateParam create() {
        return new AliyunPnsTemplateParam();
    }
}
