package com.aliyun.provider.aliyun.sms.entity;

import cn.hutool.core.collection.CollUtil;
import com.aliyun.provider.aliyun.global.entity.AliyunBaseConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 短信配置。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AliyunSmsConfig extends AliyunBaseConfig {

    /** 短信服务接入点。 */
    private String endpoint;
    /** 短信服务区域。 */
    private String region;
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
