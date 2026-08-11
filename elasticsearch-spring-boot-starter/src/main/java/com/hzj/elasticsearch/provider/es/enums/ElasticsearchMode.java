package com.hzj.elasticsearch.provider.es.enums;

import org.springframework.util.StringUtils;

/**
 * Elasticsearch 节点连接模式。
 */
public enum ElasticsearchMode {

    /** 集群模式。 */
    CLUSTER("cluster"),

    /** 单节点模式。 */
    SINGLE_NODE("single_node");


    private final String value;

    ElasticsearchMode(String value) {
        this.value = value;
    }

    /**
     * 获取协议字符串。
     *
     * @return 协议字符串
     */
    public String getValue() {
        return value;
    }


    /**
     * 判断当前是否为集群模式。
     *
     * @return true 表示集群模式
     */
    public boolean isCluster() {
        return this == CLUSTER;
    }

    /**
     * 判断当前是否为单节点模式。
     *
     * @return true 表示单节点模式
     */
    public boolean isSingleNode() {
        return this == SINGLE_NODE;
    }

    /**
     * 将字符串转换为对应的枚举值（忽略大小写）。
     *
     * @param mode 模式字符串，支持 "cluster" 或 "single_node"
     * @return 对应的枚举值
     * @throws IllegalArgumentException 如果字符串为空或无法识别
     */
    public static ElasticsearchMode of(String mode) {
        if (!StringUtils.hasText(mode)) {
            throw new IllegalArgumentException("ElasticsearchMode 不能为空");
        }

        String trimmed = mode.trim().toLowerCase();

        for (ElasticsearchMode enumValue : values()) {
            if (enumValue.value.equals(trimmed)) {
                return enumValue;
            }
        }

        throw new IllegalArgumentException(
                String.format("无效的 ElasticsearchMode: '%s'，支持的值: cluster, single_node", mode)
        );
    }
}
