package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转账场景上报信息。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTransferSceneReportInfo {

    /**
     * 场景信息内容。
     */
    private String infoContent;

    /**
     * 场景信息类型。
     */
    private String infoType;
}
