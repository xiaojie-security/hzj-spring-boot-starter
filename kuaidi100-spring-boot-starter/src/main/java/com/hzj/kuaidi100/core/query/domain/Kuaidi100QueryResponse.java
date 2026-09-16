package com.hzj.kuaidi100.core.query.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

/**
 * 快递100实时查询和地图轨迹查询响应。
 */
@Data
public class Kuaidi100QueryResponse {

    /** 消息体。 */
    private String message;

    /** 快递单号。 */
    private String nu;

    /** 是否签收。 */
    private String ischeck;

    /** 快递公司编码。 */
    private String com;

    /** 通讯状态。 */
    private String status;

    /** 明细状态标记。 */
    private String condition;

    /** 基础物流状态。 */
    private String state;

    /** 地图轨迹链接。 */
    private String trailUrl;

    /** 预计到达时间。 */
    private String arrivalTime;

    /** 平均耗时。 */
    private String totalTime;

    /** 剩余耗时。 */
    private String remainTime;

    /** 预测准确率。 */
    private String probability;

    /** 是否存在环路。 */
    private Boolean isLoop;

    /** 物流轨迹数据。 */
    private JsonNode data;

    /** 路由信息。 */
    private JsonNode routeInfo;

    /** 快递员信息。 */
    private JsonNode courierInfo;

    /** 预测路线。 */
    private JsonNode predictedRoute;

    /** 原始响应。 */
    private JsonNode raw;
}
