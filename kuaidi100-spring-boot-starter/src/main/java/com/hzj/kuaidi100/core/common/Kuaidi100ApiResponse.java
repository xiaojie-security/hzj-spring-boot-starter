package com.hzj.kuaidi100.core.common;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * 快递100企业接口通用响应。
 *
 * <p>不同增值产品的 data 结构差异较大，因此保留为 JsonNode，调用方可以按官方接口文档转换为自己的领域对象。</p>
 */
@Data
public class Kuaidi100ApiResponse {

    /** 返回编码。 */
    private Integer code;

    /** 业务数据。 */
    private JsonNode data;

    /** 返回消息。 */
    private String message;

    /** 接口耗时。 */
    private Long time;

    /** 是否调用成功。 */
    private Boolean success;

    /** 原始响应，便于兼容快递公司扩展字段。 */
    private JsonNode raw;
}
