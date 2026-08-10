package com.hzj.alipay.core.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电子回单状态查询参数。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AliPayEreceiptQueryParam {

    /**
     * 电子回单申请 ID。
     */
    private String fileId;
}
