package com.hzj.aliyun.provider.aliyun.sms.entity;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.util.Map;

/**
 * 短信运行时业务配置。
 *
 * <p>短信签名支持运行时刷新。</p>
 */
@Data
public class AliyunSmsRuntimeConfig {

    /** 签名映射。 */
    private Map<String, String> signNames;

    /** 默认签名。 */
    private String defaultSignName;

    /**
     * 获取默认签名。
     *
     * @return 默认签名
     */
    public String getSignName() {
        return defaultSignName;
    }

    /**
     * 按业务标识获取签名。
     *
     * @param signKey 业务标识
     * @return 签名
     */
    public String getSignName(String signKey) {
        if (CollUtil.isEmpty(signNames)) {
            return defaultSignName;
        }
        return signNames.getOrDefault(signKey, defaultSignName);
    }
}
